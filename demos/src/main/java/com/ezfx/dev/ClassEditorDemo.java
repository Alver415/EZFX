package com.ezfx.dev;

import com.ezfx.app.EZFXApplication;
import com.ezfx.controls.editor.introspective.ClassHierarchyEditor;
import com.ezfx.controls.icons.SVGs;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClassEditorDemo extends EZFXApplication {

	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(SVGs.GEAR.image(32));
		stage.setScene(buildScene());
		stage.setWidth(600);
		stage.setHeight(400);
		stage.centerOnScreen();
		stage.show();
	}

	private static Scene buildScene() {
		ClassHierarchyEditor<Parent> editor = new ClassHierarchyEditor<>();
		Parent parent = new Parent();
		parent.setName("Daniel");
		parent.nameProperty().subscribe(name -> System.out.println("parent.name: " + name));
		Child child = new Child();
		child.setName("Alex");
		child.nameProperty().subscribe(name -> System.out.println("child.name: " + name));
		child.anotherProperty().subscribe(another -> System.out.println("child.another: " + another));

		Button parentButton = new Button("Parent");
		parentButton.setOnAction(_ -> editor.setValue(parent));
		Button childButton = new Button("Child");
		childButton.setOnAction(_ -> editor.setValue(child));

		return new Scene(new SplitPane(new ScrollPane(editor), new StackPane(new VBox(parentButton, childButton))));
	}

	public static class Parent {
		private final Property<String> name = new SimpleObjectProperty<>(this, "name");

		public Property<String> nameProperty() {
			return this.name;
		}

		public String getName() {
			return this.nameProperty().getValue();
		}

		public void setName(String value) {
			this.nameProperty().setValue(value);
		}
	}
	public static class Child extends Parent {
		private final Property<String> another = new SimpleObjectProperty<>(this, "another");

		public Property<String> anotherProperty() {
			return this.another;
		}

		public String getAnother() {
			return this.anotherProperty().getValue();
		}

		public void setAnother(String value) {
			this.anotherProperty().setValue(value);
		}
	}
}
