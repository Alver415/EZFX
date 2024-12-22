package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.standard.BooleanEditor;
import com.ezfx.controls.icons.Icons;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

public class EditorDialog<T> extends Dialog<T> {

	public EditorDialog(Property<T> property, Editor<T> editor) {
		T originalValue = property.getValue();
		initModality(Modality.NONE);
		DialogPane dialogPane = getDialogPane();
		setResizable(true);
		setContentText("Content Text");
		ImageView graphic = new ImageView();
		graphic.setFitWidth(32);
		graphic.setFitHeight(32);
		BooleanEditor autoApply = new BooleanEditor();
		autoApply.setValue(true);
		graphic.setOnMouseClicked(a -> autoApply.setValue(!autoApply.getValue()));
		graphic.imageProperty().bind(autoApply.valueProperty().map(isAutoApply -> isAutoApply ? Icons.LOCKED : Icons.UNLOCKED));
		setGraphic(graphic);
		setHeaderText("Header Text");
		setTitle("Title");

		autoApply.valueProperty().subscribe(isAutoApply -> {
			if (isAutoApply) property.bindBidirectional(editor.valueProperty());
			else {
				property.unbindBidirectional(editor.valueProperty());
				property.setValue(originalValue);
			}
		});

		BorderPane borderPane = new BorderPane();
		BorderPane top = new BorderPane();
		borderPane.setTop(top);
		dialogPane.setContent(borderPane);
		borderPane.setCenter(editor);

		ButtonType apply = new ButtonType("Apply", ButtonData.LEFT);
		ButtonType reset = new ButtonType("Reset", ButtonData.LEFT);
		dialogPane.getButtonTypes().setAll(OK, apply, reset, CANCEL);
		Button applyButton = (Button) dialogPane.lookupButton(apply);
		applyButton.addEventFilter(ActionEvent.ACTION, event -> {
			property.setValue(editor.getValue());
			event.consume();
		});
		Button previousButton = (Button) dialogPane.lookupButton(reset);
		previousButton.addEventFilter(ActionEvent.ACTION, event -> {
			property.setValue(originalValue);
			event.consume();
		});


		setResultConverter(buttonType -> {
			if (buttonType == null || buttonType == CANCEL) {
				return originalValue;
			}
			return editor.getValue();
		});
		resultProperty().addListener((_, _, newValue) -> property.setValue(newValue));
	}

}
