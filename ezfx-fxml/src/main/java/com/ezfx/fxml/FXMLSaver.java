package com.ezfx.fxml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;

public class FXMLSaver {

	static XmlMapper mapper = new XmlMapper();

	static {
		SimpleModule fxmlModule = new SimpleModule();
		fxmlModule.addSerializer(new FXMLSerializer());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.registerModule(fxmlModule);
	}

	public void save(File file, Node node) {
		try {
			mapper.writeValue(file, node);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String serialize(Node node) {
		try {
			return mapper.writeValueAsString(node);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
