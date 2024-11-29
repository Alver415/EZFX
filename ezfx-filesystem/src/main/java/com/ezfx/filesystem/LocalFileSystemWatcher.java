package com.ezfx.filesystem;

import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

public class LocalFileSystemWatcher implements FileSystemWatcher {

	private static final Logger log = LoggerFactory.getLogger(LocalFileSystemWatcher.class);

	private final WatchService watchService;
	private final EventSource<FileSystemEvent> eventStream;
	private final Map<Path, WatchKey> watchKeyMap = new ConcurrentHashMap<>();
	private final ScheduledExecutorService executorService;

	public LocalFileSystemWatcher() throws IOException {
		this.watchService = FileSystems.getDefault().newWatchService();
		this.eventStream = new EventSource<>();
		this.executorService = Executors.newSingleThreadScheduledExecutor(
				runnable -> new Thread(runnable, "LocalFileSystemWatcher"));
		this.executorService.scheduleAtFixedRate(this::poll, 0, 1, TimeUnit.NANOSECONDS);
	}

	@Override
	public EventStream<FileSystemEvent> getFileSystemEvents() {
		return eventStream;
	}

	@Override
	public void close() throws IOException {
		watchService.close();
		executorService.close();
	}

	private void poll() {
		try {
			WatchKey key;
			while ((key = watchService.take()) != null) {
				handleKey(key);
			}
		} catch (ClosedWatchServiceException e) {
			log.debug("Closed WatchService.");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void handleKey(WatchKey key) {
		try {
			key.pollEvents().forEach(
					event -> {
						FileSystemEventRecord record = new FileSystemEventRecord(key, (WatchEvent<Path>) event);
						log.debug("new FileSystemEventRecord({}, {})", record.getPath(), record.getKind());
						eventStream.push(record);
					});
		} finally {
			key.reset();
		}
	}


	@Override
	public void register(Path path) {
		if (!Files.isDirectory(path)) {
			return;
		}
		try {
			watchKeyMap.put(path, path.register(
					watchService,
					ENTRY_CREATE,
					ENTRY_MODIFY,
					ENTRY_DELETE));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void unregister(Path path) {
		Optional.ofNullable(watchKeyMap.remove(path)).ifPresent(WatchKey::cancel);
	}

	public record FileSystemEventRecord(WatchKey watchKey, WatchEvent<Path> watchEvent) implements FileSystemEvent {
		@Override
		public Path getPath() {
			return ((Path) watchKey().watchable())
					.resolve(watchEvent().context())
					.normalize();
		}

		@Override
		public WatchEvent.Kind<Path> getKind() {
			return watchEvent().kind();
		}
	}

}
