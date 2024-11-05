package com.ezfx.demos;

import com.fsfx.control.explorer.SceneExplorer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DemoApplication extends Application {

	public static class Launcher {
		public static void main(String... args){
			Application.launch(DemoApplication.class);
		}
	}
	@Override
	public void start(Stage stage) throws Exception {
		BorderPane borderPane = new BorderPane();
		Scene scene = new Scene(borderPane);
		stage.setScene(scene);

		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Menu");
		MenuItem menuItem = new MenuItem("Scene Explorer");
		menuItem.setOnAction(_ -> SceneExplorer.stage(scene));
		menu.getItems().setAll(menuItem);
		menuBar.getMenus().setAll(menu);
		TextArea textArea = new TextArea("Test");
		textArea.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		borderPane.setTop(menuBar);
		borderPane.setCenter(textArea);

		stage.centerOnScreen();
		stage.setTitle("Demo Application");
		stage.show();

		KeyCombination shortcut = new KeyCodeCombination(KeyCode.S,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN);
		scene.setOnKeyPressed(event -> {
			if (shortcut.match(event)) {
				SceneExplorer.stage(scene);
			}
		});
	}
}
