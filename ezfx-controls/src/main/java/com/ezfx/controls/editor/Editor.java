package com.ezfx.controls.editor;

import com.ezfx.controls.editor.skin.EditorSkin;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionProxy;

import java.util.Objects;
import java.util.Optional;

public class Editor<T> extends Control {

	public static final String STYLE_CLASS = "property-editor";
	public static final String STYLE_SHEET = Objects.requireNonNull(
			Editor.class.getResource("PropertyEditor.css")).toExternalForm();

	private final Property<T> property;

	public Editor() {
		this(new SimpleObjectProperty<>());
	}

	public Editor(Property<T> property) {
		this.property = property;
		getStyleClass().add(STYLE_CLASS);
		getStylesheets().add(STYLE_SHEET);
		setFocusTraversable(false);
		if (property.isBound()) setDisable(true);
	}

	public Property<T> property() {
		return property;
	}

	public T getValue(){
		return property().getValue();
	}
	public void setValue(T value){
		property.setValue(value);
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
		return new EditorSkin<>(this);
	}

	@Override
	public String toString() {
		String className = Optional.of(getClass()).map(Class::getSimpleName).orElse(getClass().getName());
		return "%s{%s=%s}".formatted(className, property.getName(), property.getValue());
	}
}