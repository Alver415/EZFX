package com.ezfx.controls.editor;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodeInfoTest {

	private static final Logger log = LoggerFactory.getLogger(NodeInfoTest.class);

	@BeforeAll
	public static void setup(){
		Platform.startup(() -> {});
	}
	@Test
	public void test() throws Exception {
		INodeInfoHelper nodeInfo = NodeInfoHelper.CACHING;

		Node button = new Button();
		button.setId("exampleId");
		button.getStyleClass().setAll("button", "example");
		ObservableValue<String> info = nodeInfo.info(button);
		info.subscribe(text -> System.out.println(text));

		button.setId("newId");
		button.getStyleClass().clear();
		button.getStyleClass().add("another");

		// Multiple calls don't create new objects, uses cached results from CachedProxy.
		assertTrue(info == nodeInfo.info(button));
		assertTrue(info == nodeInfo.info(button));
		assertTrue(info == nodeInfo.info(button));

		nodeInfo.info(new Button());
		nodeInfo.info(new Button());
		nodeInfo.info(new Button());
		nodeInfo.info(new Button());
		nodeInfo.info(new Button());
		nodeInfo.info(new Button());
		nodeInfo.info(new Button());
		nodeInfo.info(new Button());


		assertTrue(info == nodeInfo.info(button));
	}
}
