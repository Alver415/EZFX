package com.ezfx.spring.autoconfigure;

import com.ezfx.spring.*;
import com.ezfx.spring.annotations.Prototype;
import com.ezfx.spring.model.ControllerAndView;
import javafx.scene.Node;
import javafx.util.BuilderFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AutoConfiguration
public class SpringFXAutoConfiguration {

	private final ApplicationContext context;
	private final List<SpringFXBuilder<?>> builders;

	@Autowired
	public SpringFXAutoConfiguration(
			ApplicationContext context,
			List<SpringFXBuilder<?>> builders) {
		this.context = context;
		this.builders = builders;
	}

	@Bean
	public Map<Class<?>, SpringFXBuilder<?>> getSpringFXBuilders() {
		return builders.stream().collect(Collectors.toMap(
				SpringFXBuilder::getType,
				Function.identity()));
	}

	@Bean
	public BuilderFactory getBuilderFactory(Map<Class<?>, SpringFXBuilder<?>> springFXBuilders) {
		return new SpringFXBuilderFactory(context, springFXBuilders);
	}

	@Bean
	public SpringFX getSpringFX() {
		return new SpringFX(context);
	}

	@Bean
	@Prototype
	public SpringFXLoader getSpringFXLoader(
			ApplicationContext applicationContext,
			BuilderFactory builderFactory) {
		SpringFXLoader loader = new SpringFXLoader();
		loader.setApplicationContext(context);
		loader.setControllerFactory(context::getBean);
		loader.setBuilderFactory(builderFactory);

		return loader;
	}

	@Bean
	public SpringFXInjectionPointResolver getControllerAndViewResolver(SpringFX springFX) {
		return new SpringFXInjectionPointResolver(springFX);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public <C, V extends Node> ControllerAndView<C, V> getControllerAndView(
			SpringFXInjectionPointResolver controllerAndViewResolver,
			InjectionPoint injectionPoint) {
		return controllerAndViewResolver.resolve(injectionPoint);
	}
}
