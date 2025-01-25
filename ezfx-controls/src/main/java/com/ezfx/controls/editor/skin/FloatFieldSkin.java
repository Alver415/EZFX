package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.FloatEditor;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.misc.RepeatingButton;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;
import static com.ezfx.base.utils.Converters.STRING_TO_FLOAT;

public class FloatFieldSkin extends EditorSkin<FloatEditor, Float> {
	public FloatFieldSkin(FloatEditor control) {
		super(control);

		Button increment = new RepeatingButton(new ImageView(Icons.PLUS));
		increment.setOnAction(_ -> valueProperty().setValue(valueProperty().getValue() + 1));
		Button decrement = new RepeatingButton(new ImageView(Icons.MINUS));
		decrement.setOnAction(_ -> valueProperty().setValue(valueProperty().getValue() - 1));


		TextField floatField = new TextField();
		floatField.textProperty().subscribe((oldValue, newValue) -> {
			if (!newValue.matches("^-?\\d*\\.?\\d*$")) {
				floatField.setText(oldValue); // Restore old newValue if the input is invalid
			}
		});
		bindBidirectional(floatField.textProperty(), control.valueProperty(), STRING_TO_FLOAT);
		getChildren().setAll(new HBox(floatField, increment, decrement));
	}
}
