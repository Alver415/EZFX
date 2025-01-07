package com.ezfx.controls.editor.factory;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.introspective.IntrospectingEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import javafx.beans.property.Property;

import java.util.Objects;
import java.util.Optional;

import static com.ezfx.controls.editor.introspective.EZFXIntrospector.DEFAULT_INTROSPECTOR;

public class IntrospectingEditorFactory implements EditorFactory {

	public <T> Optional<Editor<T>> buildEditor(Class<T> type, Property<T> property) {
		Objects.requireNonNull(property);
		Editor<T> editor;
		boolean hasProperties = !DEFAULT_INTROSPECTOR.getPropertyInfo(type).isEmpty();
		if (hasProperties) {
			editor = new IntrospectingPropertiesEditor<>(property);
		} else {
			editor = new IntrospectingEditor<>(property, type);
		}
		return Optional.of(editor);
	}
}
