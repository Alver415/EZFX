package com.ezfx.base.utils;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Window;

public interface IScreenBounds {

	ObservableValue<Bounds> of(Object selected);
	ObservableValue<Bounds> ofScreens();
	ObservableValue<Bounds> ofScreen(Screen screen);
	ObservableValue<Bounds> ofWindow(Window window);

	ObservableValue<Bounds> ofScene(Scene scene);
	ObservableValue<Bounds> ofNode(Node node);
}
