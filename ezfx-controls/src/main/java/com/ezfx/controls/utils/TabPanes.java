package com.ezfx.controls.utils;


import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

public interface TabPanes {

	static TabPane create(Tab... tabs) {
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		tabPane.getTabs().setAll(tabs);
		return tabPane;
	}
}
