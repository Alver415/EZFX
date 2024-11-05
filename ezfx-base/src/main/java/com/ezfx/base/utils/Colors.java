package com.ezfx.base.utils;

import javafx.scene.paint.Color;

import java.util.function.Supplier;

public class Colors {

	private static final Supplier<Double> random = Math::random;

	public static Color random() {
		return Color.color(random.get(), random.get(), random.get());
	}
}
