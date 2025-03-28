package com.ezfx.base.utils;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Optional;

public interface Screens {

	static void setScreen(Stage stage, int index) {
		ObservableList<Screen> screens = Screen.getScreens();
		Screen screen = screens.get(index);
		Rectangle2D bounds = screen.getVisualBounds();

		// Position the stage on the second monitor
		stage.setX(bounds.getMinX());
		stage.setY(bounds.getMinY());
		stage.setWidth(bounds.getWidth());
		stage.setHeight(bounds.getHeight());
	}

	static Screen getScreen(Stage stage) {
		return Screen.getScreensForRectangle(
						stage.getX(), stage.getY(),
						stage.getWidth(), stage.getHeight())
				.stream()
				.findFirst()
				.orElse(Screen.getPrimary());
	}

	static Optional<Screen> getScreen(double x, double y) {
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D bounds = screen.getBounds();
			if (bounds.contains(x, y)) {
				return Optional.of(screen);
			}
		}
		return Optional.empty();
	}

}
