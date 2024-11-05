package com.fsfx.control.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;

public abstract class EditorControl<T> extends Control {

	public EditorControl(Property<T> property) {
		setProperty(property);
	}

	private final ObjectProperty<Property<T>> property = new SimpleObjectProperty<>(this, "property");

	public ObjectProperty<Property<T>> propertyProperty() {
		return this.property;
	}

	public Property<T> getProperty() {
		return this.propertyProperty().get();
	}

	public void setProperty(Property<T> value) {
		this.propertyProperty().set(value);
	}

}