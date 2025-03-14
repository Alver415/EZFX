package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.EditorSkinBase;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.misc.ProgressView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.reactfx.EventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ApplicationEditor extends ObjectEditor<Application> {

	private static final Logger log = LoggerFactory.getLogger(ApplicationEditor.class);

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<EditorBase<Application>, Application> {

		public DefaultSkin(ApplicationEditor editor) {
			super(editor);

			ImageView spinner = new ImageView(Icons.LOADING);
			spinner.setFitWidth(64);
			spinner.setFitHeight(64);

			Label frameRateLabel = new Label("Frame Rate: ");
			ProgressView frameRateBar = new ProgressView();
			frameRateBar.textProperty().bind(FrameInfo.FRAME_RATE.map(Number::intValue).map(Objects::toString));
			frameRateBar.progressProperty().bind(FrameInfo.FRAME_RATE.divide(60));

			Label frameIntervalLabel = new Label("Frame Interval: ");
			ProgressView frameIntervalBar = new ProgressView();
			frameIntervalBar.textProperty().bind(FrameInfo.LAST_FRAME.map(Number::intValue).map(Objects::toString));
			frameIntervalBar.progressProperty().bind(FrameInfo.LAST_FRAME.divide(100d));

			Button forceClose = new Button("FORCE CLOSE");
			forceClose.setOnAction(_ -> forceClose());

			IntrospectingPropertiesEditor<Application> subEditor = new IntrospectingPropertiesEditor<>(editor.valueProperty());

			HBox frameRateBox = new HBox(4, frameRateLabel, frameRateBar);
			HBox frameIntervalBox = new HBox(4, frameIntervalLabel, frameIntervalBar);
			VBox vBox = new VBox(8, spinner, frameRateBox, frameIntervalBox, subEditor, forceClose);


			StackPane stackPane = new StackPane(vBox);
			stackPane.setPadding(new Insets(8));
			getChildren().setAll(stackPane);

		}

		private void forceClose() {
			try {
				EditorBase<Application> editor = getSkinnable();
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

	private static class FrameInfo {

		public static final DoubleProperty FRAME_RATE = new SimpleDoubleProperty();
		public static final LongProperty LAST_FRAME = new SimpleLongProperty();

		static {
			start();
		}

		private static boolean started = false;

		public static void start() {
			if (started) return;
			started = true;
			EventStreams.animationTicks()
					.latestN(100)
					.map(ticks -> {
						int n = ticks.size() - 1;
						return n * 1_000_000_000.0 / (ticks.get(n) - ticks.getFirst());
					})
					.feedTo(FRAME_RATE);

			EventStreams.animationFrames()
					.latestN(100)
					.map(List::getLast)
					.map(frame -> frame / 1_000_000)
					.feedTo(LAST_FRAME);
		}
	}

}
