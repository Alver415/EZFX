package com.ezfx.apps;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.base.utils.Colors;
import com.ezfx.controls.editor.code.CodeEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.impl.standard.FileSelectionEditor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.icons.SVGPaths;
import com.ezfx.controls.icons.SVGs;
import com.ezfx.controls.utils.SplitPanes;
import com.ezfx.controls.viewport.Viewport;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;

public class SVGParserApplication extends EZFXApplication {

	@Override
	public void init() throws Exception {
		super.init();
		setTitle("SVG Parser");
		setIcon(SVGs.GEAR.image(16));
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Stage stage = new DecoratedStage();
		stage.setScene(buildScene());
		stage.setWidth(1600);
		stage.setHeight(900);
		stage.centerOnScreen();
		stage.show();
	}

	private static Scene buildScene() {

		FileSelectionEditor fileSelectionEditor = new FileSelectionEditor();
		fileSelectionEditor.setPadding(new Insets(4));


		StackPane canvas = new StackPane();
		canvas.setBackground(Background.fill(Color.WHITE));

		DoubleEditor widthEditor = new DoubleEditor();
		widthEditor.setMin(0d);
		widthEditor.setMax(Math.pow(2, 9));
		DoubleEditor heightEditor = new DoubleEditor();
		heightEditor.setMin(0d);
		heightEditor.setMax(Math.pow(2, 9));
		canvas.prefWidthProperty().bind(widthEditor.valueProperty());
		canvas.minWidthProperty().bind(widthEditor.valueProperty());
		canvas.maxWidthProperty().bind(widthEditor.valueProperty());
		canvas.prefHeightProperty().bind(heightEditor.valueProperty());
		canvas.minHeightProperty().bind(heightEditor.valueProperty());
		canvas.maxHeightProperty().bind(heightEditor.valueProperty());

		Viewport viewport = new Viewport();
		StackPane wrapper = new StackPane(canvas);
		wrapper.setBackground(Background.fill(Color.TRANSPARENT));
		viewport.setContent(wrapper);

		double defaultSize = Math.pow(2, 9);
		widthEditor.setValue(defaultSize);
		heightEditor.setValue(defaultSize);

		StringEditor svgEditor = new CodeEditor();
		svgEditor.valueProperty()
				.map(string -> {
					try {
						return SVGPaths.parse(string);
					} catch (Exception e) {
						return new Group(new Text(e.getMessage()));
					}
				})
				.map(svg -> svg != null ? svg : new Group(new Text("ERROR")))
				.subscribe(svg -> {
					if (svg == null) return;
					canvas.getChildren().setAll(svg);
				});

		fileSelectionEditor.valueProperty()
				.map(file -> {
					try {
						return Files.readString(file.toPath());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}).subscribe(svgEditor::setValue);

		BorderPane right = new BorderPane(new StackPane(viewport));
		right.setTop(new HBox(widthEditor, heightEditor));
		BorderPane left = new BorderPane(svgEditor);
		left.setTop(fileSelectionEditor);
		SplitPane root = SplitPanes.horizontal(left, right);
		root.setBackground(Background.fill(Colors.withAlpha(Color.WHITE, 0.5)));
		return new Scene(root);
	}
}
