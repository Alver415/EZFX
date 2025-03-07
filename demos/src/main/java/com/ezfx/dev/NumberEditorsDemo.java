package com.ezfx.dev;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.controls.editor.impl.standard.*;
import com.ezfx.controls.icons.SVGs;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NumberEditorsDemo extends EZFXApplication {

	@Override
	public void start(Stage primaryStage) throws Exception {
		setTitle("Number Editors");
		Stage stage = new DecoratedStage();
		stage.getIcons().add(SVGs.GEAR.image(32));
		stage.setScene(buildScene());
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}

	private static Scene buildScene() {
		return new Scene(new VBox(
				new ShortEditor(),
				new IntegerEditor(),
				new LongEditor(),
				new FloatEditor(),
				new DoubleEditor()
		));
	}
}
