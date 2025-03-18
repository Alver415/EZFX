package com.ezfx.dev;

import com.ezfx.controls.editor.code.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class CodeEditorsDemo extends Application {

	@Override
	public void start(Stage stage) {
		CodeEditor codeEditor = new CodeEditor();
		Button javaButton = new Button("java");
		Button xmlButton = new Button("xml");
		Button cssButton = new Button("css");
		Button jsonButton = new Button("json");

		Supplier<Skin<?>> javaSkin = () -> new JavaEditorSkin(codeEditor);
		Supplier<Skin<?>> xmlSkin = () -> new XMLEditorSkin(codeEditor);
		Supplier<Skin<?>> cssSkin = () -> new CSSEditorSkin(codeEditor);
		Supplier<Skin<?>> jsonSkin = () -> new JSONEditorSkin(codeEditor);

		javaButton.setOnAction(_ -> codeEditor.setSkin(javaSkin.get()));
		xmlButton.setOnAction(_ -> codeEditor.setSkin(xmlSkin.get()));
		cssButton.setOnAction(_ -> codeEditor.setSkin(cssSkin.get()));
		jsonButton.setOnAction(_ -> codeEditor.setSkin(jsonSkin.get()));

		Scene scene = new Scene(
				new VBox(4,
						new HBox(4,
								javaButton,
								xmlButton,
								cssButton,
								jsonButton),
						codeEditor));

		stage.setScene(scene);
		stage.setTitle("Code Editor");
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();

	}
}
