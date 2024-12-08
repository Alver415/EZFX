package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Category;

import java.lang.reflect.Method;

public record PropertyInfo(
		String name,
		String displayName,
		Category category,
		int order,
		Method property,
		Method setter,
		Method getter
) {
}
