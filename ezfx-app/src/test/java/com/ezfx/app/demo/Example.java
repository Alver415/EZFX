package com.ezfx.app.demo;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

public class Example {

	@Test
	public void test(){
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.forPackages("com") // Scan the root of the classpath
				.addScanners(Scanners.values()));

		System.out.println(reflections.getSubTypesOf(Example.class));
		System.out.println(reflections.getSubTypesOf(Effect.class));
	}

	private final StringProperty name = new SimpleStringProperty(this, "name");

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return this.nameProperty().getValue();
	}

	public void setName(String value) {
		this.nameProperty().setValue(value);
	}

	private final ObjectProperty<Background> background = new SimpleObjectProperty<>(this, "background", Background.fill(Color.RED));

	public ObjectProperty<Background> backgroundProperty() {
		return this.background;
	}

	public Background getBackground() {
		return this.backgroundProperty().get();
	}

	public void setBackground(Background value) {
		this.backgroundProperty().set(value);
	}

	private final ListProperty<String> strings = new SimpleListProperty<>(this, "strings", FXCollections.observableArrayList());

	public ListProperty<String> stringsProperty() {
		return this.strings;
	}

	public ObservableList<String> getStrings() {
		return this.stringsProperty().getValue();
	}

	public void setStrings(ObservableList<String> value) {
		this.stringsProperty().setValue(value);
	}
}