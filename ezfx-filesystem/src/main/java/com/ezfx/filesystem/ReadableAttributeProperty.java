package com.ezfx.filesystem;

import com.ezfx.base.exception.UncheckedConsumer;
import com.ezfx.base.exception.UncheckedSupplier;
import com.ezfx.filesystem.utils.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import org.reactfx.EventSource;
import org.reactfx.Guard;
import org.reactfx.SuspendableEventStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class ReadableAttributeProperty<T, S>
		implements ObservableObjectValue<T>, ReadOnlyProperty<T> {

	private static final Logger log = LoggerFactory.getLogger(ReadableAttributeProperty.class);

	private final FileSystemFX fileSystem;
	private final EventSource<Void> syncRequests = new EventSource<>();
	private final EventSource<Void> flushRequests = new EventSource<>();
	private final SuspendableEventStream<Void> syncStream = syncRequests.suppressible();
	private final SuspendableEventStream<Void> flushStream = flushRequests.suppressible();

	private final FileSystemEntry entry;
	private final String name;

	protected T value;
	protected boolean valid;
	private ObservableValue<? extends T> observable = null;
	private InvalidationListener listener = null;
	protected ExpressionHelper<T> helper;

	public ReadableAttributeProperty(FileSystemEntry entry, String name) {
		this(entry, name, null);
	}

	public ReadableAttributeProperty(FileSystemEntry entry, String name, T initialValue) {
		this.entry = entry;
		this.name = name;
		this.value = initialValue;

		this.fileSystem = entry.getFileSystem();
		this.syncStream.successionEnds(Duration.ofMillis(100)).subscribe(_ -> {
			Guard guard = flushStream.suspend();
			sync().thenRun(guard::close);
		});
		this.flushStream.successionEnds(Duration.ofMillis(100)).subscribe(_ -> {
			Guard guard = syncStream.suspend();
			flush().thenRun(guard::close);
		});
	}

	@Override
	public T get() {
		valid = true;
		if (observable != null) {
			return observable.getValue();
		}
		return value;
	}

	@Override
	public T getValue() {
		return get();
	}

	void set(T newValue) {
		if (isBound()) {
			throw new RuntimeException((getBean() != null && getName() != null ?
					getBean().getClass().getSimpleName() + "." + getName() + " : " : "") + "A bound value cannot be set.");
		}
		if (!Objects.equals(value, newValue)) {
			value = newValue;
			markInvalid();
			requestFlush();
		}
	}

	void setValue(T newValue) {
		set(newValue);
	}

	boolean isBound() {
		return observable != null;
	}

	void bind(final ObservableValue<? extends T> newObservable) {
		if (newObservable == null) {
			throw new NullPointerException("Cannot bind to null");
		}

		if (!newObservable.equals(this.observable)) {
			unbind();
			observable = newObservable;
			if (listener == null) {
				listener = new Listener(this);
			}
			observable.addListener(listener);
			markInvalid();
		}
	}

	void unbind() {
		if (observable != null) {
			this.set(observable.getValue());
			observable.removeListener(listener);
			observable = null;
		}
	}


	void markInvalid() {
		if (valid) {
			valid = false;
			invalidated();
			fireValueChangedEvent();
		}
	}

	void invalidated() {
	}

	public void requestSync() {
		syncRequests.push(null);
	}

	public void requestFlush() {
		flushRequests.push(null);
	}

	abstract String getAttributeViewName();

	public String getAttributeKey() {
		return getAttributeViewName() + ":" + getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FileSystemEntry getBean() {
		return entry;
	}

	public FileSystemEntry getEntry() {
		return entry;
	}

	public FileSystemFX getFileSystem() {
		return fileSystem;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper = ExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper = ExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		helper = ExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		helper = ExpressionHelper.removeListener(helper, listener);
	}

	protected void fireValueChangedEvent() {
		ExpressionHelper.fireValueChangedEvent(helper);
	}

	@Override
	public String toString() {
		return "%s{path='%s', name='%s', value='%s'}".formatted(getClass().getSimpleName(), entry.path, name, value);
	}


	private static class Listener implements InvalidationListener, WeakListener {

		private final WeakReference<ReadableAttributeProperty<?, ?>> wref;

		public Listener(ReadableAttributeProperty<?, ?> ref) {
			this.wref = new WeakReference<>(ref);
		}

		@Override
		public void invalidated(Observable observable) {
			ReadableAttributeProperty<?, ?> ref = wref.get();
			if (ref == null) {
				observable.removeListener(this);
			} else {
				ref.markInvalid();
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return wref.get() == null;
		}
	}

	CompletableFuture<Void> sync() {
		log.debug("Sync: {}", this);
		UncheckedSupplier<S> read = this::read;
		UncheckedConsumer<S> push = this::push;
		ExecutionManager executionManager = getFileSystem().getExecutionManager();
		return executionManager.executeIO(read)
				.exceptionally(getFileSystem()::logError)
				.thenAcceptAsync(push, executionManager.getFxExecutor())
				.exceptionally(getFileSystem()::logError);
	}

	CompletableFuture<Void> flush() {
		log.debug("Flush: {}", this);
		UncheckedSupplier<S> pull = this::pull;
		UncheckedConsumer<S> write = this::write;
		ExecutionManager executionManager = getFileSystem().getExecutionManager();
		return executionManager.executeFX(pull)
				.exceptionally(getFileSystem()::logError)
				.thenAcceptAsync(write, executionManager.getIoExecutor())
				.exceptionally(getFileSystem()::logError);
	}

	abstract S read() throws IOException;

	abstract void write(S value) throws IOException;

	abstract S pull();

	abstract void push(S value);
}
