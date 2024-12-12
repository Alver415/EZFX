package com.ezfx.app.editor;

import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.base.utils.FrameInfo;
import com.ezfx.base.utils.Screens;
import com.ezfx.controls.explorer.SceneExplorer;
import com.ezfx.controls.icons.Icons;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.reactfx.EventStreams;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class FXMLEditorApplication extends Application {

	@Override
	public void start(Stage stage) {

		stage = new DecoratedStage();
		File file = Optional.ofNullable(getParameters())
				.map(Parameters::getNamed)
				.map(p -> p.get("fxml"))
				.map(File::new).orElse(null);

		SceneEditor root = new FXMLEditor(file);
//		SceneEditor root = new SceneEditor();

		Screens.setScreen(stage, 1);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("FXML Editor");
		stage.getIcons().add(Icons.EDIT);
		stage.centerOnScreen();
		stage.setMaximized(true);
		stage.show();
//
//		EventStreams.animationTicks()
//				.latestN(100)
//				.map(ticks -> {
//					int n = ticks.size() - 1;
//					return n * 1_000_000_000.0 / (ticks.get(n) - ticks.get(0));
//				})
//				.map(d -> String.format("FXML Editor - FPS: %.3f", d))
//				.feedTo(stage.titleProperty());
//
		EventStreams.animationFrames()
				.latestN(100)
				.map(List::getLast)
				.map(d -> String.format("FXML Editor - Frame Time: %s", d))
				.feedTo(stage.titleProperty());


		SceneExplorer.stage(scene);
	}

}
