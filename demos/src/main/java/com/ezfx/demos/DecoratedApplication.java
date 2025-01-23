package com.ezfx.demos;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.app.stage.EZFXStageDecorationSkin;
import com.ezfx.app.stage.StageDecoration;
import com.ezfx.app.stage.WindowsStageDecorationSkin;
import com.ezfx.base.utils.Colors;
import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.icons.SVGPaths;
import com.ezfx.controls.icons.SVGs;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static com.ezfx.controls.icons.SVGs.GEAR;

public class DecoratedApplication extends EZFXApplication {
	public static void main(String... args) {
		Application.launch(DecoratedApplication.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		configureStage("Standard Stage", primaryStage);

		DecoratedStage decoratedStage = new DecoratedStage();
		configureStage("Decorated Stage (Default Skin)", decoratedStage);

		DecoratedStage ezfxStage = new DecoratedStage();
		ezfxStage.getDecoration().setStyle("-fx-skin:\"com.ezfx.app.stage.EZFXStageDecorationSkin\"");
		configureStage("Decorated Stage (EZFX Skin)", ezfxStage);

		DecoratedStage windowsStage = new DecoratedStage();
		windowsStage.getDecoration().setStyle("-fx-skin:\"com.ezfx.app.stage.WindowsStageDecorationSkin\"");
		configureStage("Decorated Stage (Windows Skin)", windowsStage);
	}

	static int offset = 0;
	private static void configureStage(String title, Stage stage) {
		stage.setScene(buildScene());

		stage.getIcons().addAll(GEAR.image(32));
		stage.setTitle(title);
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.setX(stage.getX() + offset);
		stage.setX(stage.getX() + offset);
		offset += 64;
		stage.show();
	}

	private static Scene buildScene() {
		StackPane stackPane = new StackPane(new VBox(new Button("Button"), new Text("Example Text"), SVGs.GEAR2.svg()));
		stackPane.setBorder(Border.stroke(Color.YELLOW));
		stackPane.setBackground(Background.fill(Color.rgb(200, 100, 100, 0.5)));
		return new Scene(stackPane);
	}
}
