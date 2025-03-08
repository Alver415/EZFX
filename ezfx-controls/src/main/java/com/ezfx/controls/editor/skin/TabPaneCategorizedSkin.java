package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.*;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabPaneCategorizedSkin<E extends Control & Editor<T> & CategorizedMultiEditor<T>, T> extends EditorSkinBase<E, T> {

	private final TabPane tabPane = new TabPane();
	private final Map<Category, Tab> tabMap = new HashMap<>();

	public TabPaneCategorizedSkin(E control) {
		super(control);

		setChildren(tabPane);

		control.categorizedEditorsProperty()
				.map(categorizedEditors -> categorizedEditors.entrySet().stream().map(this::buildTab).toList())
				.orElse(List.of())
				.subscribe(tabs -> {
					tabPane.getTabs().clear();
					tabPane.getTabs().setAll(tabs);
				});
	}

	private Tab buildTab(Map.Entry<Category, ObservableList<Editor<?>>> entry) {
		Category category = entry.getKey();
		ObservableList<Editor<?>> editors = entry.getValue();

		Tab tab = tabMap.computeIfAbsent(category, this::buildTab);
		VBox content = new VBox(4);
		content.setPadding(new Insets(8));
		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		tab.setContent(scrollPane);

		content.getChildren().setAll(editors.stream().map(EditorView::new).toList());
		return tab;
	}

	private Tab buildTab(Category category) {
		Tab newTab = new Tab(category.title());
		newTab.setClosable(false);
		return newTab;
	}

}
