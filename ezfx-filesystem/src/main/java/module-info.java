open module ezfx.filesystem {
	exports com.ezfx.filesystem;
	exports com.ezfx.filesystem.utils;

	// EZFX
	requires transitive ezfx.base;

	// JavaFX
	requires transitive javafx.base;

	// ReactFX
	requires transitive reactfx;

	// Logging
	requires transitive org.slf4j;

	// Apache Commons
	requires transitive org.apache.commons.io;
}