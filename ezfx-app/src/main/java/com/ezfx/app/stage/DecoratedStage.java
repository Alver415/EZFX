package com.ezfx.app.stage;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DecoratedStage extends Stage {
	private final StageDecoration decoration = new StageDecoration();
	public DecoratedStage() {
		super(StageStyle.TRANSPARENT);
		sceneProperty().addListener((_, _, scene) -> {
			scene.setFill(Color.TRANSPARENT);
			Parent root = scene.getRoot();
			scene.setRoot(decoration);
			decoration.setRoot(root);
		});
	}
}
