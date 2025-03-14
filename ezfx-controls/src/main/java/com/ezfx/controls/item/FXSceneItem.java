package com.ezfx.controls.item;

import javafx.scene.Scene;

public class FXSceneItem<T extends Scene> extends FXItemBase<T, FXParentItem<?>> {
	protected FXSceneItem(FXItemFactory factory, T scene) {
		super(factory, scene);
		scene.rootProperty()
				.map(factory::create)
				.subscribe(root -> children.setAll(root));
	}
}
