package com.ezfx.controls.editor.impl.standard;

import com.ezfx.base.utils.Converter;
import com.ezfx.base.utils.Converters;
import com.ezfx.controls.utils.TextFormatters;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class ShortEditor extends NumberEditor<Short> {

	public ShortEditor() {
		this(new SimpleObjectProperty<>());
	}

	public ShortEditor(Property<Short> property) {
		this(property, null, null);
	}

	public ShortEditor(Property<Short> property, Short min, Short max) {
		super(property, min, max);
	}

	@Override
	Converter<Number, Short> numberToValueConverter() {
		return Converters.NUMBER_TO_SHORT;
	}

	@Override
	Converter<String, Short> stringToValueConverter() {
		return Converters.STRING_TO_SHORT;
	}	@Override
	UnaryOperator<TextFormatter.Change> textFormatFilter() {
		return TextFormatters.FILTER_SHORT;
	}
}
