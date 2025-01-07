package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.CategorizedMultiEditor;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorView;
import javafx.collections.FXCollections;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

import java.util.List;
import java.util.stream.Collectors;

public class TitledPaneCategorizedSkin<E extends Editor<T> & CategorizedMultiEditor<T>, T> extends EditorSkin<E, T> {

	private final Accordion accordion = new Accordion();

	public TitledPaneCategorizedSkin(E control) {
		super(control);
		setChildren(accordion);

		control.categorizedEditorsProperty()
				.map(categorizedEditors -> categorizedEditors.entrySet().stream()
						.map(entry -> new TitledPane(entry.getKey().title(), new ListView<>(entry.getValue().stream()
								.map(EditorView::new)
								.collect(Collectors.toCollection(FXCollections::observableArrayList))))).toList())
				.orElse(List.of())
				.subscribe(content -> {
					accordion.getPanes().setAll(content);
					content.stream().findFirst().ifPresent(accordion::setExpandedPane);
				});
	}

}
