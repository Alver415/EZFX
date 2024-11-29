package com.ezfx.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;

public class BasicAttributeProperty<T>
		extends ReadableAttributeProperty<T, T> {

	private final Function<BasicFileAttributes, T> accessor;

	BasicAttributeProperty(
			FileSystemEntry entry,
			String name,
			Function<BasicFileAttributes, T> accessor) {
		this(entry, name, accessor, null);
	}

	BasicAttributeProperty(
			FileSystemEntry entry,
			String name,
			Function<BasicFileAttributes, T> accessor,
			T initialValue) {
		super(entry, name, initialValue);
		this.accessor = accessor;
	}

	@Override
	public String getAttributeViewName() {
		return "basic";
	}

	@Override
	public T read() throws IOException {
		Path path = getEntry().getPath();
		BasicFileAttributeView view = Files.getFileAttributeView(path, BasicFileAttributeView.class);
		BasicFileAttributes basicAttributes = view.readAttributes();
		return accessor.apply(basicAttributes);
	}

	public T read(BasicFileAttributes basicAttributes) {
		return accessor.apply(basicAttributes);
	}

	@Override
	public void write(T value) throws IOException {
		// Do nothing
	}

	@Override
	public T pull() {
		return getValue();
	}

	@Override
	public void push(T value) {
		set(value);
	}

}
