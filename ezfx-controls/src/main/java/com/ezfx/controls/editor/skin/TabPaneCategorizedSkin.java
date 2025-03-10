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

		// Style change is a workaround for bug that cause the tab open/close animation to stop early, resulting
		// in tabs that stay a few pixels wide until a layout is requested at some point.
		// Possibly related, but says its resolved and I still see the bug occurring:
		// https://stackoverflow.com/questions/47616221/javafx-tabpane-tabs-dont-update-position
		tabPane.setStyle("""
				-fx-close-tab-animation: NONE;
				-fx-open-tab-animation: NONE;
				""");

		control.categorizedEditorsProperty()
				.map(categorizedEditors -> categorizedEditors.entrySet().stream().map(this::getTab).toList())
				.orElse(List.of())
				.subscribe((oldTabs, newTabs) -> {
					// Try to stay on the closes relevant tab.
					int i = Math.min(tabPane.getSelectionModel().getSelectedIndex(), newTabs.size() - 1);
					while (i >= 0 && i < newTabs.size() && i < oldTabs.size()){
						Tab oldTab = oldTabs.get(i);
						Tab newTab = newTabs.get(i);
						if (oldTab == newTab){
							break;
						}
						i--;
					}
					tabPane.getTabs().setAll(newTabs);
					tabPane.getSelectionModel().select(i);
				});
		setChildren(tabPane);
	}

	private Tab getTab(Map.Entry<Category, ObservableList<Editor<?>>> entry) {
		Category category = entry.getKey();
		ObservableList<Editor<?>> editors = entry.getValue();
		return tabMap.computeIfAbsent(category, _ -> {
			Tab newTab = new Tab(category.title());
			VBox content = new VBox(4);
			content.getChildren().setAll(editors.stream().map(EditorView::new).toList());
			content.setPadding(new Insets(8));
			ScrollPane scrollPane = new ScrollPane(content);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);
			newTab.setContent(scrollPane);
			newTab.setClosable(false);
			return newTab;
		});
	}
}
