package com.ezfx.controls.editor.impl.javafx;

import com.ezfx.controls.editor.impl.standard.SelectionEditor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.control.Skin;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Arrays;

import static com.ezfx.base.utils.EZFX.runLaterFX;

public class BlendModeEditor extends SelectionEditor<BlendMode> {

	private static final ObservableList<BlendMode> BLEND_MODE_VALUES =
			FXCollections.observableList(Arrays.asList(BlendMode.values()));

	public BlendModeEditor() {
		this(BLEND_MODE_VALUES);
	}

	public BlendModeEditor(ObservableList<BlendMode> options) {
		super(options);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	public static class DefaultSkin extends SelectionEditor.DefaultSkin<BlendMode> {

		public DefaultSkin(BlendModeEditor editor) {
			super(editor);
			editor.setCellFactory(_ -> new BlendModeListCell());
			editor.setButtonCell(new BlendModeListCell());
		}

		private static String enumCaseToNormalCase(String enumName) {
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

		private static class BlendModeListCell extends ListCell<BlendMode> {
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
		}
	}

}
