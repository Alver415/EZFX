package com.ezfx.base.utils;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jdk.jfr.consumer.EventStream;

public class FrameInfo {

	private static final long[] frameTimes = new long[100];
	private static int frameTimeIndex = 0;

	public static final StringProperty frameRate = new SimpleStringProperty();

	public static AnimationTimer frameRaterMeter = new AnimationTimer() {
		@Override
		public void handle(long now) {
			long oldFrameTime = frameTimes[frameTimeIndex];
			frameTimes[frameTimeIndex] = now;
			frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
			long elapsedNanos = now - oldFrameTime;
			double frameRate = 100_000_000_000.0 / elapsedNanos;
			FrameInfo.frameRate.set(String.format("Current frame rate: %.3f", frameRate));
		}
	};

	public static void start() {
		frameRaterMeter.start();
	}
}
