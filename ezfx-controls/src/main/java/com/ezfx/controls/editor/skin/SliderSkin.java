package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;

public class SliderSkin extends EditorSkin<DoubleEditor, Double> {
		// This is needed to prevent weak reference being garbage collected.
		private final ObjectProperty<Double> valueProperty;

		public SliderSkin(DoubleEditor control) {
			super(control);
			Slider slider = new Slider();
			valueProperty = slider.valueProperty().asObject();
			valueProperty.bindBidirectional(control.property());
			slider.orientationProperty().bind(control.orientationProperty());
			slider.minProperty().bind(control.minProperty());
			slider.maxProperty().bind(control.maxProperty());
			slider.setSnapToTicks(true);
//			Tooltip tooltip = new Tooltip();
//			tooltip.textProperty().bind(Bindings.createStringBinding(
//					() -> "%.2f (min=%.2f, max=%.2f)".formatted(slider.getValue(), slider.getMin(), slider.getMax()),
//					slider.valueProperty(), slider.minProperty(), slider.maxProperty()));
//			slider.setTooltip(tooltip);
			getChildren().setAll(slider);
		}
	}