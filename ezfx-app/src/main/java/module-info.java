open module ezfx.app {
	exports com.ezfx.app.console;
	exports com.ezfx.app.demo;
	exports com.ezfx.app.editor;

	// EZFX
	requires transitive ezfx.base;
	requires transitive ezfx.controls;
	requires transitive ezfx.fxml;
	requires transitive ezfx.settings;
	requires transitive ezfx.filesystem;
	requires transitive ezfx.polyglot;
//	requires transitive ezfx.spring;

	// JavaFX
	requires transitive javafx.controls;
	requires transitive javafx.graphics;

	// Graal Polyglot
	requires transitive org.graalvm.polyglot;

	// Logging
	requires transitive org.slf4j;
	requires org.apache.logging.log4j;
}