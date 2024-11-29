package com.ezfx.polyglot;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.graalvm.polyglot.Context;

public class PolyglotDialog<T> extends Dialog<T> {

	private ContextFX context;
	private Class<T> type;

	private final GridPane grid;
	private final Label label;
	private final TextArea textArea;


	public PolyglotDialog(Context context, Class<T> type) {
		this.context = new ContextFX(context);
		this.type = type;
		final DialogPane dialogPane = getDialogPane();

		// -- textArea
		this.textArea = new TextArea();
		this.textArea.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane.setFillWidth(textArea, true);

		// -- label
		label = new Label("polyglot Input");
		label.setMaxWidth(Double.MAX_VALUE);
		label.setMaxHeight(Double.MAX_VALUE);
		label.getStyleClass().add("content");
		label.setWrapText(true);
		label.setPrefWidth(360);
		label.setPrefWidth(Region.USE_COMPUTED_SIZE);
		label.textProperty().bind(dialogPane.contentTextProperty());

		this.grid = new GridPane();
		this.grid.setHgap(10);
		this.grid.setMaxWidth(Double.MAX_VALUE);
		this.grid.setAlignment(Pos.CENTER_LEFT);

		dialogPane.contentTextProperty().addListener(o -> updateGrid());

		setTitle("Polyglot Input");
		dialogPane.setHeaderText("Header Text");
		dialogPane.getStyleClass().add("text-input-dialog");
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		updateGrid();

		setResultConverter((dialogButton) -> {
			ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			String input = data == ButtonBar.ButtonData.OK_DONE ? textArea.getText() : null;
			return this.context.execute(input);
		});
	}

	public void updateGrid() {
		grid.getChildren().clear();

		grid.add(label, 0, 0);
		grid.add(textArea, 1, 0);
		getDialogPane().setContent(grid);

		Platform.runLater(textArea::requestFocus);
	}
}
