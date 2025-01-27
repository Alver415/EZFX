package com.ezfx.controls.editor.impl.standard;

import com.ezfx.base.utils.Converter;
import com.ezfx.base.utils.Converters;
import com.ezfx.controls.utils.TextFormatters;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class FloatEditor extends NumberEditor<Float> {

	public FloatEditor() {
		this(new SimpleObjectProperty<>());
	}

	public FloatEditor(FloatProperty property) {
		this(property.asObject());
	}

	public FloatEditor(Property<Float> property) {
		this(property, null, null);
	}

	public FloatEditor(FloatProperty property, Float min, Float max) {
		this(property.asObject(), min, max);
	}

	public FloatEditor(Property<Float> property, Float min, Float max) {
		super(property, min, max);
	}

	@Override
	Converter<Number, Float> numberToValueConverter() {
		return Converters.NUMBER_TO_FLOAT;
	}

	@Override
	Converter<String, Float> stringToValueConverter() {
		return Converters.STRING_TO_FLOAT;
	}

	@Override
	UnaryOperator<TextFormatter.Change> textFormatFilter() {
		return TextFormatters.FILTER_FLOAT;
	}
}
