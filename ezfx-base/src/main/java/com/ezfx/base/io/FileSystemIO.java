package com.ezfx.base.io;


import com.ezfx.base.exception.UncheckedRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.ezfx.base.utils.EZFX.runOnNewThread;

public interface FileSystemIO {

	static void transfer(InputStream inputStream, OutputStream outputStream) {
		runOnNewThread(() -> inputStream.transferTo(outputStream));
	}

	static Path getOrCreateDirectory(Path path) throws IOException {
		if (Files.exists(path)) {
			if (Files.isDirectory(path)) {
				return path;
			} else if (Files.isRegularFile(path)) {
				throw new IOException("File already exists at path: " + path);
			} else {
				throw new IOException("Something already exists at path: " + path);
			}
		} else {
			return Files.createDirectories(path);
		}
	}

	static File getOrCreateFile(Path path) throws IOException {
		if (Files.exists(path)) {
			if (Files.isRegularFile(path)) {
				Files.writeString(path, "");
				return path.toFile();
			} else if (Files.isDirectory(path)) {
				throw new IOException("Directory already exists at path: " + path);
			} else {
				throw new IOException("Something already exists at path: " + path);
			}
		} else {
			Files.createDirectories(path.getParent());
			return Files.createFile(path).toFile();
		}
	}

	static Path createTempDirectory(Path dir, String prefix) {
		try {
			Path directory = Files.createTempDirectory(dir, prefix);
			Runtime.getRuntime().addShutdownHook(
					new Thread((UncheckedRunnable) () -> deletePath(directory)));
			return directory;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static File createTempFile(Path dir, String name) {
		try {
			Files.createDirectories(dir);
			Path path = Files.createTempFile(dir, name, ".log");
			Runtime.getRuntime().addShutdownHook(
					new Thread((UncheckedRunnable) () -> deletePath(path)));
			return path.toFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static void deletePath(Path path) {
		try (Stream<Path> pathStream = Files.walk(path)) {
			pathStream.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(file -> {
						try {
							Files.deleteIfExists(file.toPath());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
