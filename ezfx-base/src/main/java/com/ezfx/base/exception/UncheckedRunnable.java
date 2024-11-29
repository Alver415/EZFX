package com.ezfx.base.exception;

@FunctionalInterface
public interface UncheckedRunnable extends Runnable {
	@Override
	default void run() {
		try {
			tryRun();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void tryRun() throws Exception;
}