package com.ezfx.polyglot;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Language;

import java.util.Map;
import java.util.stream.Collectors;

public class PolyglotDialog<T> extends Dialog<T> {

	private ContextFX context;
	private Class<T> type;

	private final GridPane grid;
	private final Label label;
	private final TextArea textArea;
	private final ComboBox<Language> languageChoice;


	public PolyglotDialog(Context context, Class<T> type) {
		this.context = new ContextFX(context);
		this.type = type;
		final DialogPane dialogPane = getDialogPane();

		// -- languageChoice
		Map<String, Language> languageMap = context.getEngine().getLanguages();
		ObservableList<Language> languages = languageMap.values().stream()
				.collect(Collectors.toCollection(FXCollections::observableArrayList));
		this.languageChoice = new ComboBox<>(languages);
		Callback<ListView<Language>, ListCell<Language>> cellFactory = _ -> new ListCell<>() {
			@Override
			protected void updateItem(Language item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					setText(item.getName());
				}
			}
		};
		this.languageChoice.setCellFactory(cellFactory);
		this.languageChoice.setButtonCell(cellFactory.call(null));

		// -- textArea
		this.textArea = new TextArea();
		this.textArea.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane.setFillWidth(textArea, true);

		// -- label
		label = new Label("Polyglot Input");
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
			String languageId = languageChoice.getSelectionModel().getSelectedItem().getId();
			return this.context.execute(languageId, input);
		});
	}

	public void updateGrid() {
		grid.getChildren().clear();

		grid.add(label, 0, 0);
		grid.add(languageChoice, 1, 0);
		grid.add(textArea, 2, 0);
		getDialogPane().setContent(grid);

		Platform.runLater(textArea::requestFocus);
	}
}
