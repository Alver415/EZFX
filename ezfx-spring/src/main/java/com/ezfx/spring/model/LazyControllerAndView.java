package com.ezfx.spring.model;

import java.util.function.Supplier;

public class LazyControllerAndView<Controller, View> implements ControllerAndView<Controller, View> {

	private final Supplier<ControllerAndView<Controller, View>> supplier;
	private ControllerAndView<Controller, View> inner = null;

	public LazyControllerAndView(Supplier<ControllerAndView<Controller, View>> supplier) {
		this.supplier = supplier;
	}

	@Override
	public Controller controller() {
		return get().controller();
	}

	@Override
	public View view() {
		return get().view();
	}

	protected synchronized ControllerAndView<Controller, View> get() {
		if (inner == null) {
			inner = supplier.get();
		}
		return inner;
	}
}