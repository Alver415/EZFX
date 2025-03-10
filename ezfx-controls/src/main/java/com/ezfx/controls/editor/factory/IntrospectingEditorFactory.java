package com.ezfx.controls.editor.factory;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.introspective.IntrospectingEditor;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;

import java.lang.reflect.Type;
import java.util.Optional;

import static com.ezfx.controls.editor.introspective.EZFXIntrospector.DEFAULT_INTROSPECTOR;

public class IntrospectingEditorFactory implements EditorFactory {

	public <T> Optional<Editor<T>> buildEditor(Type type) {
		Editor<T> editor;
		boolean hasProperties = !DEFAULT_INTROSPECTOR.getPropertyInfo(type).isEmpty();
		if (hasProperties) {
			editor = new IntrospectingPropertiesEditor<>();
		} else {
			editor = new IntrospectingEditor<>(type);
		}
		return Optional.of(editor);
	}
}
