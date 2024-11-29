package com.ezfx.spring;

import javafx.application.Application;
import org.springframework.context.ApplicationContext;

public abstract class SpringFXApplication extends Application {

	private ApplicationContext applicationContext;

	@Override
	public void init() {
		if (applicationContext == null) {
			this.applicationContext = initApplicationContext();
		}
	}

	public abstract ApplicationContext initApplicationContext();

	public final ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
