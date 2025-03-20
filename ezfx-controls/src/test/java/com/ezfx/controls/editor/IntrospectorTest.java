package com.ezfx.controls.editor;

import com.ezfx.base.introspector.Introspector;
import com.ezfx.base.introspector.EZFXIntrospector;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class IntrospectorTest {

	private static final Logger log = LoggerFactory.getLogger(IntrospectorTest.class);

	@Test
	public void test() throws Exception {
		Introspector introspector = EZFXIntrospector.DEFAULT_INTROSPECTOR;

		Method[] methods = IntrospectorTest.class.getMethods();

		for (Method method : methods) {
			Parameter[] parameters = method.getParameters();
			for (Parameter parameter : parameters) {
				String name = introspector.getParameterTypeName(parameter);
				log.info("{}#{}: {}",method.getDeclaringClass(), method.getName(), name);
			}
		}
	}

	public void example(String string, List<String> list, Map<String, List<String>> map) {

	}
}
