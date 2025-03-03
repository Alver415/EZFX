package com.ezfx.controls.info;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.Image;

public interface INodeInfoHelper {
	Image icon(Node node);
	String typeName(Node node);

	ObservableValue<String> nodeId(Node node);
	ObservableValue<String> styleClass(Node node);

	ObservableValue<String> info(Node node);
}
