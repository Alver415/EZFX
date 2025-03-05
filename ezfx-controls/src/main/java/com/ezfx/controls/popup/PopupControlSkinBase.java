package com.ezfx.controls.popup;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class PopupControlSkinBase<T extends PopupControl> implements Skin<T> {

	protected final T control;
	protected final Pane root;

	protected PopupControlSkinBase(T control) {
		this.control = control;
		this.root = new Pane();
	}

	public ObservableList<Node> getChildren() {
		return root.getChildren();
	}

	@Override
	public T getSkinnable() {
		return control;
	}

	@Override
	public Node getNode() {
		return root;
	}

	@Override
	public void dispose() {

	}
}
