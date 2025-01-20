package com.ezfx.app.console;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.graalvm.polyglot.Value;

public class ContextBinding {
	public ContextBinding(String key, Value value) {
		setKey(key);
		setValue(String.valueOf(value));
	}

	private final StringProperty key = new SimpleStringProperty(this, "key");

	public StringProperty keyProperty() {
		return this.key;
	}

	public String getKey() {
		return this.keyProperty().getValue();
	}

	public void setKey(String value) {
		this.keyProperty().setValue(value);
	}

	private final StringProperty value = new SimpleStringProperty(this, "value");

	public StringProperty valueProperty() {
		return this.value;
	}

	public String getValue() {
		return this.valueProperty().getValue();
	}

	public void setValue(String value) {
		this.valueProperty().setValue(value);
	}
}