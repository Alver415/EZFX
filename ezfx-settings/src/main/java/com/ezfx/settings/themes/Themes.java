package com.ezfx.settings.themes;

import atlantafx.base.theme.*;

import java.util.Set;

public interface Themes {

	static Set<Theme> getThemes() {
		Dracula dracula = new Dracula();
		NordLight nordLight = new NordLight();
		NordDark nordDark = new NordDark();
		PrimerLight primerLight = new PrimerLight();
		PrimerDark primerDark = new PrimerDark();
		CupertinoLight cupertinoLight = new CupertinoLight();
		CupertinoDark cupertinoDark = new CupertinoDark();
		Modena modena = new Modena();
		Caspian caspian = new Caspian();
		return Set.of(
				modena, caspian, dracula,
				nordLight, nordDark,
				cupertinoLight, cupertinoDark,
				primerLight, primerDark);
	}
}
