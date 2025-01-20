package com.ezfx.demos;

import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DecoratedApplication extends Application {
	public static void main(String... args){
		Application.launch(DecoratedApplication.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		configureStage("Standard Stage", primaryStage);
		configureStage("Decorated Stage", new DecoratedStage());
	}

	private static void configureStage(String title, Stage stage) {
		Scene scene = new Scene(new Group(new Text("TESTING")));
		stage.setScene(scene);

		stage.setTitle(title);
		stage.getIcons().add(Resources.image(Icons.class, "unlocked.png"));
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}
}
