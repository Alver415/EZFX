package com.ezfx.controls.editor.factory;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.filesystem.PathEditor;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class FileSystemEditorFactory implements EditorFactory {

	@SuppressWarnings("unchecked")
	public <T> Optional<Editor<T>> buildEditor(Type type) {
		if (!(type instanceof Class<?> classType)) return Optional.empty();

		Editor<T> editor = null;
		if (File.class.isAssignableFrom(classType)) {
			editor = (Editor<T>) new PathEditor();
		} else if (Path.class.isAssignableFrom(classType)) {
			editor = (Editor<T>) new PathEditor();
		}
		return Optional.ofNullable(editor);
	}
}
