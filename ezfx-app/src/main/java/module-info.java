open module ezfx.app {
	exports com.ezfx.app.console;
	exports com.ezfx.app.editor;
	exports com.ezfx.app.stage;
	exports com.ezfx.app.explorer;
	exports com.ezfx.app;

	// EZFX
	requires transitive ezfx.base;
	requires transitive ezfx.controls;
	requires transitive ezfx.fxml;
	requires transitive ezfx.settings;
	requires transitive ezfx.filesystem;
	requires transitive ezfx.polyglot;

	// JavaFX
	requires transitive javafx.controls;
	requires transitive javafx.graphics;

	// Graal Polyglot
	requires transitive org.graalvm.polyglot;

	// Logging
	requires transitive org.slf4j;
	requires org.fxmisc.flowless;
	requires org.fxmisc.richtext;
	requires easybind;
}