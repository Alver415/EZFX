package com.ezfx.demos;

import com.ezfx.app.EZFXApplication;
import com.ezfx.app.console.ManagedContext;
import com.ezfx.app.console.MonoglotView;
import com.ezfx.app.console.PolyglotView;
import com.ezfx.app.console.ProcessView;
import com.ezfx.base.io.IOConsole;
import com.ezfx.base.io.SystemIO;
import com.ezfx.base.utils.EZFX;
import com.ezfx.controls.console.ConsoleView;
import com.ezfx.controls.utils.Tabs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.ezfx.base.utils.EZFX.runFX;
import static java.lang.Thread.sleep;

public class SimpleConsoleApplication extends EZFXApplication {


	public static void main(String[] args) {
		//Replaces System in/out/err with read/write capable IOStreams.
		SystemIO.overrideSystemDefaults();
		Application.launch(SimpleConsoleApplication.class, args);
	}

	private final TabPane tabPane = new TabPane();

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(tabPane);
		stage.setScene(scene);
		stage.setTitle("Demo Application");
		stage.setWidth(600);
		stage.setHeight(400);
		stage.show();

		EZFX.runAsync(() -> {
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
				tabPane.getTabs().add(tab);

				in.setOnAction(_ -> consoleView.setTextInFill(in.getValue()));
				out.setOnAction(_ -> consoleView.setTextOutFill(out.getValue()));
				err.setOnAction(_ -> consoleView.setTextErrFill(err.getValue()));
			});


			IOConsole console = new IOConsole();
			ManagedContext managedContext = ManagedContext.newBuilder()
					.permittedLanguages("js", "python")
					.allowAllAccess(true)
					.in(console.in.getInputStream())
					.out(console.out.getPrintStream())
					.err(console.err.getPrintStream()).build();


			// ConsolePane
			IntStream.range(0, 2)
					.mapToObj(i -> Tabs.create("ConsolePane - " + i, new ConsoleView(SystemIO.console)))
					.forEach(tab -> Platform.runLater(() -> tabPane.getTabs().add(tab)));

			// PolyglotPane
			Platform.runLater(() -> tabPane.getTabs().add(
					Tabs.create("PolyglotPane", new PolyglotView(managedContext))));

			// MonoglotPanes
			managedContext.getLanguages().values().stream().map(language -> Tabs.create(
							"PolyglotPane - " + language.getName(),
							new MonoglotView(managedContext, language)))
					.forEach(tab -> Platform.runLater(() -> tabPane.getTabs().add(tab)));

			// ProcessView
			Stream.of("cmd", "powershell")
					.map(SimpleConsoleApplication::startProcess)
					.map(ProcessView::new)
					.map(program -> Tabs.create(
							"ProcessPane - " + program.getProcess().info().command().orElse(""),
							program))
					.forEach(tab -> Platform.runLater(() -> tabPane.getTabs().add(tab)));

			EZFX.runAsync(() -> {

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

	private static Process startProcess(String command) {
		try {
			return new ProcessBuilder(command).start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}