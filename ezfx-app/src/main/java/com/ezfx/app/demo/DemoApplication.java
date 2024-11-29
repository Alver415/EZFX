package com.ezfx.app.demo;

import com.ezfx.base.io.IOConsole;
import com.ezfx.base.io.SystemIO;
import com.ezfx.controls.console.ConsoleView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.graalvm.polyglot.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.ezfx.base.utils.EZFX.runFX;
import static com.ezfx.base.utils.EZFX.runOnNewThread;
import static java.lang.Thread.sleep;

public class DemoApplication extends Application {

	private final List<IOConsole> consoles = new ArrayList<>();

	public static void main(String[] args) {
		//Replaces System in/out/err with read/write capable IOStreams.
		SystemIO.overrideSystemDefaults();
		Application.launch(DemoApplication.class, args);
	}

	private final TabPane tabPane = new TabPane();

	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(tabPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Demo Application");
		primaryStage.setWidth(600);
		primaryStage.setHeight(400);
		primaryStage.show();

		primaryStage.setOnCloseRequest(_ -> this.close());

		runOnNewThread(() -> {
			runFX(() -> {
				ColorPicker in = new ColorPicker(Color.BLUE);
				ColorPicker out = new ColorPicker(Color.GREEN);
				ColorPicker err = new ColorPicker(Color.RED);
				VBox colorPickers = new VBox(in, out, err);
				ConsoleView consoleView = new ConsoleView(SystemIO.console);

				BorderPane borderPane = new BorderPane(colorPickers);
				borderPane.setCenter(consoleView);
				borderPane.setRight(colorPickers);

				Tab tab = new Tab("Editable Colors", borderPane);
				consoles.add(consoleView.getConsole());
				tabPane.getTabs().add(tab);

				in.setOnAction(_ -> consoleView.setTextInFill(in.getValue()));
				out.setOnAction(_ -> consoleView.setTextOutFill(out.getValue()));
				err.setOnAction(_ -> consoleView.setTextErrFill(err.getValue()));
			});


			IOConsole console = new IOConsole();
			ManagedContext managedContext =
					ManagedContext.start(Context.newBuilder("js", "python", "java")
							.allowAllAccess(true)
							.in(console.in.getInputStream())
							.out(console.out.getPrintStream())
							.err(console.err.getPrintStream()));


			IntStream.range(0, 2).forEach(i -> createTab("ConsolePane - " + i, new ConsoleView(SystemIO.console)));
			Stream.of("java", "js", "python").forEach(lang -> createTab(
					"PolyglotPane - " + lang,
					new PolyglotView(console, lang, managedContext)));
			Stream.of("cmd", "powershell")
					.map(DemoApplication::startProcess)
					.map(ProcessView::new)
					.forEach(program -> createTab(
							"ProcessPane - " + program.getProcess().info().command().orElse(""),
							program));

			runOnNewThread(() -> {

				sleep(2000);
				// Note that we're using normal System in/out/err
				// Don't need to use SystemIO because it statically initialized when we created the ConsolePane
				// Other classes don't need to know about SystemIO in order for it to work.
				System.out.println("Welcome to the console.");
				Scanner scanner = new Scanner(System.in);
				while (true) {
					sleep(100);
					System.err.println("What's your name?");
					String name = scanner.nextLine();
					sleep(100);
					System.err.println("Hello " + name + "! How old are you?");
					String age = scanner.nextLine();
					sleep(100);
					System.err.println("Wow " + name + ", you're " + age + " years old!");
					sleep(1000);
					System.out.println("Now lets do someone else.");
				}
			});
		});

	}

	private static Process startProcess(String command){
		try {
			return new ProcessBuilder(command).start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private boolean createTab(String title, ConsoleView content) {
		runFX(() -> {
			Tab tab = new Tab(title, content);
			consoles.add(content.getConsole());
			tabPane.getTabs().add(tab);
		});
		return true;
	}

	private void close() {
		for (IOConsole console : consoles) {
			try {
				console.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}