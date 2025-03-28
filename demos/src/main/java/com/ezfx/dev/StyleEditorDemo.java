package com.ezfx.dev;

import com.ezfx.base.utils.Screens;
import com.ezfx.controls.editor.impl.javafx.StyleEditor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StyleEditorDemo extends Application {

	@Override
	public void start(Stage stage) {
		Screens.setScreen(stage, 1);

		Label label = new Label("EXAMPLE");
		label.setStyle("-fx-background-color: yellow");
		label.setBorder(Border.stroke(Color.RED));
		StackPane stackPane = new StackPane(label);

		StyleEditor styleEditor = new StyleEditor(label.styleProperty());

		Scene scene = new Scene(new SplitPane(styleEditor, stackPane));
		stage.setScene(scene);
		stage.setTitle("Test Application");
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();

	}
}
