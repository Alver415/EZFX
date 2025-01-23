package com.ezfx.settings.themes;

import atlantafx.base.theme.Theme;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Modena implements Theme {

	@Override
	public String getName() {
		return "Modena";
	}

	@Override
	public String getUserAgentStylesheet() {
		return "com/sun/javafx/scene/control/skin/modena/modena.css";
	}

	@Override
	public String getUserAgentStylesheetBSS() {
		return "com/sun/javafx/scene/control/skin/modena/modena.bss";
	}

	@Override
	public boolean isDarkMode() {
		return false;
	}

	private final StringProperty example = new SimpleStringProperty(this, "example");

	public StringProperty exampleProperty() {
		return this.example;
	}

	public String getExample() {
		return this.exampleProperty().getValue();
	}

	public void setExample(String value) {
		this.exampleProperty().setValue(value);
	}
}
