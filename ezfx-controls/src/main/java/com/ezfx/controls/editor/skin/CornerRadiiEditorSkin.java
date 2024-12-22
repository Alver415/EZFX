package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.javafx.CornerRadiiEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.CornerRadii;

public class CornerRadiiEditorSkin extends EditorSkin<CornerRadiiEditor, CornerRadii> {
		public CornerRadiiEditorSkin(CornerRadiiEditor control) {
			super(control);

			DoubleProperty allRadii = new SimpleDoubleProperty(0d);
			control.valueProperty().subscribe(radii -> {
				allRadii.set(radii.getTopLeftHorizontalRadius());
			});


			allRadii.subscribe(newValue -> control.valueProperty().setValue(new CornerRadii(newValue.doubleValue())));

			getChildren().setAll(new DoubleEditor(allRadii));
		}
	}