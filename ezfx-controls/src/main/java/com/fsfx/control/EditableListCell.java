package com.fsfx.control;

import com.fsfx.control.editor.EditorControl;
import com.fsfx.control.editor.EditorControlFactory;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import static javafx.beans.binding.Bindings.createStringBinding;

public class EditableListCell<T> extends ListCell<T> {
	private SimpleObjectProperty<T> itemProperty = new SimpleObjectProperty<>();
	private final BorderPane borderPane = new BorderPane();

	public EditableListCell(EditableListView<T> listView) {
		itemProperty.addListener((_, oldValue, value) -> {
			ObservableList<T> list = listView.getItems();
			int index = list.indexOf(oldValue);
			list.remove(index);
			list.add(index, value);
		});
		Button removeButton = new Button("X");
		removeButton.setOnAction(_ -> listView.getItems().remove(getItem()));

		Label indexLabel = new Label();
		indexLabel.textProperty().bind(createStringBinding(() -> "[%s] ".formatted(listView.getItems().indexOf(getItem())), listView.getItems()));
		borderPane.setLeft(indexLabel);
		borderPane.setRight(removeButton);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

		setOnDragDetected(event -> {
			if (!isEmpty()) {
				Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				int index = listView.getItems().indexOf(getItem());
				content.putString(String.valueOf(index));
				content.putImage(snapshot(new SnapshotParameters(), null));
				dragboard.setContent(content);
				event.consume();
			}
		});

		// Drag over event for cells
		setOnDragOver(event -> {
			if (event.getGestureSource() != this && event.getDragboard().hasString()) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
			event.consume();
		});

		// Drop event on target cell
		setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();
			if (dragboard.hasString()) {
				int draggedIdx = Integer.parseInt(dragboard.getString());
				int thisIdx = getIndex();

				// Perform the item swap
				animateSwap(listView, draggedIdx, thisIdx);
				event.setDropCompleted(true);
			} else {
				event.setDropCompleted(false);
			}
			event.consume();
		});

		// Finalize the drop
		setOnDragDone(DragEvent::consume);
	}


	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			setGraphic(null);
			setText(null);
			return;
		}
		itemProperty.set(item);
		setGraphic(borderPane);

		if (borderPane.getCenter() == null){
			//noinspection unchecked
			EditorControl<T> subControl = new EditorControlFactory().buildEditorControl((Class<T>) item.getClass(), itemProperty);
			borderPane.setCenter(subControl);
		}

	}


	private static <T> void animateSwap(ListView<T> listView, int index1, int index2) {
		// Get the ListCells at the two indices
		ListCell<T> cell1 = getCellAt(listView, index1);
		ListCell<T> cell2 = getCellAt(listView, index2);

		if (cell1 == null || cell2 == null) {
			return;  // Exit if cells are not currently displayed
		}

		double distance = cell2.getLayoutY() - cell1.getLayoutY();

		// Create TranslateTransition for each cell
		TranslateTransition transition1 = new TranslateTransition(Duration.millis(300), cell1);
		transition1.setByY(distance);  // Move cell1 downwards

		TranslateTransition transition2 = new TranslateTransition(Duration.millis(300), cell2);
		transition2.setByY(-distance);  // Move cell2 upwards

		// Run both animations in parallel
		ParallelTransition parallelTransition = new ParallelTransition(transition1, transition2);
		parallelTransition.setOnFinished(event -> {
			// Reset translations after animation
			cell1.setTranslateY(0);
			cell2.setTranslateY(0);

			// Swap the items in the list data
			listView.getItems().set(index1, listView.getItems().set(index2, listView.getItems().get(index1)));
			listView.getSelectionModel().clearAndSelect(index2);
		});

		parallelTransition.play();
	}

	private static <T> ListCell<T> getCellAt(ListView<T> listView, int index) {
		for (Node node : listView.lookupAll(".list-cell")) {
			if (node instanceof ListCell<?> cell && cell.getIndex() == index && !cell.isEmpty()) {
				return (ListCell<T>) cell;
			}
		}
		return null;
	}
}
