open module ezfx.polyglot.test {
	exports com.ezfx.polyglot.test;

	// EZFX
	requires transitive ezfx.base;
	requires transitive ezfx.polyglot;

	// JavaFX
	requires transitive javafx.controls;
	requires transitive javafx.graphics;

	// Graal Polyglot
	requires transitive org.graalvm.polyglot;

	// Logging
	requires transitive org.slf4j;

	// Testing
	requires transitive org.junit.jupiter.api;
}