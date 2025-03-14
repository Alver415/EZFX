package com.ezfx.controls.item;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;

public class FXParentItem<T extends Parent> extends FXNodeItem<T> {
	protected FXParentItem(FXItemFactory factory, T parent) {
		super(factory, parent);
		for (Node child : parent.getChildrenUnmodifiable()) {
			children.add(factory.create(child));
		}
		parent.getChildrenUnmodifiable().addListener((ListChangeListener<? super Node>) change -> {
			while (change.next()) {
				for (Node removed : change.getRemoved()) {
					children.removeIf(item -> item.get() == removed);
				}
				for (Node added : change.getAddedSubList()) {
					children.add(factory.create(added));
				}
			}
		});
	}
}