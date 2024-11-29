open module ezfx.polyglot {
	exports com.ezfx.polyglot;

	// EZFX
	requires transitive ezfx.base;

	// JavaFX
	requires transitive javafx.controls;
	requires transitive javafx.graphics;

	// Graal Polyglot
	requires transitive org.graalvm.polyglot;

	// Logging
	requires transitive org.slf4j;
}