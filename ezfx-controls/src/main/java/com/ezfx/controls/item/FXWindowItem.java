package com.ezfx.controls.item;

import javafx.stage.Window;

public class FXWindowItem<T extends Window> extends FXItemBase<T, FXSceneItem<?>> {
	protected FXWindowItem(FXItemFactory factory, T window) {
		super(factory, window);
		window.sceneProperty()
				.map(factory::create)
				.subscribe(scene -> children.setAll(scene));
	}
}