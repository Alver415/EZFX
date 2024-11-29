package com.ezfx.filesystem;

import com.ezfx.base.utils.Converter;

import static com.ezfx.base.utils.Converters.STRING_TO_BOOLEAN;
import static com.ezfx.base.utils.Converters.STRING_TO_BYTE_ARRAY;

public class BooleanAttributeProperty extends UserAttributeProperty<Boolean> {
	private static final Converter<byte[], Boolean> CONVERTER =
			STRING_TO_BYTE_ARRAY.inverted().compound(STRING_TO_BOOLEAN);

	BooleanAttributeProperty(FileSystemEntry entry, String name) {
		this(entry, name, false);
	}

	BooleanAttributeProperty(FileSystemEntry entry, String name, boolean initialValue) {
		super(entry, name, CONVERTER, initialValue);
	}

	@Override
	public void push(Boolean value) {
		super.push(value == null || value);
	}
}
