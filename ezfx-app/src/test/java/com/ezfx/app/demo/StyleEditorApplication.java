package com.ezfx.app.demo;

import com.ezfx.base.utils.Screens;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorWrapper;
import com.ezfx.controls.editor.impl.javafx.StyleEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class StyleEditorApplication extends Application {


	public static void main(String... args) {
		Application.launch(StyleEditorApplication.class);
	}

	@Override
	public void start(Stage stage) {
		Screens.setScreen(stage, 1);

		Label label = new Label("EXAMPLE");
		label.setStyle("-fx-background-color: yellow");
		label.setBorder(Border.stroke(Color.RED));
		StyleEditor styleEditor = new StyleEditor(label.styleProperty());

		Scene scene = new Scene(new SplitPane(styleEditor, new StackPane(label)));
		stage.setScene(scene);
		stage.setTitle("Test Application");
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}
}
