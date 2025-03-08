package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.Category;
import com.ezfx.controls.editor.ListEditor;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.util.Subscription;

import java.util.List;
import java.util.Map;

public class BackgroundEditor extends ObjectEditor<Background> {
	public BackgroundEditor() {
		this(new SimpleObjectProperty<>());
	}

	public BackgroundEditor(Property<Background> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<BackgroundEditor, Background> {

		boolean locked = false;
		Subscription fillsSubscription = () -> {
		};
		Subscription imagesSubscription = () -> {
		};

		public DefaultSkin(BackgroundEditor editor) {
			super(editor);
			ListProperty<BackgroundFill> fills = new SimpleListProperty<>(this, "fills", FXCollections.observableArrayList());
			ListProperty<BackgroundImage> images = new SimpleListProperty<>(this, "images", FXCollections.observableArrayList());

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
			beanEditor.editorsProperty()
					.map(editors -> Map.of(Category.of("All"), editors))
					.subscribe(beanEditor.categorizedEditorsProperty()::putAll);
			beanEditor.getEditors().setAll(
					backgroundFillsListEditor,
					backgroundImagesListEditor);
			getChildren().setAll(beanEditor);
		}
	}
}
