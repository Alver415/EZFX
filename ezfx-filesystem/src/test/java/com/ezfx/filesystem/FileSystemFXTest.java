package com.ezfx.filesystem;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactfx.EventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static com.ezfx.filesystem.TestUtil.sleep;
import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;


public class FileSystemFXTest {

	private static final Logger log = LoggerFactory.getLogger(FileSystemFXTest.class);
	private static Path root;

	private final Map<CountDownLatch, String> latches = new ConcurrentHashMap<>();

	private <T> void assertChange(ObservableValue<T> observable, Predicate<T> predicate) {
		String message = observable instanceof Property<?> property ? property.getName() : "Unknown Observable";
		assertChange(message, observable, predicate);
	}

	private <T> void assertChange(String message, ObservableValue<T> observable, Predicate<T> predicate) {
		CountDownLatch latch = new CountDownLatch(1);
		latches.put(latch, message);
		EventStreams.valuesOf(observable).filter(predicate).subscribe(_ -> latch.countDown());
	}

	@BeforeAll
	public static void beforeAll() {
		Platform.startup(() -> {
		});
	}

	@BeforeEach
	public void beforeEach() throws IOException {
		root = Files.createTempDirectory("FileSystemFXText");
		log.info(String.valueOf(root));
	}

	@AfterEach
	public void afterEach() throws InterruptedException {
		for (Map.Entry<CountDownLatch, String> latch : latches.entrySet()) {
			assertTrue(latch.getKey().await(1, TimeUnit.SECONDS), latch.getValue());
		}
	}

	@Test
	public void createFile() throws IOException {
		FileSystemFX fileSystem = new FileSystemFX(root);

		Path file = root.resolve("newFile");
		FileSystemEntry node = fileSystem.get(file);
		assertFalse(node.isExists());
		assertFalse(node.isRegularFile());
		assertFalse(node.isDirectory());

		Files.createFile(file);

		assertChange(node.existsProperty(), TestUtil::isTrue);
		assertChange(node.isRegularFileProperty(), TestUtil::isTrue);
		assertChange(node.isDirectoryProperty(), TestUtil::isFalse);
		assertChange(node.isOtherProperty(), TestUtil::isFalse);
		assertChange(node.isSymbolicLinkProperty(), TestUtil::isFalse);

		sleep(ofMillis(500));
		Files.writeString(file, "test string");
		assertChange(node.contentProperty(), content -> Objects.equals(content, ByteBuffer.wrap("test string".getBytes())));

		sleep(ofMillis(500));
		node.setContent(ByteBuffer.wrap("another test".getBytes()));
		sleep(ofMillis(500));
		ByteBuffer bytes = ByteBuffer.wrap(Files.readAllBytes(file));
		assertEquals(bytes, node.getContent());

		sleep(ofMillis(500));
		Files.delete(file);
		assertChange(node.existsProperty(), TestUtil::isFalse);
	}


	@Test
	public void createFolder() throws IOException {
		FileSystemFX fileSystem = new FileSystemFX(root);

		Path folder = root.resolve("newFolder");
		FileSystemEntry node = fileSystem.get(folder);
		assertFalse(node.isExists());
		assertFalse(node.isRegularFile());
		assertFalse(node.isDirectory());

		Files.createDirectory(folder);

		assertChange(node.existsProperty(), TestUtil::isTrue);
		assertChange(node.isRegularFileProperty(), TestUtil::isFalse);
		assertChange(node.isDirectoryProperty(), TestUtil::isTrue);
		assertChange(node.isOtherProperty(), TestUtil::isFalse);
		assertChange(node.isSymbolicLinkProperty(), TestUtil::isFalse);
	}


}
