package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.controlsfx.control.action.ActionUtils.ActionTextBehavior.HIDE;

public class EditorWrapperSkin<T, C extends Editor<T>> extends SkinBase<EditorWrapper<T, C>> {

	public static final String STYLE_CLASS = "editor-wrapper";
	public static final String STYLE_SHEET = Objects.requireNonNull(
			EditorWrapper.class.getResource("EditorWrapper.css")).toExternalForm();

	public EditorWrapperSkin(EditorWrapper<T, C> wrapper) {
		super(wrapper);
		wrapper.getStyleClass().add(STYLE_CLASS);
		wrapper.getStylesheets().add(STYLE_SHEET);

		BorderPane borderPane = new BorderPane();

		Label title = new Label();
		title.getStyleClass().add("title");
		title.textProperty().bind(wrapper.nameProperty());

		SegmentedButton actionButtons = ActionUtils.createSegmentedButton(HIDE, wrapper.getActions());
		actionButtons.getStyleClass().add("actions");

		BorderPane top = new BorderPane();
		top.getStyleClass().add("header");
		top.setLeft(title);
		top.setRight(actionButtons);
		borderPane.setTop(top);

		HBox center = new HBox();
		wrapper.editorProperty()
				.subscribe(control -> {
					HBox.setHgrow(control, Priority.ALWAYS);
					center.getChildren().setAll(control);
				});
		borderPane.setCenter(center);

		getChildren().setAll(borderPane);
	}

	private Node buildActionControl(Action action) {
		return ActionUtils.createButton(action, HIDE);
	}

	private static <A, B> List<B> mapList(List<A> actions, Function<A, B> function) {
		return actions.stream().map(function).toList();
	}

}