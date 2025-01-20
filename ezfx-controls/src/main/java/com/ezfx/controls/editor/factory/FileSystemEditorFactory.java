package com.ezfx.controls.editor.factory;

import com.ezfx.base.observable.ObservableObjectArray;
import com.ezfx.controls.editor.ArrayEditor;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.filesystem.PathEditor;
import com.ezfx.controls.editor.impl.standard.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class FileSystemEditorFactory implements EditorFactory {

	@SuppressWarnings("unchecked")
	public <T> Optional<Editor<T>> buildEditor(Class<T> type, Property<T> property) {
		Objects.requireNonNull(property);
		type = boxIfPrimitive(type);
		Editor<T> editor = null;

		if (File.class.equals(type)) {
			editor = (Editor<T>) new PathEditor((Property<Path>) property);
		} else if (Path.class.equals(type)) {
			editor = (Editor<T>) new PathEditor((Property<Path>) property);
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
