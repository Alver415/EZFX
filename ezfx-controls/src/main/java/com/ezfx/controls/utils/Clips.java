package com.ezfx.controls.utils;

import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class Clips {
	public static Rectangle rectangle(Region target) {
		Rectangle rectangle = new Rectangle();
		rectangle.widthProperty().bind(target.widthProperty());
		rectangle.heightProperty().bind(target.heightProperty());
		rectangle.xProperty().bind(target.translateXProperty());
		rectangle.yProperty().bind(target.translateYProperty());
		return rectangle;
	}
}
