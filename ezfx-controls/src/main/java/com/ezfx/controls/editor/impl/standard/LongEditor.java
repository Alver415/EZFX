package com.ezfx.controls.editor.impl.standard;

import com.ezfx.base.utils.Converter;
import com.ezfx.base.utils.Converters;
import com.ezfx.controls.utils.TextFormatters;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class LongEditor extends NumberEditor<Long> {

	public LongEditor() {
		this(new SimpleObjectProperty<>());
	}

	public LongEditor(LongProperty property) {
		this(property.asObject());
	}

	public LongEditor(Property<Long> property) {
		this(property, null, null);
	}

	public LongEditor(LongProperty property, Long min, Long max) {
		this(property.asObject(), min, max);
	}

	public LongEditor(Property<Long> property, Long min, Long max) {
		super(property, min, max);
	}

	@Override
	Converter<Number, Long> numberToValueConverter() {
		return Converters.NUMBER_TO_LONG;
	}

	@Override
	Converter<String, Long> stringToValueConverter() {
		return Converters.STRING_TO_LONG;
	}

	@Override
	UnaryOperator<TextFormatter.Change> textFormatFilter() {
		return TextFormatters.FILTER_LONG;
	}
}
