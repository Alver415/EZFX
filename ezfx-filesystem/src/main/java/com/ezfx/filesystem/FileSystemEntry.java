package com.ezfx.filesystem;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.TreeMap;
import java.util.stream.Stream;

public class FileSystemEntry {

	private static final Logger log = LoggerFactory.getLogger(FileSystemEntry.class);

	private final FileSystemFX fileSystem;

	FileSystemEntry(FileSystemFX fileSystem, FileSystemEntry parent, Path path) {
		this.fileSystem = fileSystem;
		this.parent = parent;
		this.path = path.normalize();
		this.name.setValue(getPath().normalize().getFileName().toString());

		this.content = new FileContentProperty(this);
		this.children = new FolderChildrenProperty(this);

		this.exists = new BooleanAttributeProperty(this, "exists", true);
		this.autoSync = new BooleanAttributeProperty(this, "autoSync", true);
		this.autoFlush = new BooleanAttributeProperty(this, "autoFlush", true);

		this.creationTime = new BasicAttributeProperty<>(
				this, "creationTime", BasicFileAttributes::creationTime);
		this.lastAccessTime = new BasicAttributeProperty<>(
				this, "lastAccessTime", BasicFileAttributes::lastAccessTime);
		this.lastModifiedTime = new BasicAttributeProperty<>(
				this, "lastModifiedTime", BasicFileAttributes::lastModifiedTime);
		this.isDirectory = new BasicAttributeProperty<>(
				this, "isDirectory", BasicFileAttributes::isDirectory, false);
		this.isOther = new BasicAttributeProperty<>(
				this, "isOther", BasicFileAttributes::isOther, false);
		this.isRegularFile = new BasicAttributeProperty<>(
				this, "isRegularFile", BasicFileAttributes::isRegularFile, false);
		this.isSymbolicLink = new BasicAttributeProperty<>(
				this, "isSymbolicLink", BasicFileAttributes::isSymbolicLink, false);
		this.fileSize = new BasicAttributeProperty<>(
				this, "fileSize", BasicFileAttributes::size);


		Stream<ReadableAttributeProperty<?, ?>> attributes = Stream.of(
				content, children, exists, autoSync, autoFlush,
				creationTime, lastModifiedTime, lastAccessTime,
				isDirectory, isRegularFile, isSymbolicLink, isOther,
				fileSize);
		attributes.forEach(property -> {
			this.attributes.put(property.getAttributeKey(), property);
		});
	}

	public FileSystemFX getFileSystem() {
		return fileSystem;
	}

	public void requestSync() {
		attributes.values().forEach(ReadableAttributeProperty::requestSync);
	}

	public void requestFlush() {
		attributes.values().forEach(ReadableAttributeProperty::requestFlush);
	}

	//region name
	private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(this, "name");

	public ReadOnlyStringProperty nameProperty() {
		return this.name.getReadOnlyProperty();
	}

	public String getName() {
		return this.nameProperty().get();
	}

	//endregion name
	//region path
	final Path path;

	public Path getPath() {
		return path;
	}

	//endregion path
	//region parent
	final FileSystemEntry parent;

	public FileSystemEntry getParent() {
		return parent;
	}

	//endregion parent

	//region attributes
	final ReadOnlyMapWrapper<String, ReadableAttributeProperty<?, ?>> attributes = new ReadOnlyMapWrapper<>(this, "attributes", FXCollections.observableMap(new TreeMap<>()));

	public ReadOnlyMapProperty<String, ReadableAttributeProperty<?, ?>> attributesProperty() {
		return this.attributes;
	}

	public ObservableMap<String, ReadableAttributeProperty<?, ?>> getAttributes() {
		return attributesProperty().get();
	}

	//endregion attributes
	//region children
	private final FolderChildrenProperty children;

	public FolderChildrenProperty childrenProperty() {
		return this.children;
	}

	public ObservableMap<String, FileSystemEntry> getChildren() {
		return this.childrenProperty().get();
	}

	//endregion children
	//region content

	private final FileContentProperty content;

	public FileContentProperty contentProperty() {
		return this.content;
	}

	public ByteBuffer getContent() {
		return this.contentProperty().get();
	}

	public void setContent(ByteBuffer value) {
		this.contentProperty().set(value);
	}


	//endregion content

	//region exists
	private final BooleanAttributeProperty exists;

	public BooleanAttributeProperty existsProperty() {
		return exists;
	}

	public boolean isExists() {
		return this.existsProperty().get();
	}

	public void setExists(boolean value) {
		this.existsProperty().set(value);
	}
	//endregion exists

	//region autoSync

	private final BooleanAttributeProperty autoSync;

	public BooleanAttributeProperty autoSyncProperty() {
		return autoSync;
	}

	public boolean isAutoSync() {
		return this.autoSyncProperty().get();
	}

	public void setAutoSync(boolean value) {
		this.autoSyncProperty().set(value);
	}
	//endregion autoSync


	//region autoFlush
	private final BooleanAttributeProperty autoFlush;

	public BooleanAttributeProperty autoFlushProperty() {
		return autoFlush;
	}

	public boolean isAutoFlush() {
		return this.autoFlushProperty().get();
	}

	public void setAutoFlush(boolean value) {
		this.autoFlushProperty().set(value);
	}
	//endregion autoFlush

	//region BasicFileAttributeProperties
	//region creationTime
	final BasicAttributeProperty<FileTime> creationTime;

	public BasicAttributeProperty<FileTime> creationTimeProperty() {
		return this.creationTime;
	}

	public FileTime getCreationTime() {
		return this.creationTimeProperty().getValue();
	}
	//endregion creationTime

	//region lastAccessTime
	final BasicAttributeProperty<FileTime> lastAccessTime;

	public BasicAttributeProperty<FileTime> lastAccessTimeProperty() {
		return this.lastAccessTime;
	}

	public FileTime getLastAccessTime() {
		return this.lastAccessTimeProperty().getValue();
	}
	//endregion lastAccessTime

	//region lastModifiedTime
	final BasicAttributeProperty<FileTime> lastModifiedTime;

	public BasicAttributeProperty<FileTime> lastModifiedTimeProperty() {
		return this.lastModifiedTime;
	}

	public FileTime getLastModifiedTime() {
		return this.lastModifiedTimeProperty().getValue();
	}
	//endregion lastModifiedTime

	//region isDirectory
	final BasicAttributeProperty<Boolean> isDirectory;

	public BasicAttributeProperty<Boolean> isDirectoryProperty() {
		return this.isDirectory;
	}

	public boolean isDirectory() {
		return this.isDirectoryProperty().getValue();
	}
	//endregion isDirectory

	//region isOther
	final BasicAttributeProperty<Boolean> isOther;

	public BasicAttributeProperty<Boolean> isOtherProperty() {
		return this.isOther;
	}

	public boolean isOther() {
		return this.isOtherProperty().getValue();
	}
	//endregion isOther

	//region isRegularFile
	final BasicAttributeProperty<Boolean> isRegularFile;

	public BasicAttributeProperty<Boolean> isRegularFileProperty() {
		return this.isRegularFile;
	}

	public boolean isRegularFile() {
		return this.isRegularFileProperty().getValue();
	}
	//endregion isRegularFile

	//region isSymbolicLink
	final BasicAttributeProperty<Boolean> isSymbolicLink;

	public BasicAttributeProperty<Boolean> isSymbolicLinkProperty() {
		return this.isSymbolicLink;
	}

	public boolean isSymbolicLink() {
		return this.isSymbolicLinkProperty().getValue();
	}
	//endregion isSymbolicLink

	//region size
	final BasicAttributeProperty<Long> fileSize;

	public BasicAttributeProperty<Long> fileSizeProperty() {
		return this.fileSize;
	}

	public Long getFileSize() {
		return this.fileSizeProperty().getValue();
	}

	//endregion size
	//endregion attributes
	//region Other
	@Override
	public String toString() {
		return String.valueOf(getPath());
	}

	public void createUserAttribute(String key, String value) {
		if (!attributes.containsKey(key)) {
			attributes.put(key, new StringAttributeProperty(this, key));
		}
		ReadableAttributeProperty<?, ?> property = attributes.get(key);
		if (property instanceof StringAttributeProperty stringProperty) {
			stringProperty.set(value);
		} else {
			throw new RuntimeException("Property already exists and isn't a String: " + name);
		}
	}

	public void clear() {
	}


	//endregion Other

}
