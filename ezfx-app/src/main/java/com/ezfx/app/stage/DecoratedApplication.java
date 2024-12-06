package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.impl.standard.SelectionEditor;
import com.ezfx.controls.icons.Icons;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DecoratedApplication extends Application {
	public static void main(String... args){
		Application.launch(DecoratedApplication.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage = new DecoratedStage();
		VBox root = new VBox(new Label("Test"), new Button("This"));
		Scene scene = new Scene(root);

		stage.getIcons().add(Resources.image(Icons.class, "unlocked.png"));
		stage.setTitle("Title");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}


	public void start1(Stage stage) throws Exception {

		ObservableList<StageStyle> styles = FXCollections.observableArrayList(StageStyle.values());
		SelectionEditor<StageStyle> styleEditor = new SelectionEditor<>(styles);
		Button createStageButton = new Button("CREATE STAGE");
		createStageButton.setOnAction(_ -> {
			StageStyle style = styleEditor.getValue();
			Stage newStage = new Stage(style);
			Label label = new Label(style.name());
			label.setStyle("-fx-background-color:transparent");
			Scene scene = new Scene(label);
			scene.setFill(Color.TRANSPARENT);
			newStage.setScene(scene);
			newStage.show();
		});
		VBox root = new VBox(styleEditor, createStageButton);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}
}
