package com.ezfx.filesystem;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java's WatchService doesn't work for remote FileSystem, so need to use Apache Commons solution
 * which polls and checks for changes. Less efficient and performant, but necessary for remote FileSystems.
 */
public class RemoteFileSystemWatcher implements FileSystemWatcher {

	private static final Logger log = LoggerFactory.getLogger(RemoteFileSystemWatcher.class);

	private final EventSource<FileSystemEvent> eventStream = new EventSource<>();
	private final FileAlterationMonitor monitor;
	private final Map<Path, FileAlterationObserver> observers = new ConcurrentHashMap<>();

	public RemoteFileSystemWatcher() throws Exception {
		//create a monitor to check changes after every 500 ms
		monitor = new FileAlterationMonitor(1000);
		monitor.start();
	}

	@Override
	public EventStream<FileSystemEvent> getFileSystemEvents() {
		return eventStream;
	}

	public void register(Path path) {
		if (observers.containsKey(path)) {
			return;
		}
		FileAlterationObserver observer = createObserver(path);
		observers.put(path, observer);
		monitor.addObserver(observer);
	}

	public void unregister(Path path) {
		if (observers.containsKey(path)) {
			FileAlterationObserver observer = observers.remove(path);
			monitor.removeObserver(observer);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			monitor.stop();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private FileAlterationObserver createObserver(Path path) {
		FileAlterationObserver observer = new FileAlterationObserver(path.toFile());
		observer.addListener(new FileAlterationListenerAdaptor() {
			@Override
			public void onDirectoryCreate(File file) {
				eventStream.push(new FileSystemEventRecord(file.toPath().toAbsolutePath(), StandardWatchEventKinds.ENTRY_CREATE));
			}

			@Override
			public void onDirectoryChange(File directory) {
				eventStream.push(new FileSystemEventRecord(directory.toPath().toAbsolutePath(), StandardWatchEventKinds.ENTRY_MODIFY));
			}

			@Override
			public void onDirectoryDelete(File file) {
				eventStream.push(new FileSystemEventRecord(file.toPath().toAbsolutePath(), StandardWatchEventKinds.ENTRY_DELETE));
			}

			@Override
			public void onFileCreate(File file) {
				eventStream.push(new FileSystemEventRecord(file.toPath().toAbsolutePath(), StandardWatchEventKinds.ENTRY_CREATE));
			}

			@Override
			public void onFileChange(File file) {
				eventStream.push(new FileSystemEventRecord(file.toPath().toAbsolutePath(), StandardWatchEventKinds.ENTRY_MODIFY));
			}

			@Override
			public void onFileDelete(File file) {
				eventStream.push(new FileSystemEventRecord(file.toPath().toAbsolutePath(), StandardWatchEventKinds.ENTRY_DELETE));
			}
		});
		return observer;
	}


	public record FileSystemEventRecord(Path path,
	                                    WatchEvent.Kind<Path> kind) implements FileSystemWatcher.FileSystemEvent {

		@Override
		public Path getPath() {
			return path;
		}

		@Override
		public WatchEvent.Kind<Path> getKind() {
			return kind;
		}
	}
}
