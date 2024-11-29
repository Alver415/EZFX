package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.impl.standard.SelectionEditor;
import com.ezfx.controls.editor.skin.BlendModeEditorSkin;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.effect.BlendMode;

import java.util.Arrays;

public class BlendModeEditor extends SelectionEditor<BlendMode> {

	private static final ObservableList<BlendMode> BLEND_MODE_VALUES =
			FXCollections.observableList(Arrays.asList(BlendMode.values()));

	public BlendModeEditor(Property<BlendMode> property) {
		this(property, BLEND_MODE_VALUES);
	}

	public BlendModeEditor(Property<BlendMode> property, ObservableList<BlendMode> options) {
		super(property, options);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BlendModeEditorSkin(this);
	}


}
