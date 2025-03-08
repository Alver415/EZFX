package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import com.ezfx.controls.editor.skin.MultiEditorSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Skin;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

public class BackgroundFillEditor extends ObjectEditor<BackgroundFill> {
	public BackgroundFillEditor(Property<BackgroundFill> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<BackgroundFillEditor, BackgroundFill> {

		public DefaultSkin(BackgroundFillEditor control) {
			super(control);
			ObjectProperty<Paint> fill = new SimpleObjectProperty<>(this, "fill");
			ObjectProperty<CornerRadii> radii = new SimpleObjectProperty<>(this, "radii");
			ObjectProperty<Insets> insets = new SimpleObjectProperty<>(this, "insets");

			ObjectProperty<Color> color = new SimpleObjectProperty<>();

			control.valueProperty().subscribe(backgroundFill -> {
				if (backgroundFill == null) {
					color.set(Color.TRANSPARENT);
					return;
				}
				if (backgroundFill.getFill() instanceof Color c) {
					color.set(c);
				}
				fill.set(backgroundFill.getFill());
				radii.set(backgroundFill.getRadii());
				insets.set(backgroundFill.getInsets());
			});

			List.of(fill, radii, insets, color).forEach(property -> property.subscribe(_ ->
					control.valueProperty().setValue(new BackgroundFill(color.get(), radii.get(), insets.get()))));

			PropertiesEditor<Object> beanEditor = new PropertiesEditor<>();
			beanEditor.setSkin(new MultiEditorSkin.VerticalEditorSkin<>(beanEditor));
			beanEditor.getEditors().setAll(
					new ColorEditor(color),
					new CornerRadiiEditor(radii),
					new InsetsEditor(insets));
			getChildren().setAll(beanEditor);
		}
	}

}
