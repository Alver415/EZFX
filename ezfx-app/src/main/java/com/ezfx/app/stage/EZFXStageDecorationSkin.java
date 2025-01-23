package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;

public class EZFXStageDecorationSkin<T extends StageDecoration> extends StageDecorationSkin<T> {

	private static final String STYLE_SHEET = Resources.css(EZFXStageDecorationSkin.class, "EZFXStageDecorationSkin.css");

	public EZFXStageDecorationSkin(T control) {
		super(control);
		window.getStylesheets().setAll(STYLE_SHEET);
	}

}
