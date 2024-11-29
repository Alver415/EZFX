package com.ezfx.spring.test.fxml;

import com.ezfx.spring.SpringFX;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;

public class TestJavaFXApplication extends Application {

	public static class Launcher {
		public static void main(String... args) {
			Application.launch(TestJavaFXApplication.class);
		}
	}

	private ApplicationContext context;
	private SpringFX springFX;

	@Override
	public void init() {
		ApplicationContextInitializer<GenericApplicationContext> initializer = applicationContext -> {
			applicationContext.registerBean(TestJavaFXApplication.class, () -> this);
		};

		context = new SpringApplicationBuilder(TestSpringApplication.class)
				.initializers(initializer)
				.run();

		springFX = context.getBean(SpringFX.class);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = springFX.loadView(ApplicationController.class);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
