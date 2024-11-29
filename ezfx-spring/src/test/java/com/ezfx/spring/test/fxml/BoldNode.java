package com.ezfx.spring.test.fxml;

import com.ezfx.spring.annotations.FXMLPrototype;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLPrototype
public class BoldNode extends BorderPane {

	private static final Logger log = LoggerFactory.getLogger(BoldNode.class);

	private final StringProperty textProperty = new SimpleStringProperty();

	public StringProperty textProperty() {
		return textProperty;
	}

	public String getText() {
		return textProperty.get();
	}

	public void setText(String text) {
		this.textProperty.set(text);
	}

	@FXML
	private Button button;

	private TestBean testBean;

	public BoldNode() {
	}

	@Autowired
	public BoldNode(TestBean testBean) {
		this.testBean = testBean;
		log.info("constructor: " + this);
	}

	@FXML
	public void initialize() {
		log.info("initialize: " + button.getText());
		button.setFont(Font.font("sans-serif", FontWeight.BOLD, 12));
	}

	public void log(ActionEvent actionEvent) {
		textProperty.set(textProperty.get() + "-");
		button.setText(textProperty.get());
		log.info(String.valueOf(this));
	}
}
