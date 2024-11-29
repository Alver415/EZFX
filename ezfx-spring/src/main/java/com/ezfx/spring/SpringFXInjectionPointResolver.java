package com.ezfx.spring;

import com.ezfx.spring.model.ControllerAndView;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@SuppressWarnings("ALL")
@Component
public class SpringFXInjectionPointResolver {

	private static final Logger log = LoggerFactory.getLogger(SpringFXInjectionPointResolver.class);
	private final SpringFX springFX;

	public SpringFXInjectionPointResolver(SpringFX fxWeaver) {
		this.springFX = fxWeaver;
	}

	/**
	 * Resolve generic type classes of a {@link ControllerAndView} {@link InjectionPoint} and return a
	 * {@link ControllerAndView} embedding the {@link SpringFX#load(Class)} method for instance creation.
	 *
	 * @param injectionPoint the actual injection point for the {@link FxControllerAndView} to inject
	 * @throws IllegalArgumentException when types could not be resolved from the given injection point
	 */
	public <C, V extends Node> ControllerAndView<C, V> resolve(InjectionPoint injectionPoint) {
		ResolvableType resolvableType = findResolvableType(injectionPoint);
		if (resolvableType == null) {
			throw new IllegalArgumentException("No ResolvableType found");
		}
		try {
			Class<C> controllerClass = (Class<C>) resolvableType.getGenerics()[0].resolve();
			return springFX.load(controllerClass);
		} catch (Exception e) {
			throw new IllegalArgumentException("Generic controller type not resolvable for injection point " + injectionPoint, e);
		}
	}

	private ResolvableType findResolvableType(InjectionPoint injectionPoint) {
		return Optional.ofNullable(injectionPoint.getMethodParameter())
				.map(ResolvableType::forMethodParameter)
				.orElse(Optional.ofNullable(injectionPoint.getField())
						.map(ResolvableType::forField)
						.orElse(null));
	}

}