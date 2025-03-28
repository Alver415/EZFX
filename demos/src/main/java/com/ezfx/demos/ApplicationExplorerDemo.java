package com.ezfx.demos;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.explorer.ApplicationExplorer;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.controls.icons.SVGs;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class ApplicationExplorerDemo extends EZFXApplication {

	private static final WritableImage EMPTY_IMAGE = new WritableImage(16, 16);

	public static void main(String... args){
		Application.launch(ApplicationExplorerDemo.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		setTitle("Application");
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
