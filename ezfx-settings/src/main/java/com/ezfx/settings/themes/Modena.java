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
}
