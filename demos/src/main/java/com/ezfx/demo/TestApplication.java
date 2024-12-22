package com.ezfx.demo;

import com.ezfx.base.utils.Screens;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorWrapper;
import com.ezfx.controls.editor.introspective.IntrospectingPropertiesEditor;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class TestApplication extends Application {


	public static void main(String... args) {
		Application.launch(TestApplication.class);
	}

	@Override
	public void start(Stage stage) {
		Screens.setScreen(stage, 1);

		Example example = new Example();
		Editor<Example> editor = new IntrospectingPropertiesEditor<>(example);

		BorderPane borderPane = new BorderPane();
		Text text = new Text();
		StackPane stackPane = new StackPane(text);

		editor.valueProperty().flatMap(Example::shapeProperty).subscribe(borderPane::setCenter);

		editor.valueProperty().flatMap(Example::backgroundProperty)
				.subscribe(stackPane.backgroundProperty()::setValue);
		editor.valueProperty().flatMap(Example::stringsProperty)
				.map(s -> s.stream().sorted().collect(Collectors.joining(", ")))
				.subscribe(text.textProperty()::setValue);
		editor.valueProperty().flatMap(Example::effectProperty)
				.subscribe(text.effectProperty()::setValue);

		borderPane.setCenter(stackPane);
		EditorWrapper<Example, Editor<Example>> wrapper = new EditorWrapper<>("Editor", editor);
		wrapper.setPrefWidth(600);
		borderPane.setRight(wrapper);
		Scene scene = new Scene(borderPane);
		stage.setScene(scene);
		stage.setWidth(600);
		stage.setHeight(600);
		stage.centerOnScreen();
		stage.setTitle("Test Application");
		stage.show();
	}

	public static class Example {

		public Example() {

		}

		public Example(String name, Background background, ObservableList<String> strings) {
			setName(name);
			setBackground(background);
			setStrings(strings);
		}

		private final BooleanProperty trueOrFalse = new SimpleBooleanProperty(this, "trueOrFalse");

		public BooleanProperty trueOrFalseProperty() {
			return this.trueOrFalse;
		}

		public Boolean getTrueOrFalse() {
			return this.trueOrFalseProperty().getValue();
		}

		public void setTrueOrFalse(Boolean value) {
			this.trueOrFalseProperty().setValue(value);
		}

		private final Property<Shape> shape = new SimpleObjectProperty<>(this, "shape");

		public Property<Shape> shapeProperty() {
			return this.shape;
		}

		public Shape getShape() {
			return this.shapeProperty().getValue();
		}

		public void setShape(Shape value) {
			this.shapeProperty().setValue(value);
		}

		private final Property<Effect> effect = new SimpleObjectProperty<>(this, "effect");

		public Property<Effect> effectProperty() {
			return this.effect;
		}

		public Effect getEffect() {
			return this.effectProperty().getValue();
		}

		public void setEffect(Effect value) {
			this.effectProperty().setValue(value);
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
}
