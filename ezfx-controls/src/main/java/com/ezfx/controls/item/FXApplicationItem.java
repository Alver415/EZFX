package com.ezfx.controls.item;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.stage.Window;

public class FXApplicationItem<T extends Application> extends FXItemBase<T, FXWindowItem<?>> {

	protected FXApplicationItem(FXItemFactory factory, T application) {
		super(factory, application);
		for (Window window : Window.getWindows()) {
			children.add(factory.create(window));
		}
		Window.getWindows().addListener((ListChangeListener<? super Window>) change -> {
			while (change.next()) {
				for (Window removed : change.getRemoved()) {
//					children.removeIf(item -> item.get() == removed);
				}
				for (Window added : change.getAddedSubList()) {
					children.add(factory.create(added));
				}
			}
		});
	}
}