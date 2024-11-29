package com.ezfx.filesystem;

import com.ezfx.base.exception.UncheckedRunnable;
import com.ezfx.base.exception.UncheckedSupplier;
import com.ezfx.base.utils.EZFX;
import com.ezfx.filesystem.FileSystemWatcher.FileSystemEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.ezfx.filesystem.ExecutionManager.IO_THREAD_NAME;

public class FileSystemFX {

	static final Logger log = LoggerFactory.getLogger(FileSystemFX.class);
	static final Path DEFAULT_ROOT_PATH = Path.of(System.getProperty("user.dir"));

	final ExecutorService ioExecutor;
	final Executor fxExecutor;

	final FileSystemModel model;

	final EventSource<Throwable> errors;
	final ExecutionManager executionManager;

	public FileSystemFX() throws IOException {
		this(DEFAULT_ROOT_PATH, new LocalFileSystemWatcher());
	}

	public FileSystemFX(Path rootPath) throws IOException {
		this(rootPath, new LocalFileSystemWatcher());
	}

	public FileSystemFX(Path rootPath, FileSystemWatcher fileSystemWatcher) throws IOException {
		this.ioExecutor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, IO_THREAD_NAME));
		this.fxExecutor = EZFX::runFX;
		this.executionManager = new ExecutionManager(ioExecutor, fxExecutor);
		this.errors = new EventSource<>();
		this.errors.subscribe(e -> log.error(e.getMessage(), e));

		this.fileSystemWatcher.setValue(fileSystemWatcher);
		this.model = new FileSystemModel(this, rootPath);

		this.fileSystemWatcher.map(this::routeEvents).subscribe((old, _) -> old.unsubscribe());

		getRoot().requestSync();
	}

	//region Public API

	public FileSystemEntry get(Path path) {
		return model.get(path);
	}

	public FileSystemEntry getRoot() {
		return model.getRoot();
	}

	public FileSystemModel getModel() {
		return model;
	}

	public ExecutionManager getExecutionManager() {
		return executionManager;
	}

	public void close() throws IOException {
		getFileSystemWatcher().close();
		ioExecutor.close();
	}

	//endregion Public API

	//region FileSystemWatcher
	final ObjectProperty<FileSystemWatcher> fileSystemWatcher = new SimpleObjectProperty<>(
			this, "fileSystemWatcher");

	public ObjectProperty<FileSystemWatcher> fileSystemWatcherProperty() {
		return this.fileSystemWatcher;
	}

	public FileSystemWatcher getFileSystemWatcher() {
		return this.fileSystemWatcherProperty().get();
	}

	public void setFileSystemWatcher(FileSystemWatcher value) {
		this.fileSystemWatcherProperty().set(value);
	}

	void create(FileSystemEntry node) {
		node.setExists(Files.exists(node.getPath()));
		node.getParent().getChildren().put(node.getName(), node);
	}

	void modify(FileSystemEntry node) {
		node.requestSync();
	}

	void delete(FileSystemEntry node) {
		node.setExists(false);
		node.getParent().getChildren().remove(node.getName());
		getFileSystemWatcher().unregister(node.getPath());
		log.debug("Deleted: {}", node);
	}


	Subscription routeEvents(FileSystemWatcher watcher) {
		EventStream<FileSystemEvent> events = watcher.getFileSystemEvents().threadBridgeToFx(ioExecutor);
		Subscription create = routeEvents(events, FileSystemEvent::isCreate, this::create);
		Subscription modify = routeEvents(events, FileSystemEvent::isModify, this::modify);
		Subscription delete = routeEvents(events, FileSystemEvent::isDelete, this::delete);
//		events.subscribe(event -> log.debug("{}: {}", event.getKind(), event.getPath()));
		return Subscription.multi(create, modify, delete);
	}

	Subscription routeEvents(
			EventStream<FileSystemEvent> events,
			Predicate<FileSystemEvent> filter,
			Consumer<FileSystemEntry> consumer) {
		return events.filter(filter)
				.map(FileSystemEvent::getPath)
				.map(model::get)
				.filter(FileSystemEntry::isAutoSync)
				.subscribe(consumer);
	}
	//endregion FileSystemWatcher

	//region ExecutionManager
	public CompletableFuture<Void> executeIO(UncheckedRunnable runnable) {
		return executionManager.executeIO(runnable);
	}

	public <T> CompletableFuture<T> executeIO(UncheckedSupplier<T> supplier) {
		return executionManager.executeIO(supplier);
	}

	public CompletableFuture<Void> executeFX(UncheckedRunnable runnable) {
		return executionManager.executeFX(runnable);
	}

	public <T> CompletableFuture<T> executeFX(UncheckedSupplier<T> supplier) {
		return executionManager.executeFX(supplier);
	}

	<T> T logError(Throwable t) {
		log.error(t.getMessage(), t);
		return null;
	}
	//endregion ExecutionManager
}
