package com.ezfx.test;

import com.ezfx.base.utils.Colors;
import com.fsfx.control.editor.EditorInfo;
import com.fsfx.control.editor.ListEditor;
import com.fsfx.control.explorer.SceneExplorer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class ListEditorTest extends Application {

	public static class Launcher {
		public static void main(String... args) {
			Application.launch(ListEditorTest.class);
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		Example example = new Example();
		example.getList().addAll("Test A","Test B","Test C");

		Label left = new Label("Test");
		left.textProperty().bind(Bindings.createStringBinding(
				() -> String.join(",\n", example.getList()),
				example.listProperty()));
//		left.backgroundProperty().bind(Bindings.createObjectBinding(
//				() -> new Background(example.getBackgroundFills(), List.of()),
//				example.backgroundFillsProperty()));

		ListEditor<String> listEditor = new ListEditor<>(example.listProperty());
		ListEditor<BackgroundFill> backgroundFillsEditor = new ListEditor<>(example.backgroundFillsProperty());
		backgroundFillsEditor.setCreateElementHook(() -> new BackgroundFill(Colors.random(), CornerRadii.EMPTY, Insets.EMPTY));

		VBox right = new VBox(new ScrollPane(listEditor), new ScrollPane(backgroundFillsEditor));

		BorderPane borderPane = new BorderPane();
		borderPane.setLeft(left);
		borderPane.setRight(right);
		Scene scene = new Scene(borderPane);
		stage.setScene(scene);
		stage.show();


		SceneExplorer.stage(scene);

	}

	public static class Example {

		private final StringProperty name = new SimpleStringProperty(this, "name");

		@EditorInfo(displayName = "Name", order = 0)
		public StringProperty nameProperty() {
			return this.name;
		}

		public String getName() {
			return this.nameProperty().get();
		}

		public void setName(String value) {
			this.nameProperty().set(value);
		}

		private final ListProperty<String> list = new SimpleListProperty<>(this, "list", FXCollections.observableArrayList());

		public ListProperty<String> listProperty() {
			return this.list;
		}

		public ObservableList<String> getList() {
			return this.listProperty().get();
		}

		public void setList(ObservableList<String> value) {
			this.listProperty().set(value);
		}

		private final ListProperty<BackgroundFill> backgroundFills = new SimpleListProperty<>(this, "backgroundFills", FXCollections.observableArrayList());

		public ListProperty<BackgroundFill> backgroundFillsProperty() {
			return this.backgroundFills;
		}

		public ObservableList<BackgroundFill> getBackgroundFills() {
			return this.backgroundFillsProperty().get();
		}

		public void setBackgroundFills(ObservableList<BackgroundFill> value) {
			this.backgroundFillsProperty().set(value);
		}

		private final ObjectProperty<Insets> insets = new SimpleObjectProperty<>(this, "insets", Insets.EMPTY);

		public ObjectProperty<Insets> insetsProperty() {
			return this.insets;
		}

		public Insets getInsets() {
			return this.insetsProperty().get();
		}

		public void setInsets(Insets value) {
			this.insetsProperty().set(value);
		}

		private final ObjectProperty<CornerRadii> cornerRadii = new SimpleObjectProperty<>(this, "cornerRadii", CornerRadii.EMPTY);

		public ObjectProperty<CornerRadii> cornerRadiiProperty() {
			return this.cornerRadii;
		}

		public CornerRadii getCornerRadii() {
			return this.cornerRadiiProperty().get();
		}

		public void setCornerRadii(CornerRadii value) {
			this.cornerRadiiProperty().set(value);
		}
	}
}
