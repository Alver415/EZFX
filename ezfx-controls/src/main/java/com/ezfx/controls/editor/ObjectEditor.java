package com.ezfx.controls.editor;

import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public abstract class ObjectEditor<T> extends EditorBase<T> {

	public ObjectEditor() {
		this(new SimpleObjectProperty<>());
	}

	public ObjectEditor(T initialValue) {
		this(new SimpleObjectProperty<>(initialValue));
	}

	public ObjectEditor(Property<T> property) {
		super(property);
	}
	private final MapProperty<String, T> knownValues = new SimpleMapProperty<>(this, "knownValues", FXCollections.observableHashMap());

	public MapProperty<String, T> knownValuesProperty() {
		return this.knownValues;
	}

	public ObservableMap<String, T> getKnownValues() {
		return this.knownValuesProperty().getValue();
	}

	public void setKnownValues(ObservableMap<String, T> value) {
		this.knownValuesProperty().setValue(value);
	}

}