package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.PropertiesEditor;
import com.ezfx.controls.editor.impl.javafx.InsetsEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;

import java.util.List;

public class InsetsEditorSkin extends EditorSkin<InsetsEditor, Insets> {
		public InsetsEditorSkin(InsetsEditor control) {
			super(control);

			DoubleProperty top = new SimpleDoubleProperty();
			DoubleProperty bottom = new SimpleDoubleProperty();
			DoubleProperty right = new SimpleDoubleProperty();
			DoubleProperty left = new SimpleDoubleProperty();

			control.valueProperty().subscribe(insets -> {
				insets = insets == null ? Insets.EMPTY : insets;
				top.set(insets.getTop());
				right.set(insets.getRight());
				bottom.set(insets.getBottom());
				left.set(insets.getLeft());
			});

			List.of(top, right, bottom, left).forEach(property -> property.subscribe(_ ->
					control.valueProperty().setValue(new Insets(top.get(), right.get(), bottom.get(), left.get()))));

			PropertiesEditor<Object> beanEditor = new PropertiesEditor<>();
			beanEditor.setSkin(new MultiEditorSkin<>(beanEditor));
			beanEditor.getEditors().setAll(
					new DoubleEditor(top),
					new DoubleEditor(right),
					new DoubleEditor(bottom),
					new DoubleEditor(left));
			getChildren().setAll(beanEditor);
		}
	}