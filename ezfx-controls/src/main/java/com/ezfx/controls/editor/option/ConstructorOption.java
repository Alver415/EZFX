package com.ezfx.controls.editor.option;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.introspective.FunctionParameterEditor;

import java.lang.reflect.Constructor;
import java.util.function.Function;

public class ConstructorOption<T> extends BuilderOption<T> {

	private final Constructor<T> constructor;

	public ConstructorOption(String name, Class<T> type, Constructor<T> constructor) {
		super(name, type);
		this.constructor = constructor;
	}

	public Constructor<T> getConstructor() {
		return constructor;
	}
	@Override
	public Editor<T> buildEditor() {
		Function<Object[], T> buildFunction = args -> {
			try {
				return constructor.newInstance(args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
		return new FunctionParameterEditor<>(type, constructor.getParameters(), buildFunction);
	}

}
