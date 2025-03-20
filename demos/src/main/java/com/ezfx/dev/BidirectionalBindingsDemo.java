package com.ezfx.dev;

import com.ezfx.app.EZFXApplication;
import com.ezfx.base.utils.BidirectionalContentBindings;
import com.ezfx.base.utils.Converter;
import com.ezfx.controls.MapView;
import com.ezfx.controls.editor.impl.javafx.ColorEditor;
import com.ezfx.controls.utils.SplitPanes;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.stream.Stream;

public class BidirectionalBindingsDemo extends EZFXApplication {
	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(buildScene());
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}

	private Scene buildScene() {
		TabPane tabPane = new TabPane();
		Tab listTab = new Tab("List");
		Tab setTab = new Tab("Set");
		Tab mapTab = new Tab("Map");

		listTab.setContent(buildList());
		setTab.setContent(buildSet());
		mapTab.setContent(buildMap());

		tabPane.getTabs().setAll(Stream.of(listTab, setTab, mapTab)
				.peek(tab -> tab.setClosable(false))
				.toList());

		return new Scene(tabPane);
	}

	private Node buildList() {

		Converter<String, Color> converter = Converter.passingNull(
				string -> {
					try {
						return Color.web(string);
					} catch (Exception e) {
						return Color.GREY;
					}
				},
				Object::toString);

		ListView<String> stringList = new ListView<>();
		TextField stringInput = new TextField();
		stringInput.setOnAction(_ -> {
			stringList.getItems().add(stringInput.getText());
			stringInput.setText(null);
		});
		stringList.setCellFactory(_ -> new ListCell<>() {
			private final Rectangle rectangle = new Rectangle(16, 16);

			{
				setOnMouseClicked(event -> {
					if (event.getClickCount() == 2) {
						stringList.getItems().remove(getIndex());
					}
				});
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item);
				}
			}
		});

		ListView<Color> colorList = new ListView<>();
		ColorEditor colorEditor = new ColorEditor();
		colorEditor.valueProperty().subscribe(color -> colorList.getItems().add(color));
		colorList.setCellFactory(_ -> new ListCell<>() {
			private final Rectangle rectangle = new Rectangle(16, 16);

			{
				setOnMouseClicked(event -> {
					if (event.getClickCount() == 2) {
						colorList.getItems().remove(getIndex());
					}
				});
			}

			@Override
			protected void updateItem(Color item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					setGraphic(rectangle);
					rectangle.setFill(item);
				}
			}
		});
		SplitPane splitPane = SplitPanes.horizontal(
				new VBox(4, stringInput, stringList),
				new VBox(4, colorEditor, colorList));

		stringList.getItems().add(Color.RED.toString());
		stringList.getItems().add(Color.GREEN.toString());
		stringList.getItems().add(Color.BLUE.toString());

		colorList.getItems().add(Color.CYAN);
		colorList.getItems().add(Color.YELLOW);
		colorList.getItems().add(Color.MAGENTA);

		BidirectionalContentBindings.bind(
				colorList.getItems(),
				stringList.getItems(),
				converter.inverted());

		stringList.getSelectionModel().selectedIndexProperty().addListener(
				(_, _, selected) -> colorList.getSelectionModel().select(selected.intValue()));

		colorList.getSelectionModel().selectedIndexProperty().addListener(
				(_, _, selected) -> stringList.getSelectionModel().select(selected.intValue()));

		return splitPane;
	}

	private Node buildSet() {

		Converter<String, Color> converter = Converter.passingNull(
				string -> {
					try {
						return Color.web(string);
					} catch (Exception e) {
						return Color.GREY;
					}
				},
				Object::toString);

		ListView<String> stringList = new ListView<>();
		TextField stringInput = new TextField();
		stringInput.setOnAction(_ -> {
			stringList.getItems().add(stringInput.getText());
			stringInput.setText(null);
		});
		stringList.setCellFactory(_ -> new ListCell<>() {
			private final Rectangle rectangle = new Rectangle(16, 16);

			{
				setOnMouseClicked(event -> {
					if (event.getClickCount() == 2) {
						stringList.getItems().remove(getIndex());
					}
				});
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item);
				}
			}
		});

		ListView<Color> colorList = new ListView<>();
		ColorEditor colorEditor = new ColorEditor();
		colorEditor.valueProperty().subscribe(color -> colorList.getItems().add(color));
		colorList.setCellFactory(_ -> new ListCell<>() {
			private final Rectangle rectangle = new Rectangle(16, 16);

			{
				setOnMouseClicked(event -> {
					if (event.getClickCount() == 2) {
						colorList.getItems().remove(getIndex());
					}
				});
			}

			@Override
			protected void updateItem(Color item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					setGraphic(rectangle);
					rectangle.setFill(item);
				}
			}
		});
		SplitPane splitPane = SplitPanes.horizontal(
				new VBox(4, stringInput, stringList),
				new VBox(4, colorEditor, colorList));

		stringList.getItems().add(Color.RED.toString());
		stringList.getItems().add(Color.GREEN.toString());
		stringList.getItems().add(Color.BLUE.toString());

		colorList.getItems().add(Color.CYAN);
		colorList.getItems().add(Color.YELLOW);
		colorList.getItems().add(Color.MAGENTA);

		BidirectionalContentBindings.bind(
				colorList.getItems(),
				stringList.getItems(),
				converter.inverted());

		stringList.getSelectionModel().selectedIndexProperty().addListener(
				(_, _, selected) -> colorList.getSelectionModel().select(selected.intValue()));

		colorList.getSelectionModel().selectedIndexProperty().addListener(
				(_, _, selected) -> stringList.getSelectionModel().select(selected.intValue()));

		return splitPane;
	}

	private Node buildMap() {

		//noinspection Convert2MethodRef
		Converter<String, String> keyConverter = Converter.passingNull(
				color -> "%s.circle".formatted(color),
				circle -> circle.substring(0, circle.indexOf(".")));
		Converter<Color, Circle> valueConverter = Converter.passingNull(
				color -> new Circle(12, color),
				circle -> (Color) circle.getFill());

		MapView<String, Color> left = new MapView<>();
		MapView<String, Circle> right = new MapView<>();

		TextField stringInput = new TextField();
		ColorEditor colorEditor = new ColorEditor();
		stringInput.setOnAction(_ -> {
			String input = stringInput.getText();
			Color color = colorEditor.getValue();
			left.getMap().put(input, color);
			stringInput.setText(null);
		});

		SplitPane splitPane = SplitPanes.horizontal(
				new VBox(4, stringInput, left),
				new VBox(4, colorEditor, right));

		left.getMap().put("Red", Color.RED);
		left.getMap().put("Green", Color.GREEN);
		left.getMap().put("Blue", Color.BLUE);

		right.getMap().put("Cyan.circle", new Circle(12, Color.CYAN));
		right.getMap().put("Yellow.circle", new Circle(12, Color.YELLOW));
		right.getMap().put("Magenta.circle", new Circle(12, Color.MAGENTA));

		BidirectionalContentBindings.bind(
				left.getMap(),
				right.getMap(),
				keyConverter,
				valueConverter);

		return splitPane;
	}
}
