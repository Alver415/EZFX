package com.fsfx.control.editor;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.text.Font;

import java.util.List;
import java.util.function.Function;

public class FontEditor extends EditorControl<Font> {
	public FontEditor(Property<Font> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new FontEditorSkin(this);
	}

	public static class FontEditorSkin extends SkinBase<FontEditor> {

		private FontEditorSkin(FontEditor control) {
			super(control);
			StringProperty family = new SimpleStringProperty();
			StringProperty name = new SimpleStringProperty();
			DoubleProperty size = new SimpleDoubleProperty();

			ListProperty<String> fontFamilies = new SimpleListProperty<>(FXCollections.observableArrayList(Font.getFamilies()));
			ListProperty<String> fontNames = new SimpleListProperty<>(FXCollections.observableArrayList());

			family.map(Font::getFontNames).subscribe(names -> fontNames.setAll(names == null ? List.of() : names));
			fontNames.subscribe(names -> name.set(names.isEmpty() ? null : names.getFirst()));

			control.propertyProperty().flatMap(Function.identity()).subscribe(font -> {
				family.set(font.getFamily());
				name.set(font.getName());
				size.set(font.getSize());
			});

			List.of(family, name, size).forEach(property -> property.subscribe(_ ->
					control.getProperty().setValue(new Font(name.get(), size.get()))));

			BeanEditor<Object> beanEditor = new BeanEditor<>();
			beanEditor.setSkin(new BeanEditorSkinBase.HBoxSkin<>(beanEditor));
			beanEditor.getEditors().setAll(
					new Editor<>("Family", new SelectionEditor<>(family, fontFamilies)),
					new Editor<>("Name", new SelectionEditor<>(name, fontNames)),
					new Editor<>("Size", new DoubleEditor(size)));
			getChildren().setAll(beanEditor);
		}

	}
}
