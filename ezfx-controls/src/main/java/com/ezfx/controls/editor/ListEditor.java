package com.ezfx.controls.editor;

import com.ezfx.controls.editor.introspective.IntrospectingEditor;
import com.ezfx.controls.editor.skin.ListEditorSkin;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

public class ListEditor<T> extends IntrospectingEditor<ObservableList<T>> {

	public ListEditor() {
		this(new SimpleListProperty<>(FXCollections.observableArrayList()));
	}

	public ListEditor(ObservableList<T> list) {
		this(new SimpleListProperty<>(list));
	}

	public ListEditor(ListProperty<T> property) {
		this((Property<ObservableList<T>>) property);
	}

	public ListEditor(Property<ObservableList<T>> property) {
		super(property, (Class<ObservableList<T>>) property.getValue().getClass());
	}

	private final Property<Class<T>> genericType = new SimpleObjectProperty<>(this, "genericType");

	public Property<Class<T>> genericTypeProperty() {
		return this.genericType;
	}

	public Class<T> getGenericType() {
		return this.genericTypeProperty().getValue();
	}

	public void setGenericType(Class<T> value) {
		this.genericTypeProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ListEditorSkin<>(this);
	}

}
