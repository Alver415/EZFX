open module ezfx.app.test {
	exports com.ezfx.app.demo;

	// EZFX
	requires transitive ezfx.base;
	requires transitive ezfx.controls;
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
	requires org.junit.jupiter.api;
	requires org.reflections;
	requires javafx.fxml;
	requires org.graalvm.nativebridge;
}