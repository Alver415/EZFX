package com.ezfx.controls.editor;

import com.ezfx.controls.editor.introspective.GroupedPropertiesEditor;
import com.ezfx.controls.editor.skin.MultiEditorSkin;
import javafx.beans.property.Property;
import javafx.geometry.Orientation;
import javafx.scene.control.Skin;

import java.util.List;
import java.util.function.Function;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public interface Editors {

	record PropertyKey<T, R>(Editor<R> editor, Function<T, Property<R>> propertyFunction) {
	}


	static <T, R> PropertyKey<T, R> key(Editor<R> editor, Function<T, Property<R>> propertyFunction){
		return new PropertyKey<>(editor, propertyFunction);
	}
	static <T> Editor<T> group(Property<T> original, String title, List<PropertyKey<T, ?>> keys) {
		return group(original, title, Orientation.HORIZONTAL, keys);
	}

	static <T> Editor<T> group(Property<T> original, String title, Orientation orientation, List<PropertyKey<T, ?>> keys) {
		GroupedPropertiesEditor<T, PropertyKey<T, ?>> combinedEditor = new GroupedPropertiesEditor<>(keys) {
			@Override
			protected Editor<?> initializeEditor(PropertyKey<T, ?> key) {
				return key.editor();
			}

			@Override
			protected <R> Property<R> getProperty(PropertyKey<T, ?> key, T value) {
				return (Property<R>) key.propertyFunction().apply(value);
			}

			@Override
			protected Skin<?> createDefaultSkin() {
				return orientation == Orientation.HORIZONTAL ?
						new MultiEditorSkin.HorizontalEditorSkin<>(this) :
						new MultiEditorSkin.VerticalEditorSkin<>(this);
			}
		};
		bindBidirectional(original, combinedEditor.valueProperty());
		combinedEditor.setTitle(title);
		return combinedEditor;
	}
}
