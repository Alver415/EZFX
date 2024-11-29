package com.ezfx.spring;

import javafx.util.Builder;
import org.springframework.stereotype.Component;

@Component
public abstract class SpringFXBuilder<T> implements Builder<T> {

	protected final Class<T> type;

	public SpringFXBuilder(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public abstract T build();
}
