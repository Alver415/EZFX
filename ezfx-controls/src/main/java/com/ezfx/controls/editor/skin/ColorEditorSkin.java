package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.javafx.ColorEditor;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.util.Subscription;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ColorEditorSkin extends EditorSkin<ColorEditor, Color> {


	public ColorEditorSkin(ColorEditor editor) {
		super(editor);
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setMinHeight(24);
		subscription = bindBidirectional(colorPicker.valueProperty(), property());
		setChildren(colorPicker);
	}

	Subscription subscription;

	@Override
	public void dispose() {
		super.dispose();
		subscription.unsubscribe();
	}
}