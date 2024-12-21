package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.CategorizedMultiEditor;
import com.ezfx.controls.editor.Category;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class TitledPaneCategorizedSkin<E extends Editor<T> & CategorizedMultiEditor<T>, T> extends EditorSkin<E, T> {

	private final VBox vBox = new VBox();

	public TitledPaneCategorizedSkin(E control) {
		super(control);
		setChildren(vBox);

		control.categorizedEditorsProperty()
				.map(categorizedEditors -> categorizedEditors.entrySet().stream().map(entry -> {
					Category category = entry.getKey();
					ObservableList<Editor<?>> editors = entry.getValue();

					VBox content = new VBox(4);
					TitledPane titledPane = new TitledPane(category.title(), content);
					for (Editor<?> editor : editors) {
						content.getChildren().add(new EditorWrapper<>(editor));
					}
					return titledPane;
				}).toList()).orElse(List.of())
				.subscribe(content -> vBox.getChildren().setAll(content));
	}

}
