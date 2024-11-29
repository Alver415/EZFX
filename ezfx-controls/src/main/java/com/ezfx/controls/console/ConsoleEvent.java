package com.ezfx.controls.console;

import javafx.event.Event;
import javafx.event.EventType;

public class ConsoleEvent extends Event {

	public static final EventType<ConsoleEvent> INPUT_SUBMITTED = new EventType<>(Event.ANY, "INPUT_SUBMITTED");
	public static final EventType<ConsoleEvent> OUTPUT_PRINTED = new EventType<>(Event.ANY, "OUTPUT_PRINTED");
	public static final EventType<ConsoleEvent> IN_PRINTED = new EventType<>(OUTPUT_PRINTED, "IN_PRINTED");
	public static final EventType<ConsoleEvent> OUT_PRINTED = new EventType<>(OUTPUT_PRINTED, "OUT_PRINTED");
	public static final EventType<ConsoleEvent> ERR_PRINTED = new EventType<>(OUTPUT_PRINTED, "ERR_PRINTED");

	private final String text;

	public ConsoleEvent(EventType<? extends Event> eventType, String text) {
		super(eventType);
		this.text = text;
	}

	public String getText() {
		return text;
	}
}