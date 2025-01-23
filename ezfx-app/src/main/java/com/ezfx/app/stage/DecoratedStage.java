package com.ezfx.app.stage;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DecoratedStage extends Stage {
	private final StageDecoration decoration = new StageDecoration();

	public DecoratedStage() {
		super(StageStyle.TRANSPARENT);
		sceneProperty().subscribe(scene -> {
			if (scene == null) return;
			scene.setFill(Color.TRANSPARENT);
			Parent root = scene.getRoot();
			if (decoration.getScene() != null){
				decoration.getScene().setRoot(new StackPane());
			}
			scene.setRoot(decoration);
			decoration.setRoot(root);
		});
	}

	public StageDecoration getDecoration() {
		return decoration;
	}
}
