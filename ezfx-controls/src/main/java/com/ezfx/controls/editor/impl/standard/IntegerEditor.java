package com.ezfx.controls.editor.impl.standard;

import com.ezfx.base.utils.Converters;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.EditorSkin;
import com.ezfx.controls.editor.skin.IntegerFieldSkin;
import com.ezfx.controls.icons.Icons;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class IntegerEditor extends ObjectEditor<Integer> {

	public IntegerEditor(IntegerProperty property) {
		this(property.asObject());
	}

	public IntegerEditor(Property<Integer> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new IntegerFieldSkin(this);
	}

}
