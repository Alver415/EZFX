package com.ezfx.filesystem;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemModel {

	private static final Logger log = LoggerFactory.getLogger(FileSystemModel.class);

	private final FileSystemFX fileSystem;
	private final ReadOnlyMapWrapper<Path, FileSystemEntry> cache;
	private final FileSystemEntry root;
	private final Path rootPath;

	public FileSystemModel(FileSystemFX fileSystem, Path rootPath) {
		this.fileSystem = fileSystem;
		this.cache = new ReadOnlyMapWrapper<>(this, "cache", FXCollections.observableHashMap());
		this.rootPath = rootPath;
		this.root = initializeRoot(rootPath);
	}

	private FileSystemEntry initializeRoot(Path rootPath) {
		FileSystemEntry root = initializeNode(null, rootPath);
		// Usually read() is restricted to the ioExecutor
//		Map<String, Object> read = root.read();
//		root.push(read);
		return root;
	}

	public FileSystemEntry getRoot() {
		return root;
	}

	public ReadOnlyMapProperty<Path, FileSystemEntry> fileSystemNodesProperty() {
		return this.cache.getReadOnlyProperty();
	}

	public ObservableMap<Path, FileSystemEntry> getFileSystemNodes() {
		return fileSystemNodesProperty().get();
	}

	synchronized void put(Path path, FileSystemEntry node) {
		cache.put(path.normalize(), node);
	}

	synchronized FileSystemEntry get(Path path) {
		if (!path.normalize().toAbsolutePath().startsWith(root.getPath().toAbsolutePath())) {
			throw new IllegalArgumentException("Root " + rootPath + " is not an ancestor of path: " + path);
		}
		if (!cache.containsKey(path)) {
			initializeNode(get(path.getParent()), path);
		}
		return cache.get(path);
	}

	private FileSystemEntry initializeNode(FileSystemEntry parent, Path path) {
		FileSystemEntry node = new FileSystemEntry(fileSystem, parent, path);
		if (Files.isDirectory(path)) {
			node.isDirectoryProperty().set(true);
		} else if (Files.isRegularFile(path)) {
			node.isRegularFileProperty().set(true);
		} else {
			node.existsProperty().set(false);
		}
		cache.put(path, node);
		fileSystem.getFileSystemWatcher().register(path);
		fileSystem.getFileSystemWatcher().register(path.getParent());
		log.debug("Initialized: {}", path);
		node.requestSync();
		return node;
	}


}
