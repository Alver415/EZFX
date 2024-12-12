package com.ezfx.controls.editor;

import com.ezfx.controls.editor.introspective.ActionIntrospector;
import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.controlsfx.control.action.ActionProxy;

public abstract class ObjectEditor<T> extends Editor<T> {

	private final T initialValue;

	public ObjectEditor() {
		this(new SimpleObjectProperty<>());
	}

	public ObjectEditor(T initialValue) {
		this(new SimpleObjectProperty<>(initialValue));
	}

	public ObjectEditor(Property<T> property) {
		super(property);
		initialValue = property.getValue();
		setupActions();
	}

	@ActionProxy(id = "clear", text = "Clear", longText = "Set value to null", graphic = "font>FontAwesome|TIMES")
	public void clear() {
		setValue(null);
	}

	@ActionProxy(id = "reset", text = "Reset", longText = "Reset value to initial value", graphic = "font>FontAwesome|UNDO")
	public void reset() {
		setValue(initialValue);
	}

	private void setupActions() {
		ActionIntrospector.register(this);
		getActions().addAll(ActionIntrospector.actions("reset", "clear"));
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