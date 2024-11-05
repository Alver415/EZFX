package com.fsfx.control.editor;

import com.ezfx.base.utils.Converter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import static com.ezfx.base.MappedBinding.bindBidirectional;

public class DoubleEditor extends EditorControl<Double> {


	public DoubleEditor(DoubleProperty property) {
		this(property.asObject());
	}

	public DoubleEditor(Property<Double> property) {
		super(property);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DoubleFieldSkin(this);
	}


	public static class DoubleFieldSkin extends SkinBase<DoubleEditor> {
		protected DoubleFieldSkin(DoubleEditor control) {
			super(control);

			Button up = new Button();
			up.setOnAction(a -> control.getProperty().setValue(control.getProperty().getValue() + 1));
			Button down = new Button();
			down.setOnAction(a -> control.getProperty().setValue(control.getProperty().getValue() - 1));


			TextField doubleField = new TextField();
			doubleField.textProperty().subscribe((oldValue, newValue) -> {
				if (!newValue.matches("^-?\\d*\\.?\\d*$")) {
					doubleField.setText(oldValue); // Restore old newValue if the input is invalid
				}
			});
			Converter<String, Double> converter = Converter.of(this::to, this::from);
			bindBidirectional(doubleField.textProperty(), control.getProperty(), converter);
			getChildren().setAll(new HBox(doubleField, up, down));
		}

		private String from(Double value) {
			return String.valueOf(value);
		}

		private Double to(String string) {
			try {
				return Double.parseDouble(string);
			} catch (NumberFormatException e) {
				return null;
			}
		}
	}

	public static class SliderSkin extends SkinBase<DoubleEditor> {
		protected SliderSkin(DoubleEditor control) {
			super(control);
			Slider slider = new Slider();
			slider.valueProperty().asObject().bindBidirectional(control.getProperty());
			getChildren().setAll(slider);
		}
	}
}
