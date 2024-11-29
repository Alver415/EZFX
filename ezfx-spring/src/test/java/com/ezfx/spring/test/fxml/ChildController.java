package com.ezfx.spring.test.fxml;

import com.ezfx.spring.annotations.FXMLPrototype;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FXMLPrototype(location = "AlternateView.fxml")
public class ChildController {

	private static final Logger log = LoggerFactory.getLogger(ChildController.class);

	@FXML
	private Button button;

	private final TestBean testBean;

	public ChildController(TestBean testBean) {
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
