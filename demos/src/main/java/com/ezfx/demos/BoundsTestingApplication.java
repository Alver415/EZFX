package com.ezfx.demos;

import com.ezfx.app.EZFXApplication;
import com.ezfx.base.utils.ScreenBounds;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class BoundsTestingApplication extends EZFXApplication {
	@Override
	public void start(Stage stage) throws Exception {

		Rectangle rectangle = new Rectangle(0,0, 20, 20);
		rectangle.setFill(Color.RED);

		TranslateTransition tx = new TranslateTransition(Duration.seconds(5), rectangle);
		tx.setByX(100);
		tx.setAutoReverse(true);
		tx.setCycleCount(-1);
		tx.play();

		TranslateTransition ty = new TranslateTransition(Duration.seconds(3), rectangle);
		ty.setByY(100);
		ty.setAutoReverse(true);
		ty.setCycleCount(-1);
		ty.play();


		VBox root = new VBox(rectangle);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		Rectangle2D screen = Screen.getPrimary().getBounds();
		stage.setX(screen.getMinX());
		stage.setY(screen.getMinY());
		stage.setWidth(screen.getWidth() / 2);
		stage.setHeight(screen.getHeight() / 2);
		stage.show();


		Label stageBoundsLabel = new Label();
		ObservableValue<Bounds> stageBounds = ScreenBounds.of(stage);
		ObservableValue<String> stageBoundsText = stageBounds.map(this::toString).map(b -> "Stage: " + b);
		stageBoundsLabel.textProperty().bind(stageBoundsText);

		Label sceneBoundsLabel = new Label();
		ObservableValue<Bounds> sceneBounds = ScreenBounds.of(scene);
		ObservableValue<String> sceneBoundsText = sceneBounds.map(this::toString).map(b -> "Scene: " + b);
		sceneBoundsLabel.textProperty().bind(sceneBoundsText);

		Label buttonBoundsLabel = new Label();
		ObservableValue<Bounds> buttonBounds = ScreenBounds.of(rectangle);
		ObservableValue<String> buttonBoundsText = buttonBounds.map(this::toString).map(b -> "Button: " + b);
		buttonBoundsLabel.textProperty().bind(buttonBoundsText);

		root.getChildren().addAll(stageBoundsLabel, sceneBoundsLabel, buttonBoundsLabel);

	}

	private String toString(Bounds bounds) {
		return "x:%.2f, y:%.2f, w:%.2f, h:%.2f".formatted(
				bounds.getMinX(), bounds.getMinY(),
				bounds.getWidth(), bounds.getHeight());
	}
}
