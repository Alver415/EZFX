package com.ezfx.controls.explorer;

import com.ezfx.base.utils.Screens;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.stage.Stage;

public class ApplicationExplorer extends Control {

	public static void explore(Application application) {
		explore(application, 2);
	}

	public static void explore(Application application, int screenIndex) {
		Stage stage = new Stage();
		Screens.setScreen(stage, screenIndex);

		FrameInfo.start();
		stage.titleProperty().bind(Bindings.createStringBinding(() ->
						"Application Explorer - FPS: %.0f | Frame Time: %dms"
								.formatted(FrameInfo.animationTicks.get(), FrameInfo.animationFrames.get()),
				FrameInfo.animationFrames, FrameInfo.animationTicks));

		ApplicationExplorer applicationExplorer = new ApplicationExplorer(application);
		applicationExplorer.setPrefSize(1200, 800);
		stage.setScene(new Scene(applicationExplorer));

		stage.show();
	}

	public ApplicationExplorer(Application application) {
		setApplication(application);
	}

	private final Property<Application> application = new SimpleObjectProperty<>(this, "application");

	public Property<Application> applicationProperty() {
		return this.application;
	}

	public Application getApplication() {
		return this.applicationProperty().getValue();
	}

	public void setApplication(Application value) {
		this.applicationProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ApplicationExplorerSkin(this);
	}

}
