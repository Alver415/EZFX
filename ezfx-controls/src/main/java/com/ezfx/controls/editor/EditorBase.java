package com.ezfx.controls.editor;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.controlsfx.control.action.Action;

import java.util.Optional;

public class EditorBase<T> extends Control implements Editor<T> {

	private final Property<T> value;

	public EditorBase() {
		this(new SimpleObjectProperty<>());
	}

	protected EditorBase(Property<T> value) {
		this.value = value;
		setFocusTraversable(false);
	}

	private final StringProperty title = new SimpleStringProperty(this, "title");

	public StringProperty titleProperty() {
		return this.title;
	}

	public String getTitle() {
		return this.titleProperty().getValue();
	}

	public void setTitle(String value) {
		this.titleProperty().setValue(value);
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