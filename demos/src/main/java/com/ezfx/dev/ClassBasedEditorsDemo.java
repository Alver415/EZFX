package com.ezfx.dev;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.controls.editor.impl.standard.*;
import com.ezfx.controls.editor.introspective.ClassBasedEditor;
import com.ezfx.controls.icons.SVGs;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClassBasedEditorsDemo extends EZFXApplication {

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
		ClassBasedEditor<Region> editor = new ClassBasedEditor<>(Region.class);
		Button exampleA = new Button("Example A");
		exampleA.setOnAction(_ -> editor.setValue(exampleA));
		Button exampleB = new Button("Example B");
		exampleB.setOnAction(_ -> editor.setValue(exampleB));
		return new Scene(new SplitPane(new ScrollPane(editor), new StackPane(new VBox(exampleA, exampleB))));
	}
}
