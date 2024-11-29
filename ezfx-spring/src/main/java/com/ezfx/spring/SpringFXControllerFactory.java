package com.ezfx.spring;

import com.ezfx.spring.util.ControllerFactory;
import org.springframework.context.ApplicationContext;

public class SpringFXControllerFactory<T> implements ControllerFactory<T> {

	private final ApplicationContext context;
	private final T controller;
	private boolean override;

	public SpringFXControllerFactory(ApplicationContext context) {
		this(context, null);
	}

	public SpringFXControllerFactory(ApplicationContext context, T controller) {
		this.context = context;
		this.controller = controller;
		this.override = controller != null;
	}

	public T call(Class<T> param) {
		if (override) {
			override = false;
			return controller;
		}
		return context.getBean(param);
	}
}
