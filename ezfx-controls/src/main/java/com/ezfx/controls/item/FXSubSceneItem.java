package com.ezfx.controls.item;

import javafx.scene.SubScene;

public class FXSubSceneItem<T extends SubScene> extends FXNodeItem<T> {
	protected FXSubSceneItem(FXItemFactory factory, T subScene) {
		super(factory, subScene);
		subScene.rootProperty()
				.map(factory::create)
				.subscribe(root -> children.setAll(root));
	}
}