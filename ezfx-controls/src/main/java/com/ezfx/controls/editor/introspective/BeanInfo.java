package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Category;

import java.util.Map;
import java.util.function.Function;

public record BeanInfo<T>(
		Class<T> type,
		String name,
		String displayName,
		Category category,
		int order,
		Map<String, PropertyInfo> properties,
		Map<String, Function<Object[], T>> builders
) {
}
