package com.ezfx.filesystem;

import com.ezfx.base.utils.Converter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public abstract class UserAttributeProperty<T>
		extends WritableAttributeProperty<T, T> {

	protected final Converter<byte[], T> converter;

	UserAttributeProperty(FileSystemEntry entry, String name, Converter<byte[], T> converter) {
		this(entry, name, converter, null);
	}

	UserAttributeProperty(FileSystemEntry entry, String name, Converter<byte[], T> converter, T initialValue) {
		super(entry, name, initialValue);
		this.converter = converter;
	}

	@Override
	public T read() throws IOException {
		Path path = getEntry().getPath();
		UserDefinedFileAttributeView userAttributes = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
		return read(userAttributes);
	}

	T read(UserDefinedFileAttributeView userAttributes) throws IOException {
		try {
			String key = getName();
			int size = userAttributes.size(key);
			ByteBuffer buffer = ByteBuffer.allocate(size);
			userAttributes.read(key, buffer);
			return converter.to(buffer.array());
		} catch (NoSuchFileException e) {
			return null;
		}
	}

	@Override
	public void write(T value) throws IOException {
		Path path = getEntry().getPath();
		UserDefinedFileAttributeView userAttributes = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
		write(userAttributes, value);
	}

	void write(UserDefinedFileAttributeView userAttributes, T value) throws IOException {
		userAttributes.write(getName(), ByteBuffer.wrap(converter.from(value)));
	}

	@Override
	public T pull() {
		return converter.to(getBytes());
	}

	@Override
	public void push(T value) {
		setBytes(converter.from(value));
	}

	public byte[] getBytes() {
		return converter.from(getValue());
	}

	public void setBytes(byte[] value) {
		this.set(converter.to(value));
	}

	@Override
	public String getAttributeViewName() {
		return "user";
	}

}
