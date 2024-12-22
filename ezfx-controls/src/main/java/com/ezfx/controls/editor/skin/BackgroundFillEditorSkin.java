package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.impl.javafx.BackgroundFillEditor;
import com.ezfx.controls.editor.impl.javafx.ColorEditor;
import com.ezfx.controls.editor.impl.javafx.CornerRadiiEditor;
import com.ezfx.controls.editor.impl.javafx.InsetsEditor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

public class BackgroundFillEditorSkin extends EditorSkin<BackgroundFillEditor, BackgroundFill> {

		public BackgroundFillEditorSkin(BackgroundFillEditor control) {
			super(control);
			ObjectProperty<Paint> fill = new SimpleObjectProperty<>();
			ObjectProperty<CornerRadii> radii = new SimpleObjectProperty<>();
			ObjectProperty<Insets> insets = new SimpleObjectProperty<>();

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
			beanEditor.setSkin(new MultiEditorSkin<>(beanEditor));
			beanEditor.getEditors().setAll(
					new ColorEditor(color),
					new CornerRadiiEditor(radii),
					new InsetsEditor(insets));
			getChildren().setAll(beanEditor);
		}
	}