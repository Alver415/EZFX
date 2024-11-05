package com.fsfx.control.editor;

import java.lang.reflect.Method;

public record PropertyInfo<T>(
		String name,
		String displayName,
		String category,
		int order,
		Method property,
		Method setter,
		Method getter,
		Class<T> type) {

	@Override
	public String displayName(){
		return displayName != null ? displayName :
				name != null ? name :
				"???";
	}
}
