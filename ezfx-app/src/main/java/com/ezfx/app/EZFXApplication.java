package com.ezfx.app;

import atlantafx.base.theme.*;
import com.ezfx.app.explorer.ApplicationExplorer;
import com.ezfx.base.utils.ListChangeListeners;
import com.ezfx.settings.themes.Caspian;
import com.ezfx.settings.themes.Modena;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class EZFXApplication extends Application {

	private static final Logger log = LoggerFactory.getLogger(EZFXApplication.class);

	@Override
	public void init() throws Exception {

//		try {
//			Files.readAllLines(Path.of("ezfx-app/src/main/resources/settings.properties")).stream().forEach(string -> {
//				int splitIndex = string.indexOf("=");
//				String key = string.substring(0, splitIndex);
//				String value = string.substring(splitIndex + 1);
//				if (key.equals("theme")) {
//					setTheme(ApplicationTheme.valueOf(value));
//				}
//			});
//		} catch (Exception e){
//			log.warn("Failed to load settings.properties", e);
//		}

		//This is needed to ensure removeEventFilter works
		EventHandler<KeyEvent> handleDevToolsAction = this::handleDevToolsAction;

		Window.getWindows().addListener(ListChangeListeners.forEachAdded(window -> {
			// Not, we remove before adding to ensure we don't add multiple handlers to stages
			// as they are shown/hidden multiple times.
			window.removeEventFilter(KeyEvent.KEY_PRESSED, handleDevToolsAction);
			window.addEventHandler(KeyEvent.KEY_PRESSED, handleDevToolsAction);

			if (window instanceof Stage stage){
				if (stage.getTitle() == null && getTitle() != null) {
					stage.setTitle(getTitle());
				}
			}

		}));

		themeProperty()
				.map(ApplicationTheme::getTheme)
				.map(Theme::getUserAgentStylesheet)
				.subscribe(Application::setUserAgentStylesheet);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		Platform.exit();
	}

	private void handleDevToolsAction(KeyEvent event) {
		if (!getDevToolsShortcut().match(event)) return;

		// If devTools hasn't been initialized, build the stage with application explorer.
		if (getDevToolsStage() == null) {
			Stage devToolsStage = new Stage();
			devToolsStage.setWidth(1200);
			devToolsStage.setHeight(800);
			setDevToolsStage(devToolsStage);

			Scene scene = new Scene(new StackPane(new Text("LOADING")));
			devToolsStage.setScene(scene);
			devToolsStage.setTitle("Application Explorer");

			Platform.runLater(() -> {
				ApplicationExplorer applicationExplorer = new ApplicationExplorer(this);
				scene.setRoot(applicationExplorer);
			});
		}
		// Toggle show/hide
		Stage stage = getDevToolsStage();
		if (stage.isShowing()) {
			stage.hide();
		} else {
			stage.show();
		}
	}

	private final Property<Stage> devToolsStage = new SimpleObjectProperty<>(this, "devToolsStage");

	public Property<Stage> devToolsStageProperty() {
		return this.devToolsStage;
	}

	public Stage getDevToolsStage() {
		return this.devToolsStageProperty().getValue();
	}

	public void setDevToolsStage(Stage value) {
		this.devToolsStageProperty().setValue(value);
	}

	private final Property<KeyCombination> devToolsShortcut = new SimpleObjectProperty<>(this, "devToolsShortcut", new KeyCodeCombination(KeyCode.F12));

	public Property<KeyCombination> devToolsShortcutProperty() {
		return this.devToolsShortcut;
	}

	public KeyCombination getDevToolsShortcut() {
		return this.devToolsShortcutProperty().getValue();
	}

	public void setDevToolsShortcut(KeyCombination value) {
		this.devToolsShortcutProperty().setValue(value);
	}


	private final StringProperty title = new SimpleStringProperty(this, "title", "EZFX Application");

	public StringProperty titleProperty() {
		return this.title;
	}

	public String getTitle() {
		return this.titleProperty().getValue();
	}

	public void setTitle(String value) {
		this.titleProperty().setValue(value);
	}

	private final Property<Image> icon = new SimpleObjectProperty<>(this, "icon");

	public Property<Image> iconProperty() {
		return this.icon;
	}

	public Image getIcon() {
		return this.iconProperty().getValue();
	}

	public void setIcon(Image value) {
		this.iconProperty().setValue(value);
	}

	private final Property<ApplicationTheme> theme = new SimpleObjectProperty<>(this, "theme", ApplicationTheme.MODENA);

	public Property<ApplicationTheme> themeProperty() {
		return this.theme;
	}

	public ApplicationTheme getTheme() {
		return this.themeProperty().getValue();
	}

	public void setTheme(ApplicationTheme value) {
		this.themeProperty().setValue(value);
	}

	public enum ApplicationTheme {
		MODENA(new Modena()),
		CASPIAN(new Caspian()),
		DRACULA(new Dracula()),
		NORD_DARK(new NordDark()),
		NORD_LIGHT(new NordLight()),
		CUPERTINO_DARK(new CupertinoDark()),
		CUPERTINO_LIGHT(new CupertinoLight()),
		PRIMER_DARK(new PrimerDark()),
		PRIMER_LIGHT(new PrimerLight());

		private final Theme theme;

		ApplicationTheme(Theme theme) {
			this.theme = theme;
		}

		public Theme getTheme() {
			return theme;
		}
	}

}
