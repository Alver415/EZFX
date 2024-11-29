package com.ezfx.filesystem;

import org.reactfx.EventStream;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public interface FileSystemWatcher extends AutoCloseable {

	EventStream<FileSystemEvent> getFileSystemEvents();

	void register(Path path);

	void unregister(Path path);

	void close() throws IOException;

	interface FileSystemEvent {

		Path getPath();

		WatchEvent.Kind<Path> getKind();

		default boolean isCreate() {
			return getKind().equals(StandardWatchEventKinds.ENTRY_CREATE);
		}

		default boolean isModify() {
			return getKind().equals(StandardWatchEventKinds.ENTRY_MODIFY);
		}

		default boolean isDelete() {
			return getKind().equals(StandardWatchEventKinds.ENTRY_DELETE);
		}


	}
}
