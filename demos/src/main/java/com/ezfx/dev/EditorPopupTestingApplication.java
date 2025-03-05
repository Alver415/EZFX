package com.ezfx.dev;

import com.ezfx.app.EZFXApplication;
import com.ezfx.controls.popup.OverlayPopup;
import com.ezfx.controls.popup.NodeMiniEditor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EditorPopupTestingApplication extends EZFXApplication {
	@Override
	public void start(Stage stage) throws Exception {

		NodeMiniEditor editor = new NodeMiniEditor();

		OverlayPopup editorPopup = new OverlayPopup(stage);
//		editorPopup.titleProperty().bind(editor.valueProperty().flatMap(NodeInfoImpl.CACHING::info));

		Button button = new Button("Testing");
		button.setOnAction(a -> editorPopup.show(stage));

		editor.setValue(button);

		stage.setTitle("EditorPopup Test Application");
		stage.setWidth(400);
		stage.setHeight(600);
		stage.setScene(new Scene(button));
		stage.show();
	}
}
