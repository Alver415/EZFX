package com.ezfx.apps;

import com.ezfx.app.EZFXApplication;
import com.ezfx.base.exception.UncheckedRunnable;
import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.tree.TreeControl;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LaunchFXApplication extends EZFXApplication {

	@Override
	public void start(Stage stage) throws Exception {
		Path libDir = Path.of("demos/lib");
		URL[] libs = Files.list(libDir).map(Path::toUri).map(uri -> {
			try {
				return uri.toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}).toList().toArray(new URL[0]);

		URLClassLoader child = new URLClassLoader(libs, this.getClass().getClassLoader());
		Thread.currentThread().setContextClassLoader(child);

		Collection<URL> allPackagePrefixes = Arrays.stream(Package.getPackages()).map(p -> p.getName())
				.map(s -> s.split("\\.")[0]).distinct().map(s -> ClasspathHelper.forPackage(s)).reduce((c1, c2) -> {
					Collection<URL> c3 = new HashSet<>();
					c3.addAll(c1);
					c3.addAll(c2);
					return c3;
				}).get();

		Collection<String> allPackages = Arrays.stream(Package.getPackages()).map(p -> p.getName())
				.map(s -> s.split("\\.")[0]).distinct().toList();

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.addUrls(allPackagePrefixes)
				.forPackages(allPackages.toArray(new String[0])) // Scan the root of the classpath
				.addScanners(Scanners.values()));
		List<Class<? extends Application>> applicationClasses = reflections.getSubTypesOf(Application.class)
				.stream()
				.filter(clazz -> !clazz.isInterface())
				.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
				.toList();

		TreeControl<Item> treeControl = new TreeControl<>();
		treeControl.setRoot(buildRoot(applicationClasses));
		treeControl.setShowRoot(false);
		treeControl.setChildrenProvider(Item::getChildren);
		treeControl.setCellFactory(_ -> new TreeCell<>() {
			private static final Image FOLDER_IMAGE = Resources.image(Icons.class, "mycons/folder-16.png");
			private static final Image FOLDER_EMPTY_IMAGE = Resources.image(Icons.class, "mycons/folder-empty-16.png");
			private static final Image FILE_IMAGE = Resources.image(Icons.class, "mycons/file-16.png");
			private final BorderPane borderPane;
			private final ImageView imageView = new ImageView();
			private final Text text = new Text();
			private final Button launch = new Button("Launch");

			{
				borderPane = new BorderPane();
				borderPane.setLeft(new HBox(4, imageView, text));
				borderPane.setRight(launch);
				launch.setPadding(new Insets(0, 4, 0, 4));
				launch.setOnAction(_ -> getItem().action.run());
			}

			@Override
			protected void updateItem(Item item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
					return;
				}
				text.setText(item.name);
				boolean isFolder = item.action == DO_NOTHING;
				imageView.setImage(isFolder ? item.children().isEmpty() ? FOLDER_EMPTY_IMAGE : FOLDER_IMAGE : FILE_IMAGE);
				borderPane.setRight(isFolder ? null : launch);
				setGraphic(borderPane);
			}
		});

		Scene scene = new Scene(treeControl);
		stage.setTitle("LaunchFX");
		stage.setScene(scene);
		stage.setWidth(400);
		stage.setHeight(600);
		stage.show();

	}

	private static <T extends Application> T startSecondaryApplication(Class<T> applicationClass) throws Exception {
		Constructor<T> constructor = applicationClass.getConstructor();
		T application = constructor.newInstance();
		application.init();
		application.start(new Stage());
		return application;
	}

	private static final Runnable DO_NOTHING = () -> {
	};

	private static Item buildRoot(List<Class<? extends Application>> classes) {
		Item root = new Item("root");
		classes.forEach(appClass -> {
			Item item = root;
			String[] packages = appClass.getPackage().getName().split("\\.");
			for (String subPackage : packages) {
				item = item.children.computeIfAbsent(subPackage, Item::new);
			}

			Item leaf = new Item(
					appClass.getSimpleName(),
					(UncheckedRunnable) () -> startSecondaryApplication(appClass));
			item.children.put(leaf.name, leaf);
		});
		return root;
	}

	private record Item(String name, Runnable action, ObservableMap<String, Item> children) {
		public Item(String name, Runnable action) {
			this(name, action, FXCollections.observableHashMap());
		}

		public Item(String name) {
			this(name, DO_NOTHING, FXCollections.observableHashMap());
		}

		public ObservableList<Item> getChildren() {
			Collection<Item> values = children.values();
			return FXCollections.observableArrayList(values);
		}
	}
}
