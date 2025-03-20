open module ezfx.base {
	exports com.ezfx.base.exception;
	exports com.ezfx.base.io;
	exports com.ezfx.base.linkable;
	exports com.ezfx.base.observable;
	exports com.ezfx.base.utils;
	exports com.ezfx.base.introspector;

	// JavaFX
	requires transitive javafx.base;
	requires transitive javafx.graphics;

	// Logging
	requires transitive org.slf4j;

	// Other
	requires javassist;
	requires jdk.jfr;
	requires java.desktop;
	requires org.reflections;
	requires com.google.common;
}