package com.ezfx.controls.editor.skin;

import com.ezfx.base.utils.Converters;
import com.ezfx.controls.editor.impl.standard.IntegerEditor;
import com.ezfx.controls.icons.Icons;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class IntegerFieldSkin extends EditorSkin<IntegerEditor, Integer> {
	public IntegerFieldSkin(IntegerEditor control) {
		super(control);
		TextField integerField = new TextField();
		bindBidirectional(integerField.textProperty(), control.valueProperty(), Converters.STRING_TO_INTEGER);
		Button increment = new Button("", new ImageView(Icons.PLUS));
		increment.setOnAction(a -> property().setValue(property().getValue() + 1));
		Button decrement = new Button("", new ImageView(Icons.MINUS));
		decrement.setOnAction(a -> property().setValue(property().getValue() - 1));
		getChildren().setAll(new HBox(integerField, increment, decrement));
	}
}