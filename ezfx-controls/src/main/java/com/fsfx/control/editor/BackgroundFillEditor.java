package com.fsfx.control.editor;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;
import java.util.function.Function;

public class BackgroundFillEditor extends EditorControl<BackgroundFill> {
	public BackgroundFillEditor(Property<BackgroundFill> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BackgroundFillEditorSkin(this);
	}

	public static class BackgroundFillEditorSkin extends SkinBase<BackgroundFillEditor> {

		private BackgroundFillEditorSkin(BackgroundFillEditor control) {
			super(control);
			ObjectProperty<Paint> fill = new SimpleObjectProperty<>();
			ObjectProperty<CornerRadii> radii = new SimpleObjectProperty<>();
			ObjectProperty<Insets> insets = new SimpleObjectProperty<>();

			ObjectProperty<Color> color = new SimpleObjectProperty<>();

			control.propertyProperty().flatMap(Function.identity()).subscribe(backgroundFill -> {
				if (backgroundFill.getFill() instanceof Color c) color.set(c);
				fill.set(backgroundFill.getFill());
				radii.set(backgroundFill.getRadii());
				insets.set(backgroundFill.getInsets());
			});

			List.of(fill, radii, insets, color).forEach(property -> property.subscribe(_ ->
					control.getProperty().setValue(new BackgroundFill(color.get(), radii.get(), insets.get()))));

			BeanEditor<Object> beanEditor = new BeanEditor<>();
			beanEditor.getEditors().setAll(
					new Editor<>("Fill", new ColorSelectionEditor(color)),
					new Editor<>("Radii", new CornerRadiiEditor(radii)),
					new Editor<>("Insets", new InsetsEditor(insets)));
			getChildren().setAll(beanEditor);
		}
	}
}
