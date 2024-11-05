package com.ezfx.test;

import com.ezfx.base.utils.Colors;
import com.fsfx.control.editor.*;
import com.fsfx.control.explorer.SceneExplorer;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class TestApplication extends Application {

	public static class Launcher {
		public static void main(String... args) {
			Application.launch(TestApplication.class);
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		Example example = new Example();

		Label left = new Label("Test");
		left.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

		Editor<Insets, ?> insetsEditor = new Editor<>("Insets", new InsetsEditor(example.insetsProperty()));
		Editor<CornerRadii, ?> cornerRadiiEditor = new Editor<>("CornerRadii", new CornerRadiiEditor(example.cornerRadiiProperty()));
		VBox right = new VBox(insetsEditor, cornerRadiiEditor);

		SplitPane splitPane = new SplitPane(left, right);
		Scene scene = new Scene(splitPane);
		stage.setScene(scene);
		stage.show();

		example.insetsProperty().subscribe(insets -> {
			left.setBackground(new Background(new BackgroundFill(Color.RED, example.getCornerRadii(), insets)));
		});
		example.cornerRadiiProperty().subscribe(cornerRadii -> {
			left.setBackground(new Background(new BackgroundFill(Color.RED, cornerRadii, example.getInsets())));
		});

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
