package com.ezfx.polyglot.test;

import com.ezfx.polyglot.PolyglotDialog;
import javafx.application.Application;
import javafx.stage.Stage;
import org.graalvm.polyglot.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PolyglotFXTest extends Application {

	private static final Logger log = LoggerFactory.getLogger(PolyglotFXTest.class);

	public static class Launcher {
		public static void main(String... args) {
			Application.launch(PolyglotFXTest.class);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Context context = Context.newBuilder().build();

		PolyglotDialog<Integer> dialog = new PolyglotDialog<>(context, Integer.class);
		Optional<Integer> result = dialog.showAndWait();

		log.info(result.toString());
	}
}
