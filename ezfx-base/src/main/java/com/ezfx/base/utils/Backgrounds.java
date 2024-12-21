package com.ezfx.base.utils;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;


public interface Backgrounds {

	Color DEFAULT_PRIMARY = Color.LIGHTGRAY;
	Color DEFAULT_SECONDARY = Color.DARKGRAY;

	Background CHECKERED = checkeredBackground();

	static Background borderedBackground(Color inside, Color middle, Color outside) {
		Insets insideInsets = new Insets(-1);
		Insets middleInsets = Insets.EMPTY;
		Insets outsideInsets = new Insets(1);

		return new Background(new BackgroundFill(inside, CornerRadii.EMPTY, insideInsets),
				new BackgroundFill(middle, CornerRadii.EMPTY, middleInsets),
				new BackgroundFill(outside, CornerRadii.EMPTY, outsideInsets));
	}

	static ImagePattern checkeredImagePattern() {
		return checkeredImagePattern(DEFAULT_PRIMARY, DEFAULT_SECONDARY, 10, 10, 2, 2);
	}

	static ImagePattern checkeredImagePattern(
			Color foreground, Color background,
			int width, int height,
			int rows, int cols) {
		Image image = buildCheckeredImage(foreground, background, width, height, rows, cols);
		return new ImagePattern(image, 0, 0, width, height, false);
	}

	static Background checkeredBackground() {
		return checkeredBackground(DEFAULT_PRIMARY, DEFAULT_SECONDARY, 10, 10, 2, 2);
	}

	static Background checkeredBackground(
			Color foreground, Color background,
			int width, int height,
			int rows, int cols) {
		return new Background(new BackgroundImage(
				buildCheckeredImage(foreground, background, width, height, rows, cols),
				null, null, null, null));
	}

	static Image buildCheckeredImage(
			Color foreground,
			Color background,
			int width,
			int height,
			int rows,
			int cols) {
		WritableImage writableImage = new WritableImage(width, height);
		PixelWriter pixelWriter = writableImage.getPixelWriter();

		double cellWidth = ((double) width) / cols;
		double cellHeight = ((double) height) / rows;

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int rowNum = (int) (row / cellHeight);
				int colNum = (int) (col / cellWidth);

				Color color = (rowNum + colNum) % 2 == 0 ? foreground : background;

				pixelWriter.setColor(col, row, color);
			}
		}
		return writableImage;
	}

	static Canvas buildRuler(double xMin, double xMax, double yMin, double yMax) {
		Canvas canvas = new Canvas();
		canvas.setWidth(1000);
		canvas.setHeight(1000);
		GraphicsContext g = canvas.getGraphicsContext2D();

		g.setStroke(Color.GRAY);
		g.setLineWidth(0.5);

		for (double x = Math.round(xMin); x < xMax; x += 10) {
			g.strokeLine(x, yMin, x, yMax);
		}

		for (double y = Math.round(yMin); y < yMax; y += 10) {
			g.strokeLine(xMin, y, xMax, y);
		}

		g.setStroke(Color.BLACK);
		g.setLineWidth(1);
		g.strokeLine(xMax, 0, xMax, yMax);
		g.strokeLine(0, yMax, xMax, yMax);

		return canvas;
	}

}
