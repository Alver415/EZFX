package com.ezfx.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SimpleDemo extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new Scene(new BorderPane(new Label("Hello World"))));
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}
}
