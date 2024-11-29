package com.ezfx.spring.test.fxml;

import com.ezfx.spring.annotations.FXMLComponent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FXMLComponent
public class ApplicationController {

	private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

	@FXML
	private Button button;

	private final TestBean testBean;

	public ApplicationController(TestBean testBean) {
		this.testBean = testBean;
		log.info("constructor: " + this);
	}

	@FXML
	public void initialize() {
		log.info("initialize: ");
	}

	public void log(ActionEvent actionEvent) {
		button.setText(actionEvent.toString());
		log.info(String.valueOf(this));
	}
}
