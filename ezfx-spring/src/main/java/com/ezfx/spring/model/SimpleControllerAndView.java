package com.ezfx.spring.model;

public class SimpleControllerAndView<Controller, View> implements ControllerAndView<Controller, View> {
	private final Controller controller;
	private final View view;

	public SimpleControllerAndView(Controller controller, View view) {
		this.controller = controller;
		this.view = view;
	}

	@Override
	public Controller controller() {
		return controller;
	}

	@Override
	public View view() {
		return view;
	}
}
