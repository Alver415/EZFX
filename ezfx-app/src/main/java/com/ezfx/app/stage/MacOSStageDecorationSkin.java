package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.icons.SVGPaths;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class MacOSStageDecorationSkin<T extends StageDecoration> extends StageDecorationSkin<T> {
	private static final String STYLE_SHEET = Resources.css(MacOSStageDecorationSkin.class, "MacOSStageDecorationSkin.css");

	Group minimizeGraphic = SVGPaths._parse(Resources.inputStream(Icons.class, "mycons/macos/minimize.svg"));
	Group maximizeGraphic = SVGPaths._parse(Resources.inputStream(Icons.class, "mycons/macos/maximize.svg"));
	Group restoreGraphic = SVGPaths._parse(Resources.inputStream(Icons.class, "mycons/macos/restore.svg"));
	Group closeGraphic = SVGPaths._parse(Resources.inputStream(Icons.class, "mycons/macos/close.svg"));

	public MacOSStageDecorationSkin(T control) {
		super(control);
		window.getStylesheets().setAll(STYLE_SHEET);

		closeAction.setGraphic(closeGraphic);
		minimizeAction.setGraphic(minimizeGraphic);
		maximizeAction.setGraphic(maximizeGraphic);
		restoreAction.setGraphic(restoreGraphic);

		Circle circle = new Circle(8);
		minimizeButton.setShape(circle);
		resizeButton.setShape(circle);
		closeButton.setShape(circle);
		buttonBar.getChildren().setAll(closeButton, minimizeButton, resizeButton);

		titleBar.getChildren().clear();
		titleBar.setLeft(buttonBar);
		titleBar.setCenter(titleHeader);
		Pane rightBuffer = new Pane();
		rightBuffer.prefWidthProperty().bind(buttonBar.widthProperty());
		rightBuffer.paddingProperty().bind(buttonBar.paddingProperty());
		titleBar.setRight(rightBuffer);

		Rectangle clip = new Rectangle();
		clip.setArcWidth(24);
		clip.setArcHeight(24);
		clip.widthProperty().bind(stagePane.widthProperty());
		clip.heightProperty().bind(stagePane.heightProperty());
		stagePane.setClip(clip);

		minWidthBinding = buttonBar.widthProperty()
				.add(titleHeader.widthProperty())
				.add(rightBuffer.widthProperty())
				.add(horizontalPadding)
				.asObject();

		getStage().minWidthProperty().bind(minWidthBinding);

	}
}
