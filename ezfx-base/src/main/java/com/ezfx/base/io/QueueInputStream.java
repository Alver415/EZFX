package com.ezfx.base.io;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueInputStream extends InputStream {

	public static final int EOF = -1;

	protected final BlockingQueue<Integer> blockingQueue;

	public QueueInputStream() {
		this(new LinkedBlockingQueue<>());
	}

	public QueueInputStream(final BlockingQueue<Integer> blockingQueue) {
		this.blockingQueue = Objects.requireNonNull(blockingQueue, "blockingQueue");
	}

	public BlockingQueue<Integer> getBlockingQueue() {
		return blockingQueue;
	}

	@Override
	public int available() {
		return blockingQueue.size();
	}

	@Override
	public int read() {
		try {
			final Integer value = blockingQueue.poll(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			return value == null ? EOF : 0xFF & value;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			// throw runtime unchecked exception to maintain signature backward-compatibility of
			// this read method, which does not declare IOException
			throw new IllegalStateException(e);
		}
	}

	@Override
	public int read(byte[] bytes, int offset, int length) {
		Objects.checkFromIndexSize(offset, length, bytes.length);
		if (length == 0) {
			return 0;
		}

		int readChar = read();
		if (readChar == -1) {
			return -1;
		}
		bytes[offset] = (byte) readChar;

		int i = 1;
		for (; i < length; i++) {
			readChar = read();
			if (readChar == -1) {
				break;
			}
			bytes[offset + i] = (byte) readChar;
			// Return without blocking if we've reached the end of the queue.
			if (available() < 1) {
				i++;
				break;
			}
		}
		return i;
	}
}