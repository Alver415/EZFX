package com.ezfx.base.io;

import java.io.*;
import java.nio.file.Path;

import static com.ezfx.base.io.FileSystemIO.*;

public final class IOConsole implements Closeable {

	public static final Path DEFAULT_DIRECTORY = Path.of(System.getProperty("io.console.dir.base", System.getProperty("java.io.tmpdir")));
	public static final String DEFAULT_PREFIX = System.getProperty("io.console.dir.prefix", "io-console");

	public final IOStream in;
	public final IOStream out;
	public final IOStream err;

	public IOConsole() {
		this(null, null, null);
	}

	public IOConsole(InputStream _in, OutputStream _out, OutputStream _err) {
		this(DEFAULT_PREFIX, _in, _out, _err);
	}

	public IOConsole(String directoryPrefix, InputStream _in, OutputStream _out, OutputStream _err) {
		this(createTempDirectory(DEFAULT_DIRECTORY, directoryPrefix), _in, _out, _err);
	}

	public IOConsole(Path directory, InputStream _in, OutputStream _out, OutputStream _err) {
		this(createTempFile(directory, "in"), _in,
				createTempFile(directory, "out"), _out,
				createTempFile(directory, "err"), _err);
	}

	public IOConsole(File fileIn, InputStream _in, File fileOut, OutputStream _out, File fileErr, OutputStream _err) {
		this.in = new IOStream(fileIn);
		this.out = new IOStream(fileOut);
		this.err = new IOStream(fileErr);
		if (_in != null)
			transfer(_in, in.getOutputStream());
		if (_out != null)
			out.subscribe(_out);
		if (_err != null)
			err.subscribe(_err);
	}

	public void close() throws IOException {
		this.in.close();
		this.out.close();
		this.err.close();
	}

}
