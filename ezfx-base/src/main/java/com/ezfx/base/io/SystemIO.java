package com.ezfx.base.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static com.ezfx.base.io.FileSystemIO.getOrCreateDirectory;
import static com.ezfx.base.io.FileSystemIO.getOrCreateFile;

public final class SystemIO {

	// References to the original System in/out/err streams.
	public static final InputStream _in = System.in;
	public static final PrintStream _out = System.out;
	public static final PrintStream _err = System.err;

	public static final IOConsole console;
	public static final IOStream in;
	public static final IOStream out;
	public static final IOStream err;

	private static final Path dirPath;
	private static final File inFile;
	private static final File outFile;
	private static final File errFile;

	static {
		try {
			dirPath = getOrCreateDirectory(IOConsole.DEFAULT_DIRECTORY);
			inFile = getOrCreateFile(dirPath.resolve("in"));
			outFile = getOrCreateFile(dirPath.resolve("out"));
			errFile = getOrCreateFile(dirPath.resolve("err"));
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
		console = new IOConsole(inFile, _in, outFile, _out, errFile, _err);
		in = console.in;
		out = console.out;
		err = console.err;
	}

	// This is only here to prevent a potential memory leak.
	// Would otherwise need to create a new InputStream for each init() call.
	private static final InputStream inSplit = in.getInputStream();

	public static void overrideSystemDefaults() {
		System.setIn(inSplit);
		System.setOut(out.getPrintStream());
		System.setErr(err.getPrintStream());
	}

	public static void resetSystemDefaults() {
		System.setIn(_in);
		System.setOut(_out);
		System.setErr(_err);
	}
}
