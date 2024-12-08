package com.ezfx.app.demo;

import com.ezfx.base.utils.Screens;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.control.cell.ColorGridCell;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.util.List;
import java.util.stream.Stream;

public class FontAwesomeApplication extends Application {


	public static void main(String... args) {
		Application.launch(FontAwesomeApplication.class);
	}

	@Override
	public void start(Stage stage) {
		Screens.setScreen(stage, 1);

		BorderPane borderPane = new BorderPane();

		GridView<Node> gridView = new GridView<>();
		List<Button> actions = Stream.of(FontAwesome.Glyph.values())
				.map(glyph -> {
					Action action = new Action("");
					action.setLongText(glyph.name());
					Glyph glyphNode = Glyph.create("FontAwesome|%s".formatted(glyph));
					//FIXME: Can't adjust font size via css for some reason.
//					glyphNode.setStyle("-fx-font-size: 36px");
					glyphNode = glyphNode.sizeFactor(6);
					action.setGraphic(glyphNode);
					return action;
				})
				.map(ActionUtils::createButton)
				.peek(button -> button.setMinSize(128,128))
				.peek(button -> button.setMaxSize(128,128))
				.toList();
		gridView.setCellWidth(128);
		gridView.setCellHeight(128);
		gridView.setCellFactory(_ -> new GridCell<>(){
			@Override
			protected void updateItem(Node item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null){
					setText(null);
					setGraphic(null);
				} else {
					setGraphic(item);
				}
			}
		});

		gridView.getItems().setAll(actions);
		borderPane.setCenter(gridView);

		Scene scene = new Scene(borderPane);
		stage.setScene(scene);
		stage.setWidth(600);
		stage.setHeight(600);
		stage.centerOnScreen();
		stage.setTitle("FontAwesome Glyph Viewer");
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
