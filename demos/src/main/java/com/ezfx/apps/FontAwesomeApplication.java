package com.ezfx.apps;

import com.ezfx.base.utils.Screens;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

public class FontAwesomeApplication extends Application {


	public static void main(String... args) {
		Application.launch(FontAwesomeApplication.class);
	}

	@Override
	public void start(Stage stage) {
		Screens.setScreen(stage, 1);

		TextField filterField = new TextField();
		ObservableList<FontAwesome.Glyph> glyphs = FXCollections.observableArrayList(FontAwesome.Glyph.values());
		FilteredList<FontAwesome.Glyph> filteredGlyphs = new FilteredList<>(glyphs);
		filteredGlyphs.predicateProperty().bind(filterField.textProperty().map(filterText -> glyph -> {
			String glyphName = glyph.name().toLowerCase();
			String filter = filterText.toLowerCase();
			return glyphName.contains(filter);
		}));

		BorderPane borderPane = new BorderPane();
		HBox top = new HBox(new Label("Filter: "), filterField);
		HBox.setHgrow(filterField, Priority.ALWAYS);
		top.setAlignment(Pos.CENTER);
		top.setPadding(new Insets(32));
		borderPane.setTop(top);

		GridView<FontAwesome.Glyph> gridView = new GridView<>();
		gridView.setCellWidth(128);
		gridView.setCellHeight(128);
		gridView.setCellFactory(_ -> new GridCell<>() {
			@Override
			protected void updateItem(FontAwesome.Glyph item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					Action action = new Action(item.name());
					action.setLongText(item.name());
					Glyph glyphNode = Glyph.create("FontAwesome|%s".formatted(item));
					glyphNode = glyphNode.sizeFactor(6);
					action.setGraphic(glyphNode);
					Button button = ActionUtils.createButton(action, ActionUtils.ActionTextBehavior.HIDE);
					button.setMinSize(128, 128);
					button.setMaxSize(128, 128);
					setGraphic(button);
				}
			}
		});

		gridView.setItems(filteredGlyphs);
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
