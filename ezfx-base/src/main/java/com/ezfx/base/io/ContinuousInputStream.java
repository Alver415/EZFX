package com.ezfx.base.io;

import javafx.util.Duration;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import static com.ezfx.base.utils.EZFX.sleep;


/**
 * An InputStream that never ends. When reading and the internal inputStream hits the EOF signal (-1), then it just
 * continuously polls until more data is available instead of passing along the EOF signal.
 */
public class ContinuousInputStream extends FilterInputStream {

	private static final Duration DEFAULT_DELAY = Duration.millis(100);
	private final Duration delay = DEFAULT_DELAY;

	public ContinuousInputStream(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public int read() throws IOException {
		while (true) {
			ensureOpen();
			int read = in.read();
			if (read > 0) {
				return read;
			} else {
				sleep(delay);
			}
		}
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {
		Objects.checkFromIndexSize(offset, length, bytes.length);
		int readChar;
		int i = 0;
		for (; i < length; i++) {
			readChar = read();
			// When we hit the EOF signal, break and return whatever we've read so far.
			if (readChar == -1) {
				break;
			}
			bytes[offset + i] = (byte) readChar;
			// Return without blocking if we've reached the end of the stream.
			if (available() < 1) {
				i++;
				break;
			}
		}
		return i;
	}


	@Override
	public void close() throws IOException {
		InputStream input = in;
		in = null;
		if (input != null)
			input.close();
	}

	/**
	 * Throws IOException if the stream is closed.
	 */
	private void ensureOpen() throws IOException {
		if (in == null) {
			throw new IOException("Stream closed");
		}
	}

	@Override
	public long transferTo(OutputStream outputStream) throws IOException {
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int read;
		while (in != null) {
			ensureOpen();
			int available = available();
			if (available > 0) {
				read = read(buffer, 0, Math.min(available, bufferSize));
				if (read > 0) {
					outputStream.write(buffer, 0, read);
				}
				if (available() == 0) {
					outputStream.flush();
				}
			} else {
				sleep(delay);
			}
		}
		return -1;
	}
}
