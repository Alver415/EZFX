package com.ezfx.demos;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.explorer.ApplicationExplorer;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.controls.icons.SVGs;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class ApplicationExplorerDemo extends EZFXApplication {

	private static final WritableImage EMPTY_IMAGE = new WritableImage(16, 16);

	@Override
	public void start(Stage primaryStage) throws Exception {
		setTitle("Application");
		Stage stage = new DecoratedStage();
		stage.getIcons().add(SVGs.GEAR.image(32));
		iconProperty().subscribe(icon -> stage.getIcons().setAll(icon == null ? EMPTY_IMAGE : icon));
		stage.setScene(buildScene());
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}

	private Scene buildScene() {
		return new Scene(new ApplicationExplorer(this));
	}
}
