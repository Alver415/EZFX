package com.fsfx.control.editor;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.List;

public class BackgroundEditor extends EditorControl<Background> {
	public BackgroundEditor(Property<Background> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BackgroundEditorSkin(this);
	}

	public static class BackgroundEditorSkin extends SkinBase<BackgroundEditor> {

		boolean locked = false;
		private BackgroundEditorSkin(BackgroundEditor control) {
			super(control);
			ListProperty<BackgroundFill> fills = new SimpleListProperty<>(FXCollections.observableArrayList());

			control.getProperty().subscribe(background -> {
				if (locked) return;
				fills.setAll(background == null ? List.of() : background.getFills());
			});

			Runnable rebuild = () -> {
				locked = true;
				control.getProperty().setValue(new Background(fills.get(), List.of()));
				locked = false;
			};
			fills.subscribe(list -> {
				list.addListener((InvalidationListener) _ -> rebuild.run());
				rebuild.run();
			});

			BeanEditor<Object> beanEditor = new BeanEditor<>();
			ListEditor<BackgroundFill> backgroundFillListEditor = new ListEditor<>(fills);
			backgroundFillListEditor.setCreateElementHook(() -> new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY));
			beanEditor.getEditors().setAll(new Editor<>("Fills", backgroundFillListEditor));
			getChildren().setAll(beanEditor);
		}
	}
}
