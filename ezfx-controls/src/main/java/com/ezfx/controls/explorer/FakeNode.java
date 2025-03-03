package com.ezfx.controls.explorer;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class FakeNode<T> extends Node {

	private final Property<T> actual = new SimpleObjectProperty<>(this, "actual");

	public Property<T> actualProperty() {
		return this.actual;
	}

	public T getActual() {
		return this.actualProperty().getValue();
	}

	public void setActual(T value) {
		this.actualProperty().setValue(value);
	}

	private final ListProperty<Node> children = new SimpleListProperty<>(this, "children", FXCollections.observableArrayList());

	public ListProperty<Node> childrenProperty() {
		return this.children;
	}

	public ObservableList<Node> getChildren() {
		return this.childrenProperty().getValue();
	}

	public void setChildren(ObservableList<Node> value) {
		this.childrenProperty().setValue(value);
	}
}
