package com.ezfx.controls.editor.skin;

import com.ezfx.base.utils.Converters;
import com.ezfx.controls.editor.impl.standard.ShortEditor;
import com.ezfx.controls.icons.Icons;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ShortFieldSkin extends EditorSkin<ShortEditor, Short> {
	public ShortFieldSkin(ShortEditor control) {
		super(control);
		TextField ShortField = new TextField();
		bindBidirectional(ShortField.textProperty(), control.valueProperty(), Converters.STRING_TO_SHORT);
		Button increment = new Button("", new ImageView(Icons.PLUS));
		increment.setOnAction(_ -> property().setValue((short) (property().getValue() + 1)));
		Button decrement = new Button("", new ImageView(Icons.MINUS));
		decrement.setOnAction(_ -> property().setValue((short) (property().getValue() - 1)));
		getChildren().setAll(new HBox(ShortField, increment, decrement));
	}
}