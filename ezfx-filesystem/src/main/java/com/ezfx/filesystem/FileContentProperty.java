package com.ezfx.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FileContentProperty
		extends WritableAttributeProperty<ByteBuffer, ByteBuffer> {

	private final FileSystemEntry entry;

	FileContentProperty(FileSystemEntry entry) {
		super(entry, "content");
		this.entry = entry;
	}

	@Override
	public FileSystemFX getFileSystem() {
		return entry.getFileSystem();
	}

	public ByteBuffer read() throws IOException {
		byte[] bytes = FileSystemIO.readAllBytes(entry.getPath());
		return bytes == null ? null : ByteBuffer.wrap(bytes);
	}

	@Override
	public void write(ByteBuffer bytes) throws IOException {
		FileSystemIO.writeAllBytes(entry.getPath(), bytes == null ? null : bytes.array());
	}

	public ByteBuffer pull() {
		return getValue();
	}

	@Override
	public void push(ByteBuffer bytes) {
		this.set(bytes);
	}

	@Override
	public String getAttributeViewName() {
		return "ezfx";
	}

}
