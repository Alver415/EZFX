package com.fsfx.control.editor;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.CornerRadii;

import java.util.function.Function;

public class CornerRadiiEditor extends EditorControl<CornerRadii> {
	public CornerRadiiEditor(Property<CornerRadii> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CornerRadiiEditorSkin(this);
	}

	public static class CornerRadiiEditorSkin extends SkinBase<CornerRadiiEditor> {
		private CornerRadiiEditorSkin(CornerRadiiEditor control) {
			super(control);

//			DoubleProperty topLeftHorizontalRadius = new SimpleDoubleProperty();
//			DoubleProperty topLeftVerticalRadius = new SimpleDoubleProperty();
//			DoubleProperty topRightHorizontalRadius = new SimpleDoubleProperty();
//			DoubleProperty topRightVerticalRadius = new SimpleDoubleProperty();
//
//			DoubleProperty bottomLeftHorizontalRadius = new SimpleDoubleProperty();
//			DoubleProperty bottomLeftVerticalRadius = new SimpleDoubleProperty();
//			DoubleProperty bottomRightHorizontalRadius = new SimpleDoubleProperty();
//			DoubleProperty bottomRightVerticalRadius = new SimpleDoubleProperty();
//
//			control.propertyProperty().flatMap(Function.identity()).subscribe(radii -> {
//				radii = radii == null ? CornerRadii.EMPTY : radii;
//				topLeftHorizontalRadius.set(radii.getTopLeftHorizontalRadius());
//				topLeftVerticalRadius.set(radii.getTopLeftVerticalRadius());
//				topRightHorizontalRadius.set(radii.getTopRightHorizontalRadius());
//				topRightVerticalRadius.set(radii.getTopRightVerticalRadius());
//
//				bottomLeftHorizontalRadius.set(radii.getBottomLeftHorizontalRadius());
//				bottomLeftVerticalRadius.set(radii.getBottomLeftVerticalRadius());
//				bottomRightHorizontalRadius.set(radii.getBottomRightHorizontalRadius());
//				bottomRightVerticalRadius.set(radii.getBottomRightVerticalRadius());
//			});

			DoubleProperty allRadii = new SimpleDoubleProperty(0d);
			control.propertyProperty().flatMap(Function.identity()).subscribe(radii -> {
				allRadii.set(radii.getTopLeftHorizontalRadius());
			});


			allRadii.subscribe(newValue -> control.getProperty().setValue(new CornerRadii(newValue.doubleValue())));

			getChildren().setAll(new DoubleEditor(allRadii));
		}
	}
}
