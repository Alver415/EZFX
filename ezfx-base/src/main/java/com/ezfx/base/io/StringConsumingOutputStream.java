package com.ezfx.base.io;

import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Consumer;

public class StringConsumingOutputStream extends OutputStream {

	protected final StringBuffer buffer;
	protected final Consumer<String> consumer;

	public StringConsumingOutputStream(Consumer<String> consumer) {
		this.buffer = new StringBuffer();
		this.consumer = consumer;
	}

	@Override
	public synchronized void write(int value) {
		buffer.append((char) value);
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

	@Override
	public synchronized void flush() {
		String string = buffer.toString();
		consumer.accept(string);
		buffer.setLength(0);
	}
}
