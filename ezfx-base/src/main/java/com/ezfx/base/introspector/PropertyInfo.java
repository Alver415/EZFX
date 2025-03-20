package com.ezfx.base.introspector;

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
