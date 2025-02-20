package com.ezfx.controls.editor.factory;

import com.ezfx.controls.editor.Editor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface EditorFactory {

	EditorFactory DEFAULT_FACTORY = composite(
			new EZFXEditorFactory(), // First, check if there's EZFX Editor.
			new FileSystemEditorFactory(),
			new StandardEditorFactory(), // Then default to basic primitive, enum, array, etc.
			new IntrospectingEditorFactory(), // Finally, fall back to building editor via property or constructor introspection
			new IntrospectingEditorFactory() // Finally, fall back to building editor via property or constructor introspection
	);

	default <T> Optional<Editor<T>> buildEditor(Class<T> type) {
		return buildEditor(type, new SimpleObjectProperty<>());
	}

	default <T> Optional<Editor<T>> buildEditor(T value) {
		return buildEditor((Class<T>) value.getClass(), new SimpleObjectProperty<>(value));
	}
	default <T> Optional<Editor<T>> buildEditor(Property<T> property) {
		//TODO: Handle nullpointer of property.getValue().
		// Wrap it in delegating editor that changes when the value changes away from null?..
		return buildEditor((Class<T>) property.getValue().getClass(), property);
	}

	<T> Optional<Editor<T>> buildEditor(Class<T> type, Property<T> property);

	static EditorFactory composite(EditorFactory... factories) {
		return new EditorFactory() {
			@Override
			public <T> Optional<Editor<T>> buildEditor(Class<T> type, Property<T> property) {
				for (EditorFactory factory : factories) {
					Optional<Editor<T>> editor = factory.buildEditor(type, property);
					if (editor.isPresent()) {
						return editor;
					}
				}
				return Optional.empty();
			}
		};
	}

}
