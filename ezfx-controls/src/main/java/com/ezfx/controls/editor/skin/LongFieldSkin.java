package com.ezfx.controls.editor.skin;

import com.ezfx.base.utils.Converters;
import com.ezfx.controls.editor.impl.standard.LongEditor;
import com.ezfx.controls.icons.Icons;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class LongFieldSkin extends EditorSkin<LongEditor, Long> {
	public LongFieldSkin(LongEditor control) {
		super(control);
		TextField longField = new TextField();
		bindBidirectional(longField.textProperty(), control.valueProperty(), Converters.STRING_TO_LONG);
		Button increment = new Button("", new ImageView(Icons.PLUS));
		increment.setOnAction(_ -> valueProperty().setValue(valueProperty().getValue() + 1));
		Button decrement = new Button("", new ImageView(Icons.MINUS));
		decrement.setOnAction(_ -> valueProperty().setValue(valueProperty().getValue() - 1));
		getChildren().setAll(new HBox(longField, increment, decrement));
	}
}