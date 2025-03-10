package com.ezfx.controls.editor.option;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorBase;
import com.ezfx.controls.editor.introspective.FunctionParameterEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.function.Function;

public class ConstructorOption<T> extends BuilderOption<T> {

	private static final Logger log = LoggerFactory.getLogger(ConstructorOption.class);
	private final Constructor<T> constructor;

	public ConstructorOption(String name, Type type, Constructor<T> constructor) {
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
				log.debug(e.getMessage(), e);
				return (T) new EditorBase<>();
//				throw new RuntimeException(e);
			}
		};
		return new FunctionParameterEditor<>(type, constructor.getParameters(), buildFunction);
	}

}
