package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorView;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.controlsfx.control.action.ActionUtils;

public class EditorViewSkin<T, C extends Editor<T>> extends SkinBase<EditorView<T, C>> {

	//TODO: Implement style to represent whether null value.
	private static final PseudoClass IS_NULL = PseudoClass.getPseudoClass("is-null");
	private static final PseudoClass NON_NULL = PseudoClass.getPseudoClass("non-null");

	public EditorViewSkin(EditorView<T, C> wrapper) {
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
		wrapper.editorProperty()
				.subscribe(control -> {
					HBox.setHgrow(control, Priority.ALWAYS);
					center.getChildren().setAll(control);
				});


	}

}