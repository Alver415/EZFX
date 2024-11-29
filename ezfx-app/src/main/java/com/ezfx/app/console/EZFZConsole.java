package com.ezfx.app.console;

import com.ezfx.app.demo.ProcessView;
import com.ezfx.controls.explorer.SceneExplorer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EZFZConsole extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		String cmd = "cmd";
		ProcessView processView = new ProcessView(new ProcessBuilder(cmd).start());
		processView.setPrefSize(600, 400);

		Scene scene = new Scene(processView);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setTitle("EZFX Console");
		stage.show();

		SceneExplorer.stage(scene);
	}
}
