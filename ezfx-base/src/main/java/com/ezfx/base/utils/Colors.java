package com.ezfx.base.utils;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public interface Colors {

	static Color random() {
		return Color.color(Math.random(), Math.random(), Math.random());
	}

	static String toHexFormat(Paint paint) {
		return paint instanceof Color color ?
				toHexFormat(color) :
				String.valueOf(paint);
	}

	static String toHexFormat(Color color) {
		return String.format(
				"#%02X%02X%02X",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	static String toRgba(Paint paint) {
		return paint instanceof Color color ?
				toRgba(color) :
				String.valueOf(paint);
	}

	static String toRgba(Color color) {
		return String.format(
				"rgba(%d, %d, %d, %.2f)",
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255),
				color.getOpacity());
	}

	static Color withAlpha(Color white, double alpha) {
		return white.interpolate(Color.TRANSPARENT, alpha);
	}
}
