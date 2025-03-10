package com.ezfx.dev;

import com.ezfx.app.EZFXApplication;
import com.ezfx.controls.editor.impl.javafx.NodeEditor;
import com.ezfx.controls.editor.introspective.ClassBasedEditor;
import com.ezfx.controls.icons.SVGs;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NodeEditorDemo extends EZFXApplication {

	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(SVGs.GEAR.image(32));
		stage.setScene(buildScene());
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}

	private static Scene buildScene() {
		NodeEditor editor = new NodeEditor();
		editor.setValue(new Region());
		Button exampleA = new Button("Example A");
		exampleA.setOnAction(_ -> editor.setValue(exampleA));
		Label exampleB = new Label("Example B");
		exampleB.setOnMouseClicked(_ -> editor.setValue(exampleB));
		ScrollPane scrollPane = new ScrollPane(editor);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		return new Scene(new SplitPane(scrollPane, new StackPane(new VBox(exampleA, exampleB))));
	}
}
