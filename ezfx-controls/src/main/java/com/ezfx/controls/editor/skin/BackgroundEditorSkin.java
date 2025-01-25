package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.ListEditor;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.impl.javafx.BackgroundEditor;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.util.Subscription;

import java.util.List;

public class BackgroundEditorSkin extends EditorSkin<BackgroundEditor, Background> {

	boolean locked = false;
	Subscription fillsSubscription = () -> {
	};
	Subscription imagesSubscription = () -> {
	};

	public BackgroundEditorSkin(BackgroundEditor editor) {
		super(editor);
		ListProperty<BackgroundFill> fills = new SimpleListProperty<>(FXCollections.observableArrayList());
		ListProperty<BackgroundImage> images = new SimpleListProperty<>(FXCollections.observableArrayList());

		editor.valueProperty().subscribe(background -> {
			if (locked) return;
			fills.setAll(background == null ? List.of() : background.getFills());
			images.setAll(background == null ? List.of() : background.getImages());
		});

		Runnable rebuild = () -> {
			locked = true;
			editor.setValue(new Background(fills.get(), images.get()));
			locked = false;
		};
		fills.subscribe(list -> {
			fillsSubscription.unsubscribe();
			imagesSubscription = list.subscribe(rebuild);
			rebuild.run();
		});
		images.subscribe(list -> {
			fillsSubscription.unsubscribe();
			imagesSubscription = list.subscribe(rebuild);
			rebuild.run();
		});
		ListEditor<BackgroundFill> backgroundFillsListEditor = new ListEditor<>(fills);
		backgroundFillsListEditor.setGenericType(BackgroundFill.class);

		ListEditor<BackgroundImage> backgroundImagesListEditor = new ListEditor<>(images);
		backgroundImagesListEditor.setGenericType(BackgroundImage.class);

		PropertiesEditor<Object> beanEditor = new PropertiesEditor<>();
		beanEditor.setSkin(new MultiEditorSkin<>(beanEditor));
		beanEditor.getEditors().setAll(
				backgroundFillsListEditor,
				backgroundImagesListEditor);
		getChildren().setAll(beanEditor);
	}
}