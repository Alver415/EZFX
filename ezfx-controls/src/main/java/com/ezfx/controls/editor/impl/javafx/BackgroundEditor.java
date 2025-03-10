package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.paint.Color;
import javafx.util.Subscription;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class BackgroundEditor extends ObjectEditor<Background> {
	public BackgroundEditor() {
		this(new SimpleObjectProperty<>());
	}

	public BackgroundEditor(Property<Background> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BackgroundColorSkin(this);
	}

	public static class BackgroundColorSkin extends EditorSkinBase<BackgroundEditor, Background> {
		private static final Converter<Color, Background> converter = Converter.of(
				Background::fill,
				background -> (Color) Optional.ofNullable(background).stream()
						.map(Background::getFills)
						.flatMap(Collection::stream)
						.map(BackgroundFill::getFill)
						.filter(a -> a instanceof Color)
						.findFirst()
						.orElse(Color.WHITE));

		public BackgroundColorSkin(BackgroundEditor editor) {
			super(editor);
			ColorEditor colorEditor = new ColorEditor();
			colorEditor.setTitle("Background Color");
			bindBidirectional(colorEditor.valueProperty(), editor.valueProperty(), converter);
			setChildren(colorEditor);
		}
	}

	public static class BackgroundEditorSkin extends EditorSkinBase<BackgroundEditor, Background> {

		boolean locked = false;
		Subscription fillsSubscription = () -> {
		};
		Subscription imagesSubscription = () -> {
		};

		public BackgroundEditorSkin(BackgroundEditor editor) {
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
