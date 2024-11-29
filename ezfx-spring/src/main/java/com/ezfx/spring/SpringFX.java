package com.ezfx.spring;

import com.ezfx.spring.annotations.FXMLAutoLoad;
import com.ezfx.spring.model.ControllerAndView;
import com.ezfx.spring.model.LazyControllerAndView;
import com.ezfx.spring.model.SimpleControllerAndView;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

@SuppressWarnings("ALL")
@Component
public class SpringFX {

	private static final Logger log = LoggerFactory.getLogger(SpringFX.class);

	protected ApplicationContext context;

	@Autowired
	public SpringFX(ApplicationContext context) {
		this.context = context;
	}

	public <Controller> Controller loadController(Class<Controller> clazz) {
		return load(clazz).controller();
	}

	public <T> T loadView(Class<?> clazz) {
		return (T) load(clazz).view();
	}

	public <Controller, View> ControllerAndView<Controller, View> load(Class<Controller> clazz) {
		return load(clazz, null);
	}

	public <Controller> Controller loadController(Controller controller) {
		return load(controller).controller();
	}

	public <T> T loadView(Object controller) {
		return (T) load(controller).view();
	}

	@SuppressWarnings("unchecked")
	public <Controller, View> ControllerAndView<Controller, View> load(Controller controller) {
		Class<Controller> clazz = (Class<Controller>) controller.getClass();
		return load(clazz, controller);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected <Controller, View> ControllerAndView<Controller, View> load(
			Class<Controller> clazz,
			Controller controller) {
		Objects.requireNonNull(AnnotationUtils.findAnnotation(clazz, FXMLAutoLoad.class),
				"%s is not annotated with @FXMLLoad.".formatted(clazz.getSimpleName()));

		URL location = getLocation(clazz);
		ResourceBundle resources = getResources(clazz);
		ClassLoader classLoader = clazz.getClassLoader();

		SpringFXLoader fxmlLoader = context.getBean(SpringFXLoader.class);
		fxmlLoader.setLocation(location);
		fxmlLoader.setResources(resources);
		fxmlLoader.setClassLoader(classLoader);
		fxmlLoader.setRoot(controller);
		if (controller != null &&
				(controller.getClass().isAssignableFrom(Node.class) ||
						Node.class.isAssignableFrom(controller.getClass()))) {
			fxmlLoader.setController(controller);
		}

		return switch (getMode(clazz)) {
			case EAGER -> loadSimple(fxmlLoader);
			case LAZY -> loadLazy(fxmlLoader);
		};
	}

	private <Controller, View> SimpleControllerAndView<Controller, View> loadSimple(SpringFXLoader fxmlLoader) {
		try {
			View view = fxmlLoader.load();
			Controller controller = fxmlLoader.getController();
			return ControllerAndView.simple(controller, view);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private <Controller, View> LazyControllerAndView<Controller, View> loadLazy(SpringFXLoader fxmlLoader) {
		return ControllerAndView.lazy(() -> loadSimple(fxmlLoader));
	}

	private FXMLAutoLoad.Mode getMode(Class<?> clazz) {
		FXMLAutoLoad annotation = Objects.requireNonNull(
				AnnotationUtils.findAnnotation(clazz, FXMLAutoLoad.class));
		return annotation.mode();
	}

	private URL getLocation(Class<?> clazz) {
		FXMLAutoLoad annotation = Objects.requireNonNull(
				AnnotationUtils.findAnnotation(clazz, FXMLAutoLoad.class));
		String location = annotation.location().isBlank() ?
				clazz.getSimpleName() + ".fxml" :
				annotation.location();
		return clazz.getResource(location);
	}

	private ResourceBundle getResources(Class<?> clazz) {
		FXMLAutoLoad annotation = Objects.requireNonNull(
				AnnotationUtils.findAnnotation(clazz, FXMLAutoLoad.class));
		String name = annotation.resourceBundle().isBlank() ?
				clazz.getName() :
				annotation.resourceBundle();
		try {
			return ResourceBundle.getBundle(name);
		} catch (MissingResourceException e) {
			log.trace("Failed to find Resource Bundle: " + name, e);
			return null;
		}
	}
}