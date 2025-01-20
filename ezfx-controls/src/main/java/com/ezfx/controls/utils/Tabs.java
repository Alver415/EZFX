package com.ezfx.controls.utils;


import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.List;
import java.util.Map;

public interface Tabs {

	static Tab create(String name, Node content) {
		Tab tab = new Tab(name);
		tab.setContent(content);
		return tab;
	}

	static Tab create(ObservableValue<String> name, Node content) {
		Tab tab = new Tab();
		tab.textProperty().bind(name);
		tab.setContent(content);
		return tab;
	}

	static List<Tab> list(Map<String, Node> map){
		return map.entrySet().stream().map(e -> create(e.getKey(), e.getValue())).toList();
	}
}
