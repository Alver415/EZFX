package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.util.Subscription;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ColorEditor extends ObjectEditor<Color> {

	public ColorEditor() {
		super(new SimpleObjectProperty<>());
	}
	public ColorEditor(Property<Color> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<ColorEditor, Color> {

		public DefaultSkin(ColorEditor editor) {
			super(editor);
			ColorPicker colorPicker = new ColorPicker();
			colorPicker.setMinHeight(24);
			subscription = bindBidirectional(colorPicker.valueProperty(), valueProperty());
			setChildren(colorPicker);
		}

		Subscription subscription;

		@Override
		public void dispose() {
			super.dispose();
			subscription.unsubscribe();
		}
	}

}
