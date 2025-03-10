package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.EditorSkinBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Skin;
import javafx.scene.layout.CornerRadii;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class CornerRadiiEditor extends ObjectEditor<CornerRadii> {

	public CornerRadiiEditor() {
		super();
	}

	public CornerRadiiEditor(ObjectProperty<CornerRadii> radii) {
		super();
		bindBidirectional(radii, valueProperty());
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends EditorSkinBase<CornerRadiiEditor, CornerRadii> {
		public DefaultSkin(CornerRadiiEditor control) {
			super(control);

			DoubleProperty allRadii = new SimpleDoubleProperty(0d);
			control.valueProperty().orElse(CornerRadii.EMPTY)
					.subscribe(radii -> allRadii.set(radii.getTopLeftHorizontalRadius()));

			allRadii.subscribe(newValue -> control.valueProperty().setValue(new CornerRadii(newValue.doubleValue())));

			getChildren().setAll(new DoubleEditor(allRadii));
		}
	}

}
