package com.ezfx.controls.editor.impl.standard;

import com.ezfx.base.utils.Converter;
import com.ezfx.base.utils.Converters;
import com.ezfx.controls.utils.TextFormatters;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class IntegerEditor extends NumberEditor<Integer> {

	public IntegerEditor() {
		this(new SimpleObjectProperty<>());
	}

	public IntegerEditor(IntegerProperty property) {
		this(property.asObject());
	}

	public IntegerEditor(Property<Integer> property) {
		this(property, null, null);
	}

	public IntegerEditor(IntegerProperty property, Integer min, Integer max) {
		this(property.asObject(), min, max);
	}

	public IntegerEditor(Property<Integer> property, Integer min, Integer max) {
		super(property, min, max);
	}

	@Override
	protected Converter<Number, Integer> numberToValueConverter() {
		return Converters.NUMBER_TO_INTEGER;
	}

	@Override
	protected Converter<String, Integer> stringToValueConverter() {
		return Converters.STRING_TO_INTEGER;
	}

	@Override
	protected UnaryOperator<TextFormatter.Change> textFormatFilter() {
		return TextFormatters.FILTER_INTEGER;
	}
}
