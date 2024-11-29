package com.ezfx.filesystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ezfx.filesystem.EntryType.DIRECTORY;
import static com.ezfx.filesystem.EntryType.FILE;

public class FolderChildrenProperty
		extends MapAttributeProperty<String, FileSystemEntry, EntryType> {

	FolderChildrenProperty(FileSystemEntry entry) {
		super(entry, "children", FXCollections.observableHashMap());
	}

	@Override
	public Map<String, EntryType> read() throws IOException {
		Map<String, EntryType> map = new HashMap<>();
		for (Path path : FileSystemIO.listChildren(getEntry().getPath())) {
			EntryType type = Files.isDirectory(path) ? DIRECTORY : FILE;
			map.put(path.getFileName().toString(), type);
		}
		return Map.copyOf(map);
	}

	@Override
	public void write(Map<String, EntryType> map) throws IOException {
		Path path = getEntry().getPath();
		Set<Path> toDelete = new HashSet<>();
		for (Path childPath : FileSystemIO.listChildren(path)) {
			String name = childPath.getFileName().toString();
			if (!map.containsKey(name)) {
				toDelete.add(childPath);
				continue;
			}
			EntryType type = map.get(name);
			if (Files.isRegularFile(childPath) && type != FILE) {
				toDelete.add(childPath);
			}
			if (Files.isDirectory(childPath) && type != DIRECTORY) {
				toDelete.add(childPath);
			}
		}

		for (Path childPath : toDelete) {
			Files.delete(childPath);
		}

		for (Map.Entry<String, EntryType> e : map.entrySet()) {
			String childName = e.getKey();
			EntryType type = e.getValue();
			Path childPath = path.resolve(childName);
			if (!Files.exists(childPath)) {
				switch (type) {
					case FILE -> Files.createFile(childPath);
					case DIRECTORY -> Files.createDirectory(childPath);
					default -> throw new RuntimeException();
				}
			}
		}
	}

	@Override
	public Map<String, EntryType> pull() {
		return getValue().entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getKey,
				v -> v.getValue().isDirectory() ? DIRECTORY : FILE));
	}

	@Override
	public void push(Map<String, EntryType> map) {
		ObservableMap<String, FileSystemEntry> children = value;

		Set<String> toRemove = new HashSet<>();
		for (String key : children.keySet()) {
			if (!map.containsKey(key)) {
				toRemove.add(key);
			}
		}
		toRemove.forEach(children::remove);

		for (String key : map.keySet()) {
			FileSystemEntry child = getFileSystem().get(getEntry().getPath().resolve(key));
			children.put(key, child);
		}
	}

	@Override
	public String getAttributeViewName() {
		return "ezfx";
	}
}
