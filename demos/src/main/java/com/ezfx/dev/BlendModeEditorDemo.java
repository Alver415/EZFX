package com.ezfx.dev;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.base.utils.Colors;
import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.impl.javafx.BlendModeEditor;
import com.ezfx.controls.editor.impl.javafx.ColorEditor;
import com.ezfx.controls.editor.impl.javafx.NodeEditor;
import com.ezfx.controls.icons.SVGs;
import com.ezfx.controls.utils.SplitPanes;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.Optional;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class BlendModeEditorDemo extends EZFXApplication {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(buildScene());
		stage.setWidth(1600);
		stage.setHeight(900);
		stage.centerOnScreen();
		stage.show();
	}

	private static Scene buildScene() {
		Label label = new Label();
		NodeEditor nodeEditor = new NodeEditor();
		nodeEditor.setValue(label);
		return new Scene(new StackPane(new SubScene(nodeEditor, 200, 200)));
	}


}
