package com.ezfx.spring;

import com.ezfx.spring.annotations.FXMLAutoLoad;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringFXBuilderFactory implements BuilderFactory {

	private static final JavaFXBuilderFactory DEFAULT_BUILDER_FACTORY = new JavaFXBuilderFactory();

	private final ApplicationContext context;
	private final Map<Class<?>, SpringFXBuilder<?>> builderMap;

	public SpringFXBuilderFactory(
			ApplicationContext context,
			Map<Class<?>, SpringFXBuilder<?>> builderMap) {
		this.context = context;
		this.builderMap = builderMap;
	}

	@Override
	public Builder<?> getBuilder(Class<?> type) {
		if (builderMap.containsKey(type)) {
			return builderMap.get(type);
		}
		if (AnnotationUtils.findAnnotation(type, FXMLAutoLoad.class) != null) {
			return new SpringFXProxyBuilder<>(context, type);
		}
		return DEFAULT_BUILDER_FACTORY.getBuilder(type);
	}
}