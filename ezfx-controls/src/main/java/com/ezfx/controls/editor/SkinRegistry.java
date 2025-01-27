package com.ezfx.controls.editor;

import com.ezfx.controls.editor.code.CSSEditorSkin;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.editor.skin.TextAreaSkin;
import com.ezfx.controls.editor.skin.TextFieldSkin;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;

import java.util.function.Function;

import static javafx.collections.FXCollections.observableArrayList;

public class SkinRegistry {

	public SkinRegistry() {
		register(StringEditor.class, new SkinOption<>(TextFieldSkin.class.getSimpleName(),TextFieldSkin::new));
		register(StringEditor.class, new SkinOption<>(TextAreaSkin.class.getSimpleName(),TextAreaSkin::new));
		register(StringEditor.class, new SkinOption<>(CSSEditorSkin.class.getSimpleName(), CSSEditorSkin::new));
	}

	private final MapProperty<Class<? extends Skinnable>, ListProperty<SkinOption<? extends Skinnable>>> registry =
			new SimpleMapProperty<>(this, "registry", FXCollections.observableHashMap());

	public MapProperty<Class<? extends Skinnable>, ListProperty<SkinOption<? extends Skinnable>>> registryProperty() {
		return registry;
	}

	public ObservableMap<Class<? extends Skinnable>, ListProperty<SkinOption<? extends Skinnable>>> getRegistry() {
		return this.registryProperty().getValue();
	}

	public void setRegistry(ObservableMap<Class<? extends Skinnable>, ListProperty<SkinOption<? extends Skinnable>>> value) {
		this.registryProperty().setValue(value);
	}

	public <T extends Skinnable> void register(Class<? extends T> key, SkinOption<? extends T> value) {
		ListProperty<SkinOption<? extends Skinnable>> list = registry
				.computeIfAbsent(key, _ -> new SimpleListProperty<>(observableArrayList()));
		list.add(value);
	}

	public <T extends Skinnable> ObservableList<SkinOption<T>> getSkinBuilder(T key) {
		return (ObservableList<SkinOption<T>>) (ListProperty<?>) registry.get(key.getClass());
	}

	public static class SkinOption<T extends Skinnable> {
		private final String name;
		private final Function<T, Skin<T>> function;

		public SkinOption(String name, Function<T, Skin<T>> function) {
			this.name = name;
			this.function = function;
		}

		public String getName() {
			return name;
		}

		public Function<T, Skin<T>> getFunction() {
			return function;
		}
	}

}
