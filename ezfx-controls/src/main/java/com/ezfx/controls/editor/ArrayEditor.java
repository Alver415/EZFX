package com.ezfx.controls.editor;

import com.ezfx.base.observable.ObservableObjectArray;
import com.ezfx.base.utils.TypeReference;
import com.ezfx.controls.editor.introspective.IntrospectingEditor;
import com.ezfx.controls.editor.skin.ArrayEditorSkin;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

import java.lang.reflect.Type;

public class ArrayEditor<T> extends IntrospectingEditor<ObservableObjectArray<T>> {

	public ArrayEditor(Class<T> genericType) {
		super(new TypeReference<ObservableObjectArray<T>>(){}.getType());
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
