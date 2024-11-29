package com.ezfx.filesystem;

import static com.ezfx.base.utils.Converters.STRING_TO_BYTE_ARRAY;

public class StringAttributeProperty extends UserAttributeProperty<String> {

	StringAttributeProperty(FileSystemEntry entry, String name) {
		this(entry, name, null);
	}

	StringAttributeProperty(FileSystemEntry entry, String name, String initialValue) {
		super(entry, name, STRING_TO_BYTE_ARRAY.inverted(), initialValue);
	}
}
