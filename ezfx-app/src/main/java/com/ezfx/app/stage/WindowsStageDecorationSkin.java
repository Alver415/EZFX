package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;

public class WindowsStageDecorationSkin<T extends StageDecoration> extends StageDecorationSkin<T> {
	private static final String STYLE_SHEET = Resources.css(WindowsStageDecorationSkin.class, "WindowsStageDecorationSkin.css");

	public WindowsStageDecorationSkin(T control) {
		super(control);
		window.getStylesheets().setAll(STYLE_SHEET);
		buttonBar.getChildren().setAll(minimizeButton, resizeButton, closeButton);
	}
}
