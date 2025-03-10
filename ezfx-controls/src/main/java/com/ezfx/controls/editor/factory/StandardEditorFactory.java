package com.ezfx.controls.editor.factory;

import com.ezfx.controls.editor.ArrayEditor;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.standard.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class StandardEditorFactory implements EditorFactory {

	@SuppressWarnings("unchecked")
	public <T> Optional<Editor<T>> buildEditor(Type type) {
		if (!(type instanceof Class<?> classType)) return Optional.empty();
		classType = boxIfPrimitive(classType);
		Editor<T> editor = null;

		if (String.class.equals(classType)) {
			editor = (Editor<T>) new StringEditor();
		} else if (Byte.class.equals(classType)) {
			editor = (Editor<T>) new ByteEditor();
		} else if (Short.class.equals(classType)) {
			editor = (Editor<T>) new ShortEditor();
		} else if (Integer.class.equals(classType)) {
			editor = (Editor<T>) new IntegerEditor();
		} else if (Long.class.equals(classType)) {
			editor = (Editor<T>) new LongEditor();
		} else if (Float.class.equals(classType)) {
			editor = (Editor<T>) new FloatEditor();
		} else if (Double.class.equals(classType)) {
			editor = (Editor<T>) new DoubleEditor();
		} else if (Boolean.class.equals(classType)) {
			editor = (Editor<T>) new BooleanEditor();
		} else if (Character.class.equals(classType)) {
			editor = (Editor<T>) new CharacterEditor();
		} else if (classType.isEnum()) {
			editor = (Editor<T>) new SelectionEditor<>(List.of(classType.getEnumConstants()));
		}
//		} else if (classType.isArray()) {
//			editor = (Editor<T>) new ArrayEditor<>((Class<T>) classType.getComponentType());
//		}
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
