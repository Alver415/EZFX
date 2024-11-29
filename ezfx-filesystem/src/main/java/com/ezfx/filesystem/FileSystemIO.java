package com.ezfx.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;
import java.util.stream.Stream;

public class FileSystemIO {

	public static byte[] readAllBytes(Path path) throws IOException {
		if (Files.exists(path) && Files.isRegularFile(path)) {
			return Files.readAllBytes(path);
		}
		return null;
	}

	public static void writeAllBytes(Path path, byte[] bytes) throws IOException {
		if (Files.exists(path) && Files.isRegularFile(path)) {
			Files.write(path, bytes);
		}
	}

	public static List<Path> listChildren(Path path) throws IOException {
		if (Files.exists(path) && Files.isDirectory(path)) {
			try (Stream<Path> childPaths = Files.list(path)) {
				return childPaths.toList();
			}
		}
		return List.of();
	}

	public static BasicFileAttributeView getBasicAttributeView(Path path) {
		return Files.getFileAttributeView(path, BasicFileAttributeView.class);
	}

	public static UserDefinedFileAttributeView getUserAttributeView(Path path) {
		return Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
	}
}
