package com.ezfx.controls.misc;

import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DraggableListCell<T> extends ListCell<T> {

	private static final Logger log = LoggerFactory.getLogger(DraggableListCell.class);

	public DraggableListCell() {
		setOnDragDetected(this::onDragDetected);
		setOnDragOver(this::onDragOver);
		setOnDragEntered(this::onDragEntered);
		setOnDragExited(this::onDragExited);
		setOnDragDropped(this::onDragDropped);
		setOnDragDone(DragEvent::consume);
	}


	private void onDragDetected(MouseEvent event) {
		if (!isEmpty()) {
			Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			int index = getListView().getItems().indexOf(getItem());
			content.putString(String.valueOf(index));
			content.putImage(snapshot(new SnapshotParameters(), null));
			dragboard.setContent(content);
			event.consume();
		}
	}

	private void onDragOver(DragEvent event) {
		if (event.getGestureSource() != this && event.getDragboard().hasString()) {
			event.acceptTransferModes(TransferMode.MOVE);
		}
		event.consume();
	}

	private void onDragDropped(DragEvent event) {
		Dragboard dragboard = event.getDragboard();
		if (dragboard.hasString()) {
			int draggedIdx = Integer.parseInt(dragboard.getString());
			int thisIdx = getIndex();

			// Perform the item swap
			ObservableList<T> list = getListView().getItems();
			if (thisIdx < 0 || thisIdx >= list.size()) thisIdx = list.size() - 1;
			list.set(draggedIdx, list.set(thisIdx, list.get(draggedIdx)));
			getListView().getSelectionModel().select(thisIdx);
			event.setDropCompleted(true);
		} else {
			event.setDropCompleted(false);
		}
		event.consume();
	}

	private static final PseudoClass DRAG_HOVER = PseudoClass.getPseudoClass("drag-hover");


	static long last = System.currentTimeMillis();

	private void onDragEntered(DragEvent event) {
		if (event.getGestureSource() != this && event.getDragboard().hasString() && !isEmpty()) {
			long now = System.currentTimeMillis();
			log.info(String.valueOf(now - last));
			last = now;
			pseudoClassStateChanged(DRAG_HOVER, true);
			getStyleClass().add("highlight");
		}
	}

	private void onDragExited(DragEvent event) {
		pseudoClassStateChanged(DRAG_HOVER, false);
		getStyleClass().remove("highlight");
	}
}
