package com.ezfx.base.utils;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

// TODO: Cache everything so we don't load each image/css/fxml file multiple times.
public interface Resources {

	Logger log = LoggerFactory.getLogger(Resources.class);

	static String css(Class<?> clazz, String name) {
		return Objects.requireNonNull(clazz.getResource(name)).toExternalForm();
	}

	static URL fxml(Class<?> clazz, String name) {
		return Objects.requireNonNull(clazz.getResource(name));
	}

	static File file(Class<?> clazz, String resource) {
		try {
			return new File(Objects.requireNonNull(clazz.getResource(resource)).toURI());
		} catch (Exception e) {
			throw new RuntimeException("Failed to load resource: %s %s".formatted(clazz, resource), e);
		}
	}

	static Image image(Object object, String resource) {
		return image(object.getClass(), resource);
	}

	static Image image(Class<?> clazz, String resource) {
		InputStream resourceStream = clazz.getResourceAsStream(resource);
		if (resourceStream == null) {
			log.warn("Failed to find resource: %s %s".formatted(clazz, resource));
			return null;
		}
		return new Image(resourceStream);
	}

}
