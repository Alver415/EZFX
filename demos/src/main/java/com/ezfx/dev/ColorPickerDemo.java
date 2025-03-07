package com.ezfx.dev;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.stage.DecoratedStage;
import com.ezfx.base.utils.Colors;
import com.ezfx.base.utils.Converter;
import com.ezfx.controls.editor.impl.javafx.ColorEditor;
import com.ezfx.controls.icons.SVGs;
import com.ezfx.controls.utils.SplitPanes;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class ColorPickerDemo extends EZFXApplication {

	@Override
	public void start(Stage primaryStage) throws Exception {
		setTitle("Color Picker");
		Stage stage = new DecoratedStage();
		stage.getIcons().add(SVGs.GEAR.image(32));
		stage.setScene(buildScene());
		stage.setWidth(1600);
		stage.setHeight(900);
		stage.centerOnScreen();
		stage.show();
	}

	private static Scene buildScene() {
		ColorEditor colorEditor = new ColorEditor();
		StackPane canvas = new StackPane();
		bindBidirectional(colorEditor.valueProperty(), canvas.backgroundProperty(),
				Converter.of(Background::fill, background -> {
					try {
						return (Color) background.getFills().getFirst().getFill();
					} catch (Exception e) {
						return null;
					}
				}));


		BorderPane right = new BorderPane(canvas);
		BorderPane left = new BorderPane(colorEditor);
		SplitPane root = SplitPanes.horizontal(left, right);
		root.setBackground(Background.fill(Colors.withAlpha(Color.WHITE, 0.5)));
		return new Scene(root);
	}
}
