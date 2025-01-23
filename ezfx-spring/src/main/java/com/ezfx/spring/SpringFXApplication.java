package com.ezfx.spring;

import com.ezfx.app.EZFXApplication;
import javafx.application.Application;
import org.springframework.context.ApplicationContext;

public abstract class SpringFXApplication extends EZFXApplication {

	private ApplicationContext applicationContext;

	@Override
	public void init() throws Exception {
		super.init();
		if (applicationContext == null) {
			this.applicationContext = initApplicationContext();
		}
	}

	public abstract ApplicationContext initApplicationContext();

	public final ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
