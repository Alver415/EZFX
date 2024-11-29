package com.ezfx.controls.editor;

import com.ezfx.base.observable.ObservableObjectArray;
import com.ezfx.controls.editor.introspective.IntrospectingEditor;
import com.ezfx.controls.editor.skin.ArrayEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

public class ArrayEditor<T> extends IntrospectingEditor<ObservableObjectArray<T>> {

	public ArrayEditor(Class<T> genericType, Property<ObservableObjectArray<T>> property) {
		super(property, (Class<ObservableObjectArray<T>>) property.getValue().getClass());
		setGenericType(genericType);
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
		return new ArrayEditorSkin<>(this);
	}

}
