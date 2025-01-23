open module ezfx.settings {
	exports com.ezfx.settings.themes;
	exports com.ezfx.settings;

	// EZFX
	requires transitive ezfx.base;
	requires transitive ezfx.controls;

	// JavaFX
	requires transitive javafx.controls;
	requires transitive javafx.graphics;

	// AtalantaFX
	requires transitive atlantafx.base;

}