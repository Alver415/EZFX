open module ezfx.fxml {
	exports com.ezfx.fxml;

	// JavaFX
	requires transitive javafx.base;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;

	// Logging
	requires transitive org.slf4j;

	// Jackson
	requires transitive com.fasterxml.jackson.core;
	requires transitive com.fasterxml.jackson.databind;
	requires transitive com.fasterxml.jackson.dataformat.xml;


	requires transitive java.xml;
	requires transitive java.desktop;
}