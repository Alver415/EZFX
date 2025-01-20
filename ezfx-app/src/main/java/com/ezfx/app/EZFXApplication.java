package com.ezfx.app;

import com.ezfx.app.explorer.ApplicationExplorer;
import com.ezfx.base.utils.ListChangeListeners;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class EZFXApplication extends Application {

	@Override
	public void init() throws Exception {
		//This is needed to ensure removeEventFilter works
		EventHandler<KeyEvent> handleDevToolsAction = this::handleDevToolsAction;

		Window.getWindows().addListener(ListChangeListeners.forEachAdded(window -> {
			// Not, we remove before adding to ensure we don't add multiple handlers to stages
			// as they are shown/hidden multiple times.
			window.removeEventFilter(KeyEvent.KEY_PRESSED, handleDevToolsAction);
			window.addEventHandler(KeyEvent.KEY_PRESSED, handleDevToolsAction);
			darkModeProperty().map(darkMode -> darkMode ? BlendMode.DIFFERENCE : BlendMode.SRC_OVER)
					.subscribe(blendMode -> window.getScene().getRoot().setBlendMode(blendMode));
		}));
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

	private final BooleanProperty darkMode = new SimpleBooleanProperty(this, "darkMode");

	public BooleanProperty darkModeProperty() {
		return this.darkMode;
	}

	public Boolean getDarkMode() {
		return this.darkModeProperty().getValue();
	}

	public void setDarkMode(Boolean value) {
		this.darkModeProperty().setValue(value);
	}

}
