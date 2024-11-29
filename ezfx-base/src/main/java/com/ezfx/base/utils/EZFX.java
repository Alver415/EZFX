package com.ezfx.base.utils;

import com.ezfx.base.exception.UncheckedRunnable;
import com.ezfx.base.exception.UncheckedSupplier;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import javafx.util.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface EZFX {

	static void sleep(Duration duration) {
		sleep(duration.toMillis());
	}

	static void sleep(double millis) {
		sleep((long) millis);
	}

	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	static <T> T tryGet(UncheckedSupplier<T> supplier) {
		return supplier.get();
	}

	static void tryRun(UncheckedRunnable runnable) {
		runnable.run();
	}

	static void runLater(Duration duration, UncheckedRunnable runnable) {
		runOnVirtualThread(() -> {
			sleep(duration.toMillis());
			runnable.run();
		});
	}

	static void runLaterFX(Duration duration, UncheckedRunnable runnable) {
		runOnVirtualThread(() -> {
			sleep(duration.toMillis());
			runFX(runnable::run);
		});
	}

	static Subscription runRepeatedly(Duration duration, UncheckedRunnable runnable) {
		AtomicBoolean running = new AtomicBoolean(true);
		runOnVirtualThread(() -> {
			while (running.get()) {
				runnable.run();
				sleep(duration.toMillis());
			}
		});
		return () -> running.set(false);
	}

	static Subscription runRepeatedlyFX(Duration duration, UncheckedRunnable runnable) {
		AtomicBoolean running = new AtomicBoolean(true);
		runOnVirtualThread(() -> {
			while (running.get()) {
				runFX(runnable::run);
				sleep(duration.toMillis());
			}
		});
		return () -> running.set(false);
	}

	static void runOnNewThread(UncheckedRunnable runnable) {
		runOnNewThread((Runnable) runnable);
	}

	static void runOnNewThread(Runnable runnable) {
		runOnVirtualThread(runnable);
	}

	static Thread runOnVirtualThread(UncheckedRunnable runnable) {
		return runOnVirtualThread((Runnable) runnable);
	}

	static Thread runOnVirtualThread(Runnable runnable) {
		return Thread.ofVirtual().start(runnable);
	}

	static Thread runOnPlatformThread(UncheckedRunnable runnable) {
		return runOnPlatformThread((Runnable) runnable);
	}

	static Thread runOnPlatformThread(Runnable runnable) {
		return Thread.ofPlatform().start(runnable);
	}

	static void runFX(UncheckedRunnable runnable) {
		runFX((Runnable) runnable);
	}

	static void runFX(Runnable runnable) {
		Platform.runLater(runnable);
	}

	static boolean isFxApplicationThread() {
		return Platform.isFxApplicationThread();
	}



	// region Streams
	static <T> Collector<T, ?, ObservableList<T>> toObservableArrayList() {
		return Collectors.toCollection(FXCollections::observableArrayList);
	}
	// endregion Streams

}