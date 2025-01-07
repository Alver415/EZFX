package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.skin.EditorSkin;
import com.ezfx.controls.explorer.FrameInfo;
import com.ezfx.controls.misc.ProgressView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ApplicationEditorSkin extends EditorSkin<Editor<Application>, Application> {

	private static final Logger log = LoggerFactory.getLogger(ApplicationEditorSkin.class);

	public ApplicationEditorSkin(ApplicationEditor editor) {
		super(editor);

		Label frameRateLabel = new Label("Frame Rate: ");
		ProgressView frameRateBar = new ProgressView();
		frameRateBar.textProperty().bind(FrameInfo.FRAME_RATE.map(Number::intValue).map(Objects::toString));
		frameRateBar.progressProperty().bind(FrameInfo.FRAME_RATE.divide(60));

		Label frameTimeLabel = new Label("Frame Delta: ");
		ProgressView frameTimeBar = new ProgressView();
		frameTimeBar.textProperty().bind(FrameInfo.LAST_FRAME.map(Number::intValue).map(Objects::toString));
		frameTimeBar.progressProperty().bind(FrameInfo.LAST_FRAME.divide(100d));

		Button forceClose = new Button("FORCE CLOSE");
		forceClose.setOnAction(_ -> forceClose());

		HBox frameRateBox = new HBox(4, frameRateLabel, frameRateBar);
		HBox frameTimeBox = new HBox(4, frameTimeLabel, frameTimeBar);
		VBox vBox = new VBox(8, frameRateBox, frameTimeBox, forceClose);

		StackPane stackPane = new StackPane(vBox);
		stackPane.setPadding(new Insets(8));
		getChildren().setAll(stackPane);

	}

	private void forceClose() {
		try {
			Editor<Application> editor = getSkinnable();
			Application application = editor.getValue();
			application.stop();
			Platform.exit();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		} finally {
			System.exit(1);
		}
	}
}
