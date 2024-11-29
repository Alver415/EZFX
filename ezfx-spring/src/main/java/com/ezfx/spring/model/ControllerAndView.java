package com.ezfx.spring.model;

import java.util.function.Supplier;

public interface ControllerAndView<Controller, View> {

	Controller controller();

	View view();

	static <C, V> SimpleControllerAndView<C, V> simple(C controller, V view) {
		return new SimpleControllerAndView<>(controller, view);
	}

	static <C, V> LazyControllerAndView<C, V> lazy(Supplier<ControllerAndView<C, V>> supplier) {
		return new LazyControllerAndView<>(supplier);
	}
}