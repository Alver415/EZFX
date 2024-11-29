package com.ezfx.controls.editor;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyValueRepositoryTest {

	private static final Logger log = LoggerFactory.getLogger(PropertyValueRepositoryTest.class);

	@Test
	public void test(){
		PropertyValueRepository repo = new PropertyValueRepository();

		repo.register(Border.class, Border.EMPTY);

		ObservableList<Border> borders = repo.get(Border.class);
		log.info(String.valueOf(borders));
		borders.addListener((ListChangeListener<? super Border>) change -> {
			log.info("Change");
		});

		repo.register(Border.class, Border.stroke(Color.RED));

		log.info(String.valueOf(borders));

	}
}
