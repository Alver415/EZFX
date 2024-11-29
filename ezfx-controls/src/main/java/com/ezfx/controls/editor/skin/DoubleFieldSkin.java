package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.misc.RepeatingButton;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;
import static com.ezfx.base.utils.Converters.STRING_TO_DOUBLE;

public class DoubleFieldSkin extends EditorSkin<DoubleEditor, Double> {
	public DoubleFieldSkin(DoubleEditor control) {
		super(control);

		Button increment = new RepeatingButton(new ImageView(Icons.PLUS));
		increment.setOnAction(_ -> property().setValue(property().getValue() + 1));
		Button decrement = new RepeatingButton(new ImageView(Icons.MINUS));
		decrement.setOnAction(_ -> property().setValue(property().getValue() - 1));


		TextField doubleField = new TextField();
		doubleField.textProperty().subscribe((oldValue, newValue) -> {
			if (!newValue.matches("^-?\\d*\\.?\\d*$")) {
				doubleField.setText(oldValue); // Restore old newValue if the input is invalid
			}
		});
		bindBidirectional(doubleField.textProperty(), control.property(), STRING_TO_DOUBLE);
		getChildren().setAll(new HBox(doubleField, increment, decrement));
	}
}