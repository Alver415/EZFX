package com.ezfx.filesystem;

import javafx.beans.value.ObservableValue;
import org.reactfx.EventSource;

public class FlushListener<T> {
	private final EventSource<Runnable> events = new EventSource<>();

	public void flushEventHandler(ObservableValue<? extends T> observableValue, T oldValue, T newValue) {
//		events.push();
	}
}
