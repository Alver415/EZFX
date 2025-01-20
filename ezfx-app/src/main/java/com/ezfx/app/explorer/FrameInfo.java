package com.ezfx.app.explorer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import org.reactfx.EventStreams;

import java.util.List;

public class FrameInfo {

	public static final DoubleProperty FRAME_RATE = new SimpleDoubleProperty();
	public static final LongProperty LAST_FRAME = new SimpleLongProperty();

	static {
		start();
	}
	private static boolean started = false;
	public static void start() {
		if (started) return;
		started = true;
		EventStreams.animationTicks()
				.latestN(100)
				.map(ticks -> {
					int n = ticks.size() - 1;
					return n * 1_000_000_000.0 / (ticks.get(n) - ticks.getFirst());
				})
				.feedTo(FRAME_RATE);

		EventStreams.animationFrames()
				.latestN(100)
				.map(List::getLast)
				.map(frame -> frame / 1_000_000)
				.feedTo(LAST_FRAME);
	}
}
