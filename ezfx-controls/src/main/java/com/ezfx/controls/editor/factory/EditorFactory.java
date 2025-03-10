package com.ezfx.controls.editor.factory;

import com.ezfx.controls.editor.Editor;

import java.lang.reflect.Type;
import java.util.Optional;

@FunctionalInterface
public interface EditorFactory {

	EditorFactory DEFAULT_FACTORY = composite(
			new EZFXEditorFactory(), // First, check if there's EZFX Editor.
			new FileSystemEditorFactory(),
			new StandardEditorFactory(), // Then default to basic primitive, enum, array, etc.
			new IntrospectingEditorFactory() // Finally, fall back to building editor via property or constructor introspection
	);

	<T> Optional<Editor<T>> buildEditor(Type type);
	default <T> Optional<Editor<T>> buildEditor(Class<T> type){
		return buildEditor((Type) type);
	}

	static EditorFactory composite(EditorFactory... factories) {
		return new EditorFactory() {
			@Override
			public <T> Optional<Editor<T>> buildEditor(Type type) {
				for (EditorFactory factory : factories) {
					Optional<Editor<T>> editor = factory.buildEditor(type);
					if (editor.isPresent()) {
						return editor;
					}
				}
				return Optional.empty();
			}
		};
	}

}
