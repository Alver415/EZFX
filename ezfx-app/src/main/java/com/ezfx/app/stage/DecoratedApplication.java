package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import static com.ezfx.base.utils.EZFX.runRepeatedlyFX;

public class DecoratedApplication extends Application {
	public static void main(String... args){
		Application.launch(DecoratedApplication.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox(new Label("Test"), new Button("This"));

		Stage stage = new DecoratedStage();
		Scene scene = new Scene(new Group(new Text("TESTING")));
		stage.setScene(scene);

		stage.setTitle("Test Example");
		stage.getIcons().add(Resources.image(Icons.class, "unlocked.png"));
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}
}
