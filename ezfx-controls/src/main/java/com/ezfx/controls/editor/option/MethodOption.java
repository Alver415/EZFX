package com.ezfx.controls.editor.option;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.introspective.FunctionParameterEditor;

import java.lang.reflect.Method;
import java.util.function.Function;

public class MethodOption<T> extends BuilderOption<T> {

	private final Method method;

	public MethodOption(String name, Class<T> type, Method method) {
		super(name, type);
		this.method = method;
	}

	@Override
	public Editor<T> buildEditor() {
		Function<Object[], T> buildFunction = args -> {
			try {
				return (T) method.invoke(null, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
		return new FunctionParameterEditor<>(type, method.getParameters(), buildFunction);
	}
}