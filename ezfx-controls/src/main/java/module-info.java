open module ezfx.controls {
	exports com.ezfx.controls.console;
	exports com.ezfx.controls.explorer;
	exports com.ezfx.controls.utils;
	exports com.ezfx.controls.viewport;
	exports com.ezfx.controls.icons;
	exports com.ezfx.controls.nodetree;
	exports com.ezfx.controls.misc;
	exports com.ezfx.controls.editor;
	exports com.ezfx.controls.editor.introspective;
	exports com.ezfx.controls.editor.impl.standard;
	exports com.ezfx.controls.editor.impl.javafx;
	exports com.ezfx.controls.editor.skin;


	// EZFX
	requires transitive ezfx.base;

	// JavaFX
	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.graphics;

	// ReactFX
	requires reactfx;

	// EasyBind
	requires easybind;

	// FXMisc
	requires org.fxmisc.richtext;
	requires org.fxmisc.flowless;

	// ControlsFX
	requires transitive org.controlsfx.controls;

	// Logging
	requires transitive org.slf4j;

	// Reflections
	requires org.reflections;
}