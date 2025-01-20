package com.ezfx.app.console;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class ContextBindings {

	private final MapProperty<String, Object> bindings = new SimpleMapProperty<>(this, "bindings", FXCollections.observableHashMap());

	public MapProperty<String, Object> bindingsProperty() {
		return this.bindings;
	}

	public ObservableMap<String, Object> getBindings() {
		return this.bindingsProperty().getValue();
	}

	public void setBindings(ObservableMap<String, Object> value) {
		this.bindingsProperty().setValue(value);
	}
}
