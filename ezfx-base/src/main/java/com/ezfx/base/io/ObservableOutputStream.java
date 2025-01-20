package com.ezfx.base.io;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Subscription;

import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;

import static com.ezfx.base.utils.EZFX.tryRun;

public class ObservableOutputStream extends OutputStream implements ObservableValue<String> {

	protected final StringBuffer buffer = new StringBuffer();
	protected final StringProperty lastFlush = new SimpleStringProperty(this, "lastFlush");

	@Override
	public synchronized void write(int integer) {
		char character = (char) integer;
		buffer.append(character);
	}

	@Override
	public synchronized void write(byte[] bytes) {
		write(bytes, 0, bytes.length);
	}

	@Override
	public synchronized void write(byte[] bytes, int offset, int length) {
		Objects.checkFromIndexSize(offset, length, bytes.length);
		for (int i = 0; i < length; i++) {
			write(bytes[offset + i]);
		}
	}

	public synchronized void flush() {
		String string = buffer.toString();
		buffer.setLength(0);
		lastFlush.set(string);
	}

	public Subscription subscribe(OutputStream outputStream) {
		return subscribe(flushed -> Optional.ofNullable(flushed)
				.filter(s -> !s.isEmpty())
				.map(String::getBytes)
				.ifPresent(bytes -> tryRun(() -> {
					outputStream.write(bytes);
					outputStream.flush();
				})));
	}

	@Override
	public void addListener(InvalidationListener listener) {
		lastFlush.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		lastFlush.addListener(listener);
	}

	@Override
	public void addListener(ChangeListener<? super String> listener) {
		lastFlush.addListener(listener);
	}

	@Override
	public void removeListener(ChangeListener<? super String> listener) {
		lastFlush.addListener(listener);

	}

	@Override
	public String getValue() {
		return lastFlush.getValue();
	}
}