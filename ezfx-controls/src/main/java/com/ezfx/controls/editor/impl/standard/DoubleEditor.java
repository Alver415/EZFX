package com.ezfx.controls.editor.impl.standard;

import com.ezfx.base.utils.Converter;
import com.ezfx.base.utils.Converters;
import com.ezfx.controls.utils.TextFormatters;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class DoubleEditor extends NumberEditor<Double> {

	public DoubleEditor() {
		this(new SimpleObjectProperty<>());
	}

	public DoubleEditor(DoubleProperty property) {
		this(property.asObject());
	}

	public DoubleEditor(Property<Double> property) {
		this(property, null, null);
	}

	public DoubleEditor(DoubleProperty property, Double min, Double max) {
		this(property.asObject(), min, max);
	}

	public DoubleEditor(Property<Double> property, Double min, Double max) {
		super(property, min, max);
	}

	@Override
	Converter<Number, Double> numberToValueConverter() {
		return Converters.NUMBER_TO_DOUBLE;
	}

	@Override
	Converter<String, Double> stringToValueConverter() {
		return Converters.STRING_TO_DOUBLE;
	}

	@Override
	UnaryOperator<TextFormatter.Change> textFormatFilter() {
		return TextFormatters.FILTER_DOUBLE;
	}
}
