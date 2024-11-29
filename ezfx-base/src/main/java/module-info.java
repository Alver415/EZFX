open module ezfx.base {
	exports com.ezfx.base.exception;
	exports com.ezfx.base.io;
	exports com.ezfx.base.linkable;
	exports com.ezfx.base.observable;
	exports com.ezfx.base.utils;

	// JavaFX
	requires transitive javafx.base;
	requires transitive javafx.graphics;

	// Logging
	requires transitive org.slf4j;
}