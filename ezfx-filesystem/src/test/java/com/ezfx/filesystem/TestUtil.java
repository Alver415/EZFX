package com.ezfx.filesystem;

import java.time.Duration;

public class TestUtil {

	public static boolean isTrue(Boolean bool) {
		return bool;
	}

	public static boolean isFalse(Boolean bool) {
		return !bool;
	}

	public static void sleep(Duration duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
