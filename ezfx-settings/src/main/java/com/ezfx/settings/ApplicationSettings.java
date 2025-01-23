package com.ezfx.settings;

import atlantafx.base.theme.*;
import com.ezfx.settings.themes.Caspian;
import com.ezfx.settings.themes.Modena;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class ApplicationSettings {

	public ApplicationSettings() {
		themeProperty()
				.map(ApplicationTheme::getTheme)
				.map(Theme::getUserAgentStylesheet)
				.subscribe(Application::setUserAgentStylesheet);
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

	// TODO: This enum is only needed to force the EditorFactory to build a SelectionEditor.
	// Should be a way to annotate properties as selections and supply a list of options.
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
