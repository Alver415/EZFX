package com.ezfx.app.console;

import com.ezfx.app.demo.ProcessView;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.controls.explorer.SceneExplorer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EZFZConsole extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		stage = new DecoratedStage();
		String cmd = "cmd";
		ProcessView processView = new ProcessView(new ProcessBuilder(cmd).start());

		Scene scene = new Scene(processView);
		stage.setWidth(600);
		stage.setHeight(400);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setTitle("EZFX Console");
		stage.show();

		SceneExplorer.stage(scene);
	}
}
