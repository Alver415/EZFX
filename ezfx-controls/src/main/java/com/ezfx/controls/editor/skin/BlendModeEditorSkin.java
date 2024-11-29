package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.javafx.BlendModeEditor;
import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

public class BlendModeEditorSkin extends ComboBoxSelectionEditorSkin<BlendMode> {

	public BlendModeEditorSkin(BlendModeEditor editor) {
		super(editor);
		editor.setCellFactory(CELL_FACTORY);
		editor.setButtonCell(CELL_FACTORY.call(null));
	}

	public static String enumCaseToNormalCase(String enumName) {
		// Split by underscores, capitalize each word, and join with spaces
		String[] words = enumName.toLowerCase().split("_");
		StringBuilder formattedName = new StringBuilder();

		for (String word : words) {
			if (!word.isEmpty()) {
				formattedName.append(Character.toUpperCase(word.charAt(0)))
						.append(word.substring(1))
						.append(" ");
			}
		}

		return formattedName.toString().trim();
	}

	private static final Callback<ListView<BlendMode>, ListCell<BlendMode>> CELL_FACTORY = _ -> new ListCell<>() {
		@Override
		protected void updateItem(BlendMode item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(enumCaseToNormalCase(item.name()));
				setGraphic(generateExample(item));
			}
		}
	};

	private static Group generateExample(BlendMode item) {
		Color base = Color.RED;
		Color inverted = base.invert().deriveColor(15, 1, 1, 1);

		ColorInput topInput = new ColorInput(0, 0, 24, 16, base);
		Blend blend = new Blend();
		blend.setTopInput(topInput);
		blend.setMode(item);

		Circle circle = new Circle(8, 8, 12);
		circle.setFill(inverted);
		circle.setEffect(blend);

		return new Group(circle);
	}
}
