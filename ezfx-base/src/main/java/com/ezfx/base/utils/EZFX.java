package com.ezfx.base.utils;

import com.ezfx.base.exception.UncheckedRunnable;
import com.ezfx.base.exception.UncheckedSupplier;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface EZFX {

	Logger log = LoggerFactory.getLogger(EZFX.class);

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

	static void runAsync(UncheckedRunnable runnable) {
		runAsync((Runnable) runnable);
	}

	static void runAsync(Runnable runnable) {
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


	// region Timer
	static void printTime(Runnable runnable){
		printTime("Delta Time", runnable);
	}
	static void printTime(String message, Runnable runnable){
		long start = System.nanoTime();
		try{
			runnable.run();
		} finally {
			long end = System.nanoTime();
			long deltaNanos = end - start;
			long deltaMillis = deltaNanos / 1000000;
			log.info("%s: %dms".formatted(message, deltaMillis));
		}
	}
	// endregion Timer



	// region Streams
	static <T> Collector<T, ?, ObservableList<T>> toObservableArrayList() {
		return Collectors.toCollection(FXCollections::observableArrayList);
	}
	// endregion Streams

}