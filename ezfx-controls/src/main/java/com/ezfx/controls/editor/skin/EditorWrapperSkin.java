package com.ezfx.controls.editor.skin;

import com.ezfx.base.utils.Backgrounds;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorWrapper;
import com.ezfx.controls.utils.ActionNodes;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.controlsfx.control.action.ActionUtils;

import static org.controlsfx.control.action.ActionUtils.ActionTextBehavior.HIDE;

public class EditorWrapperSkin<T, C extends Editor<T>> extends SkinBase<EditorWrapper<T, C>> {

	public EditorWrapperSkin(EditorWrapper<T, C> wrapper) {
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

		// TODO: Don't hardcode styling
		// Ideally move to css, but without impacting performance.
		root.setPadding(new Insets(4));
		root.setBackground(Background.fill(Color.BLACK.interpolate(Color.TRANSPARENT, 0.95)));

		// Bindings
		title.textProperty().bind(wrapper.nameProperty());
		wrapper.actionsProperty()
				.map(actions -> ActionNodes.createButtonBar(actions, HIDE))
				.subscribe(top::setRight);
		wrapper.editorProperty()
				.subscribe(control -> {
					HBox.setHgrow(control, Priority.ALWAYS);
					center.getChildren().setAll(control);
				});

	}

}