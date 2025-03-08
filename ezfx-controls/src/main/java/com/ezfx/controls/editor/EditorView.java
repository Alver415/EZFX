package com.ezfx.controls.editor;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

public class EditorView<T, E extends Editor<T>> extends Control {

	public EditorView(String name) {
		this(new SimpleStringProperty(name));
	}

	public EditorView(Property<String> name) {
		this(name, null);
	}

	public EditorView(E editor) {
		this(editor.valueProperty().getName(), editor);
	}

	public EditorView(String name, E editor) {
		this(new SimpleStringProperty(name), editor);
	}

	public EditorView(Property<String> name, E editor) {
		nameProperty().bind(name);
		setEditor(editor);
		setFocusTraversable(false);
		ObservableValue<ObservableList<Action>> obs = editorProperty().map(e -> e instanceof EditorBase<?> eb ? eb.actionsProperty() : null);
		actionsProperty().bind(obs);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin<>(this);
	}

	private final StringProperty name = new SimpleStringProperty(this, "name");

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return this.nameProperty().get();
	}

	public void setName(String value) {
		this.nameProperty().set(value);
	}

	private final ObjectProperty<E> editor = new SimpleObjectProperty<>(this, "editor");

	public ObjectProperty<E> editorProperty() {
		return this.editor;
	}

	public E getEditor() {
		return this.editorProperty().get();
	}

	public void setEditor(E value) {
		this.editorProperty().set(value);
	}

	private final ListProperty<Action> actions = new SimpleListProperty<>(this, "actions", FXCollections.observableArrayList());

	public ListProperty<Action> actionsProperty() {
		return this.actions;
	}

	public ObservableList<Action> getActions() {
		return this.actionsProperty().get();
	}

	public void setActions(ObservableList<Action> value) {
		this.actionsProperty().set(value);
	}


	public static class DefaultSkin<T, C extends Editor<T>> extends SkinBase<EditorView<T, C>> {

		//TODO: Implement style to represent whether null value.
		private static final PseudoClass IS_NULL = PseudoClass.getPseudoClass("is-null");
		private static final PseudoClass NON_NULL = PseudoClass.getPseudoClass("non-null");

		public DefaultSkin(EditorView<T, C> wrapper) {
			super(wrapper);

			// Components
			BorderPane root = new BorderPane();
			BorderPane top = new BorderPane();
			Label title = new Label();
			HBox actionsBar = new HBox();
			HBox center = new HBox();
			getChildren().setAll(root);

			// Structure
			top.setLeft(title);
			top.setRight(actionsBar);
			root.setTop(top);
			root.setCenter(center);

			// Style
			title.getStyleClass().add("title");
			actionsBar.getStyleClass().add("actions");
			top.getStyleClass().add("header");

			// TODO: Don't hardcode styling (Ideally move to css, but without impacting performance)
			root.setPadding(new Insets(4));
			root.setBackground(Background.fill(Color.BLACK.interpolate(Color.TRANSPARENT, 0.95)));

			// Bindings
			title.textProperty().bind(wrapper.nameProperty());
			wrapper.actionsProperty()
					.map(ActionUtils::createContextMenu)
					.subscribe(wrapper::setContextMenu);
			wrapper.editorProperty().map(Editor::getNode).subscribe(control -> {
				HBox.setHgrow(control, Priority.ALWAYS);
				center.getChildren().setAll(control);
			});


		}

	}

}
