package com.ezfx.apps;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.editor.SceneEditor;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.base.utils.Screens;
import com.ezfx.controls.icons.Icons;
import com.ezfx.filesystem.FileSystemEntry;
import com.ezfx.filesystem.FileSystemFX;
import com.ezfx.fxml.FXMLSaver;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static com.ezfx.base.utils.Converters.STRING_TO_BYTE_BUFFER;

public class FXMLEditorApplication extends EZFXApplication {

	private FileSystemFX fileSystem;

	@Override
	public void init() throws Exception {
		super.init();
		fileSystem = new FileSystemFX();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Stage stage = new DecoratedStage();
		SceneEditor sceneEditor = new SceneEditor();

		content = pathProperty().map(fileSystem::get)
				.flatMap(FileSystemEntry::contentProperty)
				.map(STRING_TO_BYTE_BUFFER::from);
		content.addListener((_, _, value) -> sceneEditor.setTarget(loadSceneTarget(getPath())));

		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");

		MenuItem open = new MenuItem("Open...");
		open.setOnAction(_ -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(Path.of(System.getProperty("user.dir")).toFile());
			Optional.ofNullable(fileChooser.showOpenDialog(stage))
					.map(File::toPath)
					.ifPresent(this::setPath);
		});

		MenuItem save = new MenuItem("Save");
		save.setOnAction(_ -> {
			FXMLSaver saver = new FXMLSaver();
			saver.save(getPath().toFile(), sceneEditor.getTarget());
		});

		MenuItem saveAs = new MenuItem("Save As...");
		saveAs.setOnAction(_ -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(Path.of(System.getProperty("user.dir")).toFile());
			Optional.ofNullable(fileChooser.showOpenDialog(stage))
					.ifPresent(file -> {
						FXMLSaver saver = new FXMLSaver();
						saver.save(file, sceneEditor.getTarget());
					});
		});

		fileMenu.getItems().setAll(open, save, saveAs);
		menuBar.getMenus().setAll(fileMenu);

		BorderPane root = new BorderPane();
		root.setTop(menuBar);
		root.setCenter(sceneEditor);

		Screens.setScreen(stage, 1);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("FXML Editor");
		stage.getIcons().add(Icons.EDIT);
		stage.show();

		// Load initial file
		Optional.ofNullable(getParameters())
				.map(Parameters::getNamed)
				.map(p -> p.get("fxml"))
				.map(Path::of)
				.ifPresent(this::setPath);
	}

	private static Node loadSceneTarget(Path path) {
		try {
			FXMLLoader loader = new FXMLLoader(path.toUri().toURL());
			return loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final Property<Path> path = new SimpleObjectProperty<>(this, "path");
	private ObservableValue<String> content;

	public Property<Path> pathProperty() {
		return this.path;
	}

	public Path getPath() {
		return this.pathProperty().getValue();
	}

	public void setPath(Path value) {
		this.pathProperty().setValue(value);
	}

}
