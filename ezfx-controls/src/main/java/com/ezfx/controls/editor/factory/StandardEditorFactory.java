package com.ezfx.controls.editor.factory;

import com.ezfx.base.observable.ObservableObjectArray;
import com.ezfx.controls.editor.ArrayEditor;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.standard.*;
import javafx.beans.property.Property;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class StandardEditorFactory implements EditorFactory {

	@SuppressWarnings("unchecked")
	public <T> Optional<Editor<T>> buildEditor(Class<T> type, Property<T> property) {
		Objects.requireNonNull(property);
		type = boxIfPrimitive(type);
		Editor<T> editor = null;

		if (String.class.equals(type)) {
			editor = (Editor<T>) new StringEditor((Property<String>) property);
		} else if (Byte.class.equals(type)) {
			editor =  (Editor<T>) new ByteEditor((Property<Byte>) property);
		} else if (Short.class.equals(type)) {
			editor =  (Editor<T>) new ShortEditor((Property<Short>) property);
		} else if (Integer.class.equals(type)) {
			editor =  (Editor<T>) new IntegerEditor((Property<Integer>) property);
		} else if (Long.class.equals(type)) {
			editor =  (Editor<T>) new LongEditor((Property<Long>) property);
		} else if (Float.class.equals(type)) {
			editor =  (Editor<T>) new FloatEditor((Property<Float>) property);
		} else if (Double.class.equals(type)) {
			editor =  (Editor<T>) new DoubleEditor((Property<Double>) property);
		} else if (Boolean.class.equals(type)) {
			editor =  (Editor<T>) new BooleanEditor((Property<Boolean>) property);
		} else if (Character.class.equals(type)) {
			editor =  (Editor<T>) new CharacterEditor((Property<Character>) property);
		} else if (type.isEnum()) {
			editor =  new SelectionEditor<>(property, List.of(type.getEnumConstants()));
		} else if (type.isArray()) {
			editor =  (Editor<T>) new ArrayEditor<>(
					(Class<T>) type.getComponentType(),
					(Property<ObservableObjectArray<T>>) property);
		}
		return Optional.ofNullable(editor);
	}

	private static <T> Class<T> boxIfPrimitive(Class<T> type) {
		if (!type.isPrimitive()) {
			return type;
		} else if (type.equals(byte.class)) {
			return (Class<T>) Byte.class;
		} else if (type.equals(short.class)) {
			return (Class<T>) Short.class;
		} else if (type.equals(int.class)) {
			return (Class<T>) Integer.class;
		} else if (type.equals(long.class)) {
			return (Class<T>) Long.class;
		} else if (type.equals(float.class)) {
			return (Class<T>) Float.class;
		} else if (type.equals(double.class)) {
			return (Class<T>) Double.class;
		} else if (type.equals(boolean.class)) {
			return (Class<T>) Boolean.class;
		} else if (type.equals(char.class)) {
			return (Class<T>) Character.class;
		}
		return type;
	}

}
