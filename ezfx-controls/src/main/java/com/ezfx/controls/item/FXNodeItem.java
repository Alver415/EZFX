package com.ezfx.controls.item;

import javafx.scene.Node;

public class FXNodeItem<T extends Node> extends FXItemBase<T, FXNodeItem<?>> {
	protected FXNodeItem(FXItemFactory factory, T node) {
		super(factory, node);
	}
}