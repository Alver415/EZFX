package com.ezfx.demos;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.console.ManagedContext;
import com.ezfx.app.console.PolyglotView;
import com.ezfx.controls.utils.SplitPanes;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public class EZFXPolyglotApplication extends EZFXApplication {
	private ManagedContext managedContext;
	private SubScene subScene;
	private Canvas canvas;

	@Override
	public void start(Stage stage) throws Exception {
		canvas = new Canvas(600,400);
		subScene = new SubScene(new StackPane(), 600, 400);

		managedContext = ManagedContext.newBuilder()
				.permittedLanguages("js", "python")
				.allowAllAccess(true)
				.build();
		managedContext.getContext().getPolyglotBindings().putMember("application", this);
		managedContext.getContext().getPolyglotBindings().putMember("canvas", canvas);
		managedContext.getContext().getPolyglotBindings().putMember("subScene", subScene);

		setupExamples();
		SplitPane examples = SplitPanes.horizontal(new StackPane(canvas), new StackPane(subScene));

		PolyglotView polyglotView = new PolyglotView(managedContext);
		SplitPane root = SplitPanes.vertical(examples, polyglotView);
		Scene scene = new Scene(root);
		stage.setWidth(1600);
		stage.setHeight(900);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setTitle("Polyglot Playground");
		stage.show();
	}

	private void setupExamples() throws IOException {
		// Start with a little example circle on the canvas
		GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		graphicsContext.setFill(Color.RED);
		graphicsContext.fillOval(0, 0, 100, 100);

		// Start with an example FXML loaded.
		FXMLLoader loader = new FXMLLoader(Path.of("demos/src/main/resources/com/ezfx/demos/Icon.fxml").toUri().toURL());
		subScene.setRoot(loader.load());
	}

	public void stop() throws Exception {
		super.stop();
		managedContext.close();
		Platform.exit();
	}
}
