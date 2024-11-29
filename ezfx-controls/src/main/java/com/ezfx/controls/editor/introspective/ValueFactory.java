package com.ezfx.controls.editor.introspective;

import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

public class ValueFactory {

	public static BackgroundFill backgroundFill(Paint fill){
		return new BackgroundFill(fill, new CornerRadii(20), new Insets(20));
	}
}
