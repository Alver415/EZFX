package com.ezfx.controls.editor;

import javafx.beans.property.Property;
import javafx.scene.Node;

public interface Editor<T> {

	Node getNode();

	Property<T> valueProperty();
	T getValue();
	void setValue(T value);
}
