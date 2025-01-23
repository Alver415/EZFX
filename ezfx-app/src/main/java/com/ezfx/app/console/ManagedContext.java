package com.ezfx.app.console;

import com.ezfx.base.utils.EZFX;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.management.ExecutionEvent;
import org.graalvm.polyglot.management.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ezfx.base.utils.EZFX.runFX;


public class ManagedContext implements Closeable {

	private static final Logger log = LoggerFactory.getLogger(ManagedContext.class);
	private final Context context;

	private ManagedContext(Context context, String[] permittedLanguages) {
		this.context = context;
		this.queue = new LinkedBlockingQueue<>();
		this.languages.putAll(Arrays.stream(permittedLanguages).map(context.getEngine().getLanguages()::get)
				.collect(Collectors.toMap(Language::getId, Function.identity())));
	}

	public Context getContext() {
		return context;
	}

	//region Lifecycle
	private final BooleanProperty closed = new SimpleBooleanProperty(this, "closed", false);
	private void start() {
		while (!closed.get()) {
			synchronized (closed) {
				if (closed.get()) continue;
				try {
					Evaluation evaluation = queue.poll(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
					runFX(() -> {
						try {
							if (evaluation == null) return;
							Value result = context.eval(evaluation.source);
							evaluation.value.complete(result);
						} catch (PolyglotException e) {
							evaluation.value.completeExceptionally(e);
						}
					});
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void close() {
		synchronized (closed) {
			closed.set(true);
			context.close();
		}
	}
	//endregion Lifecycle
	//region Evaluation
	private final BlockingQueue<Evaluation> queue;

	private static final class Evaluation {
		private final Source source;
		private final CompletableFuture<Value> value;

		private Evaluation(Source source) {
			this.source = source;
			this.value = new CompletableFuture<>();
		}
	}

	public Value eval(String languageId, CharSequence charSequence) throws ExecutionException, InterruptedException {
		return evalAsync(languageId, charSequence).get();
	}

	public Value eval(Source source) throws ExecutionException, InterruptedException {
		return evalAsync(source).get();
	}

	public CompletableFuture<Value> evalAsync(String languageId, CharSequence charSequence) {
		return evalAsync(Source.create(languageId, charSequence));
	}

	public CompletableFuture<Value> evalAsync(Source source) {
		if (closed.get()) {
			throw new IllegalStateException("ManagedContext is closed.");
		}
		Evaluation evaluation = new Evaluation(source);
		if (!queue.offer(evaluation)) {
			throw new IllegalStateException("Failed to submit source");
		}
		return evaluation.value;
	}
	//endregion Evaluation
	// region Bindings
	public void putPolyglotMember(String key, Object value) {
		putMember(context.getPolyglotBindings(), key, value);
	}

	public void putMember(Language language, String key, Object value) {
		putMember(language.getId(), key, value);
	}

	public void putMember(String languageId, String key, Object value) {
		putMember(context.getBindings(languageId), key, value);
	}

	public void putMember(Value bindings, String key, Object value) {
		bindings.putMember(key, value);
	}

	public Value getPolyglotMember(String key) {
		return getMember(context.getPolyglotBindings(), key);
	}

	public Value getMember(Language language, String key) {
		return getMember(language.getId(), key);
	}

	public Value getMember(String languageId, String key) {
		return getMember(context.getBindings(languageId), key);
	}

	public Value getMember(Value binding, String key) {
		return binding.getMember(key);
	}
	// endregion Bindings
	//region Properties
	private final MapProperty<String, ContextBindings> bindings = new SimpleMapProperty<>(this, "bindings", FXCollections.observableHashMap());

	public MapProperty<String, ContextBindings> bindingsProperty() {
		return this.bindings;
	}

	public ObservableMap<String, ContextBindings> getBindings() {
		return this.bindingsProperty().getValue();
	}

	public void setBindings(ObservableMap<String, ContextBindings> value) {
		this.bindingsProperty().setValue(value);
	}

	private final MapProperty<String, Language> languages = new SimpleMapProperty<>(this, "languages", FXCollections.observableHashMap());

	public MapProperty<String, Language> languagesProperty() {
		return this.languages;
	}

	public ObservableMap<String, Language> getLanguages() {
		return this.languagesProperty().getValue();
	}

	public void setLanguages(ObservableMap<String, Language> value) {
		this.languagesProperty().setValue(value);
	}

	//endregion Properties
	//region Listeners
	public ExecutionListener onReturn(Consumer<ExecutionEvent> listener) {
		return ExecutionListener.newBuilder().onReturn(listener)
				.expressions(true)
				.statements(true)
				.roots(true)
				// If additional event data is collected then the peak performance overhead of execution listeners is significant.
				// It is not recommended to collect additional event data when running production workloads.
				.collectInputValues(true)
				.collectReturnValue(true)
				.collectExceptions(true)
				.attach(context.getEngine());
	}

	private ExecutionListener onEnter(Consumer<ExecutionEvent> listener) {
		return ExecutionListener.newBuilder().onEnter(listener)
				.expressions(true)
				.statements(true)
				.roots(true)
				// If additional event data is collected then the peak performance overhead of execution listeners is significant.
				// It is not recommended to collect additional event data when running production workloads.
				.collectInputValues(true)
				.collectReturnValue(true)
				.collectExceptions(true)
				.attach(context.getEngine());
	}

	//endregion Listeners
	//region Builder
	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private boolean allowAllAccess = false;
		private String[] permittedLanguages = new String[0];
		private InputStream in;
		private OutputStream out;
		private OutputStream err;

		private Builder() {
		}

		public Builder allowAllAccess(boolean allowAllAccess) {
			this.allowAllAccess = allowAllAccess;
			return this;
		}

		public Builder permittedLanguages(String... permittedLanguages) {
			this.permittedLanguages = permittedLanguages;
			return this;
		}

		public Builder in(InputStream in) {
			this.in = in;
			return this;
		}

		public Builder out(OutputStream out) {
			this.out = out;
			return this;
		}

		public Builder err(OutputStream err) {
			this.err = err;
			return this;
		}

		public ManagedContext build() throws ExecutionException, InterruptedException {
			Context.Builder builder = Context.newBuilder(permittedLanguages);
			builder.allowAllAccess(allowAllAccess);
			if (in != null) {
				builder.in(in);
			}
			if (out != null) {
				builder.out(out);
			}
			if (err != null) {
				builder.err(err);
			}
			Context context = builder.build();
			CompletableFuture<ManagedContext> future = new CompletableFuture<>();
			EZFX.runOnPlatformThread(() -> {
				ManagedContext managedContext = new ManagedContext(context, permittedLanguages);
				future.complete(managedContext);
				managedContext.start();
			});
			return future.get();
		}
	}

	//endregion Builder
}