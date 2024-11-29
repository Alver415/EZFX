package com.ezfx.app.editor;

import com.ezfx.base.utils.Screens;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class FXMLEditorApplication extends Application {

	@Override
	public void start(Stage stage) {
		File file = Optional.ofNullable(getParameters())
				.map(Parameters::getNamed)
				.map(p -> p.get("fxml"))
				.map(File::new).orElse(null);

		SceneEditor root = new FXMLEditor(file);

		Screens.setScreen(stage, 1);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Scene Editor");
		stage.centerOnScreen();
		stage.setMaximized(true);
		stage.show();
	}

}
