package com.ezfx.demos;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.console.ProcessView;
import com.ezfx.app.stage.DecoratedStage;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EZFZConsoleApplication extends EZFXApplication {
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

	}
}
