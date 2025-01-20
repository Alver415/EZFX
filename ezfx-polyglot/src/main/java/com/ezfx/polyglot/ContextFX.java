package com.ezfx.polyglot;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class ContextFX {

	private final Context context;

	public ContextFX(Context context) {
		this.context = context;
	}

	public <T> T execute(String languageId, String script) {
		Value result = context.eval(languageId, script);
		return cast(result);
	}

	@SuppressWarnings("unchecked")
	private <T> T cast(Value result) {
		if (result.isHostObject()) {
			return result.asHostObject();
		} else if (result.isProxyObject()) {
			return result.asProxyObject();
		} else if (result.isBoolean()) {
			return (T) (Boolean) result.asBoolean();
		} else if (result.isNumber()) {
			return (T) result.as(Number.class);
		} else if (result.isString()) {
			return (T) result.asString();
		}
		throw new UnsupportedOperationException("Failed to cast result: %s".formatted(result));
	}
}
