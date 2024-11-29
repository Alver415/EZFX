package com.ezfx.app.demo;

import com.ezfx.base.utils.EZFX;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.Closeable;
import java.util.concurrent.*;


public class ManagedContext implements Closeable {

	private final Context context;
	private final BlockingQueue<Evaluation> queue;
	private final BooleanProperty closed = new SimpleBooleanProperty(this, "closed", false);

	public static ManagedContext start(Context.Builder builder) throws InterruptedException, ExecutionException {
		CompletableFuture<ManagedContext> future = new CompletableFuture<>();
		EZFX.runOnPlatformThread(() -> {
			ManagedContext context = new ManagedContext(builder.build());
			future.complete(context);
			context.start();
		});
		return future.get();
	}

	private void start() {
		while (!closed.get()) {
			synchronized (closed) {
				if (closed.get()) continue;
				try {
					Evaluation evaluation = queue.poll(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
					try {
						if (evaluation == null) continue;
						evaluation.value.complete(context.eval(evaluation.source));
					} catch (PolyglotException e) {
						evaluation.value.completeExceptionally(e);
					}
				} catch (Exception e) {
					System.err.print(e.getMessage());
				}
			}
		}
	}

	private ManagedContext(Context context) {
		this.context = context;
		this.queue = new LinkedBlockingQueue<>();
	}

	// Blocking
	public Value eval(String languageId, CharSequence charSequence) throws ExecutionException, InterruptedException {
		return evalAsync(languageId, charSequence).get();
	}

	// Blocking
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

	public Context getContext() {
		return context;
	}

	@Override
	public void close() {
		synchronized (closed) {
			closed.set(true);
			context.close();
		}
	}


	private static final class Evaluation {
		private final Source source;
		private final CompletableFuture<Value> value;

		private Evaluation(Source source) {
			this.source = source;
			this.value = new CompletableFuture<>();
		}
	}
}