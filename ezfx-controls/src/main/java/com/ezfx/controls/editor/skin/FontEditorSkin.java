package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.impl.javafx.FontEditor;
import com.ezfx.controls.editor.impl.standard.DoubleEditor;
import com.ezfx.controls.editor.impl.standard.SelectionEditor;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class FontEditorSkin extends EditorSkin<FontEditor, Font> {

	private static final Logger log = LoggerFactory.getLogger(FontEditorSkin.class);

	private final StringProperty family = new SimpleStringProperty(this, "family", Font.getDefault().getFamily());
	private final StringProperty name = new SimpleStringProperty(this, "name", Font.getDefault().getName());
	private final DoubleProperty size = new SimpleDoubleProperty(this, "size", Font.getDefault().getSize());

	private final ListProperty<String> availableNames = new SimpleListProperty<>(this, "availableNames", FXCollections.observableArrayList());

	public FontEditorSkin(FontEditor control) {
		super(control);

		family.map(Font::getFontNames).orElse(List.of()).subscribe(family -> {
			availableNames.setAll(family);
			family.stream().findFirst().ifPresent(name::set);
		});
		control.property().subscribe(font -> {
			family.set(font.getFamily());
			name.set(font.getName());
			size.set(font.getSize());
		});

		List.of(name, size).forEach(property -> property.subscribe(_ ->{
					if (!property().isBound()) property().setValue(new Font(name.get(), size.get()));
				}));

		SelectionEditor<String> familyEditor = new SelectionEditor<>(family, Font.getFamilies());
		SelectionEditor<String> nameEditor = new SelectionEditor<>(name, availableNames);
		DoubleEditor sizeEditor = new DoubleEditor(size);

		familyEditor.setCellFactory(FAMILY_CELL_FACTORY);
		nameEditor.setCellFactory(NAME_CELL_FACTORY);

		familyEditor.setButtonCell(FAMILY_CELL_FACTORY.call(null));
		nameEditor.setButtonCell(NAME_CELL_FACTORY.call(null));

		VBox familyWrapper = new VBox(new Label("Family"), familyEditor);
		VBox nameWrapper = new VBox(new Label("Name"), nameEditor);
		VBox sizeWrapper = new VBox(new Label("Size"), sizeEditor);

		HBox hBox = new HBox(4, familyWrapper, nameWrapper, sizeWrapper);
		getChildren().setAll(hBox);
	}

	private static final Callback<ListView<String>, ListCell<String>> NAME_CELL_FACTORY = buildFactory(name -> new Font(name, 12));
	private static final Callback<ListView<String>, ListCell<String>> FAMILY_CELL_FACTORY = buildFactory(family -> Font.font(family, 12));

	private static Callback<ListView<String>, ListCell<String>> buildFactory(Function<String, Font> fontConstructor) {
		return _ -> new ListCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setFont(fontConstructor.apply(item));
					setText(item);
				}
			}
		};
	}

}