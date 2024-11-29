package com.ezfx.base.io;

import javafx.util.Subscription;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class IOStream implements Closeable {

	private final File file;
	private final List<InputStream> inputStreams;
	private final ObservableOutputStream outputStream;
	private final PrintStream printStream;
	private final FileOutputStream fileOutputStream;

	public IOStream(File file) {
		try {
			this.file = file;
			this.fileOutputStream = new FileOutputStream(file);

			this.inputStreams = new ArrayList<>();
			this.outputStream = new ObservableOutputStream();
			this.printStream = new PrintStream(this.outputStream, true);

			this.outputStream.subscribe(fileOutputStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getInputStream() {
		try {
			return split();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream split() throws IOException {
		ContinuousInputStream inputStream = new ContinuousInputStream(new FileInputStream(file));
		inputStreams.add(inputStream);
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public Subscription subscribe(OutputStream outputStream) {
		return this.outputStream.subscribe(outputStream);
	}

	public PrintStream getPrintStream() {
		return printStream;
	}

	public void println() {
		printStream.println();
	}

	public void println(String string) {
		printStream.println(string);
	}

	public void print(String string) {
		printStream.print(string);
	}

	public void close() throws IOException {
		this.printStream.close();
		this.outputStream.close();
		this.fileOutputStream.close();
		for (InputStream inputStream : this.inputStreams) {
			inputStream.close();
		}
	}

}
