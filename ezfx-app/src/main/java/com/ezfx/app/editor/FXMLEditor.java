package com.ezfx.app.editor;

import com.ezfx.fxml.FXMLSaver;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class FXMLEditor extends SceneEditor {

	private final ObjectProperty<File> file = new SimpleObjectProperty<>(this, "file");

	public ObjectProperty<File> fileProperty() {
		return this.file;
	}

	public File getFile() {
		return this.fileProperty().get();
	}

	public void setFile(File value) {
		this.fileProperty().set(value);
	}

	public FXMLEditor() {
		this(null);
	}

	public FXMLEditor(File initialFile) {
		targetProperty().bind(file.map(FXMLEditor::loadFXML));
		setFile(initialFile);
	}

	private static Node loadFXML(File file) {
		try {
			FXMLLoader loader = new FXMLLoader(file.toURI().toURL());
			return loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXMLEditorSkin(this);
	}

	public static class FXMLEditorSkin extends SceneEditorSkin {

		protected FXMLEditorSkin(FXMLEditor control) {
			super(control);
			MenuBar menuBar = new MenuBar();
			Menu fileMenu = new Menu("File");

			MenuItem open = new MenuItem("Open...");
			open.setOnAction(a -> {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(Path.of(System.getProperty("user.dir")).toFile());
				Optional.ofNullable(fileChooser.showOpenDialog(control.getScene().getWindow()))
						.ifPresent(control::setFile);
			});
			MenuItem save = new MenuItem("Save");
			save.setOnAction(a -> {
				FXMLSaver saver = new FXMLSaver();
				saver.save(control.getFile(), control.getTarget());
			});

			MenuItem saveAs = new MenuItem("Save As...");
			saveAs.setOnAction(a -> {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(Path.of(System.getProperty("user.dir")).toFile());
				Optional.ofNullable(fileChooser.showOpenDialog(control.getScene().getWindow()))
						.ifPresent(file -> {
							FXMLSaver saver = new FXMLSaver();
							saver.save(file, control.getTarget());
						});
			});

			fileMenu.getItems().setAll(open, saveAs);
			menuBar.getMenus().setAll(fileMenu);
			borderPane.setTop(menuBar);
		}
	}
}