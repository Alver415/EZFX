package com.ezfx.controls.editor;

import com.ezfx.controls.editor.introspective.ActionIntrospector;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionProxy;

import java.util.Optional;

public class EditorBase<T> extends Control implements Editor<T> {

	private final T initialValue;
	private final Property<T> value;

	public EditorBase() {
		this(new SimpleObjectProperty<>());
	}

	public EditorBase(Property<T> value) {
		this.value = value;
		setFocusTraversable(false);

		// TODO: Find a way to make this reactive. Might not be possible
		if (value.isBound()) setDisable(true);

		initialValue = value.getValue();
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

	@Override
	public Node getNode() {
		return this;
	}

	public Property<T> valueProperty() {
		return value;
	}

	public T getValue() {
		return valueProperty().getValue();
	}

	public void setValue(T value) {
		valueProperty().setValue(value);
	}

	private final ListProperty<Action> actions = new SimpleListProperty<>(this, "actions", FXCollections.observableArrayList());

	public ListProperty<Action> actionsProperty() {
		return this.actions;
	}

	public ObservableList<Action> getActions() {
		return this.actionsProperty().getValue();
	}

	public void setActions(ObservableList<Action> value) {
		this.actionsProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new EditorSkinBase<>(this);
	}

	@Override
	public String toString() {
		String className = Optional.of(getClass()).map(Class::getSimpleName).orElse(getClass().getName());
		return "%s{%s=%s}".formatted(className, value.getName(), value.getValue());
	}
}