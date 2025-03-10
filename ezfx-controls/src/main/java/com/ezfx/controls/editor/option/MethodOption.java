package com.ezfx.controls.editor.option;

import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.introspective.FunctionParameterEditor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Function;

public class MethodOption<T> extends BuilderOption<T> {

	private final Method method;

	public MethodOption(String name, Type type, Method method) {
		super(name, type);
		this.method = method;
	}

	@Override
	public EditorBase<T> buildEditor() {
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