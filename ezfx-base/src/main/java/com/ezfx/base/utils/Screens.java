package com.ezfx.base.utils;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

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
}
