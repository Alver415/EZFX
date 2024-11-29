package com.ezfx.filesystem;

import com.ezfx.base.exception.UncheckedRunnable;
import com.ezfx.base.exception.UncheckedSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.ezfx.base.utils.EZFX.isFxApplicationThread;

public class ExecutionManager {

	private static final Logger log = LoggerFactory.getLogger(ExecutionManager.class);
	public static final String IO_THREAD_NAME = "JavaFX FileSystem Thread";
	private final Executor fxExecutor;
	private final Executor ioExecutor;

	ExecutionManager(Executor ioExecutor, Executor fxExecutor) {
		this.ioExecutor = ioExecutor;
		this.fxExecutor = fxExecutor;
	}

	public boolean isIoFileSystemThread() {
		return IO_THREAD_NAME.equals(Thread.currentThread().getName());
	}

	public <T> CompletableFuture<T> executeFX(UncheckedSupplier<T> supplier) {
		if (isFxApplicationThread()) {
			return CompletableFuture.completedFuture(supplier.get());
		}
		return CompletableFuture.supplyAsync(supplier, fxExecutor);
	}

	public CompletableFuture<Void> executeFX(UncheckedRunnable runnable) {
		if (isFxApplicationThread()) {
			runnable.run();
			return CompletableFuture.completedFuture(null);
		}
		return CompletableFuture.runAsync(runnable, ioExecutor);
	}


	public <T> CompletableFuture<T> executeIO(UncheckedSupplier<T> supplier) {
		if (isIoFileSystemThread()) {
			return CompletableFuture.completedFuture(supplier.get());
		}
		return CompletableFuture.supplyAsync(supplier, ioExecutor);
	}

	public CompletableFuture<Void> executeIO(UncheckedRunnable runnable) {
		if (isIoFileSystemThread()) {
			runnable.run();
			return CompletableFuture.completedFuture(null);
		}
		return CompletableFuture.runAsync(runnable, ioExecutor);
	}


	public Executor getIoExecutor() {
		return ioExecutor;
	}

	public Executor getFxExecutor() {
		return fxExecutor;
	}

}
