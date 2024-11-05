package com.fsfx.control.editor;

import com.fsfx.control.EditableListView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ListEditor<T> extends EditorControl<ObservableList<T>> {

	public ListEditor(ObservableList<T> list) {
		super(new SimpleObjectProperty<>(list));
	}

	public ListEditor(ListProperty<T> property) {
		super(property);
	}

	public ListEditor(Property<ObservableList<T>> property) {
		super(property);
	}

	private final ObjectProperty<Supplier<T>> createElementHook = new SimpleObjectProperty<>(this, "createElementHook");

	public ObjectProperty<Supplier<T>> createElementHookProperty() {
		return this.createElementHook;
	}

	public Supplier<T> getCreateElementHook() {
		return this.createElementHookProperty().get();
	}

	public void setCreateElementHook(Supplier<T> value) {
		this.createElementHookProperty().set(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new VBoxEditor<>(this);
	}

	public static class ListEditorSkin<T> extends SkinBase<ListEditor<T>> {

		private ListEditorSkin(ListEditor<T> control) {
			super(control);
			ObservableList<T> list = control.getProperty().getValue();

			EditableListView<T> listView = new EditableListView<>();
			listView.setItems(list);
			listView.setMinHeight(100);
			Button addButton = new Button("Add");
			addButton.setOnAction(_ -> control.getProperty().getValue().add(control.getCreateElementHook().get()));
			addButton.disableProperty().bind(control.createElementHookProperty().map(Objects::isNull).orElse(true));
			BorderPane borderPane = new BorderPane();
			borderPane.setCenter(listView);
			borderPane.setBottom(addButton);
			getChildren().setAll(borderPane);
		}
	}

	public static class VBoxEditor<T> extends SkinBase<ListEditor<T>> {

		List<Editor<T, ?>> editors = new ArrayList<>();

		boolean lock = false;

		private VBoxEditor(ListEditor<T> control) {
			super(control);
			ObservableList<T> list = control.getProperty().getValue();

			VBox vBox = new VBox();
			EditorControlFactory factory = new EditorControlFactory();

			Runnable setupEditors = () -> {
				lock = true;
				for (int i = 0; i < list.size(); i++) {
					T item = list.get(i);
					Editor<T, ?> editor;
					if (editors.size() <= i) {
						//noinspection unchecked
						Class<T> clazz = (Class<T>) item.getClass();
						SimpleObjectProperty<T> property = new SimpleObjectProperty<>(item);
						int finalI = i;
						property.addListener((_, _, newValue) -> {
							if (!lock) {
								list.remove(finalI);
								list.add(finalI, newValue);
							}
						});
						EditorControl<T> subControl = factory.buildEditorControl(clazz, property);
						StringProperty textProperty = new SimpleStringProperty();
						editor = new Editor<>(textProperty, subControl);
						textProperty.bind(Bindings.createStringBinding(
								() -> "[%s]".formatted(vBox.getChildren().indexOf(editor)),
								vBox.getChildren()));
						editor.setOnDragDetected(event -> {
							if (!list.isEmpty()) {
								Dragboard dragboard = editor.startDragAndDrop(TransferMode.MOVE);
								ClipboardContent content = new ClipboardContent();
								int index = list.indexOf(subControl.getProperty().getValue());
								content.putString(String.valueOf(index));
								content.putImage(editor.snapshot(new SnapshotParameters(), null));
								dragboard.setContent(content);
								event.consume();
							}
						});

						// Drag over event for cells
						editor.setOnDragOver(event -> {
							if (event.getGestureSource() != this && event.getDragboard().hasString()) {
								event.acceptTransferModes(TransferMode.MOVE);
							}
							event.consume();
						});

						// Drop event on target cell
						int thisIdx = i;
						editor.setOnDragDropped(event -> {
							Dragboard dragboard = event.getDragboard();
							if (dragboard.hasString()) {
								int draggedIdx = Integer.parseInt(dragboard.getString());

								if (draggedIdx != thisIdx) {
									T first = list.get(draggedIdx);
									T second = list.set(thisIdx, first);
									list.set(draggedIdx, second);
								}
								event.setDropCompleted(true);
							} else {
								event.setDropCompleted(false);
							}
							event.consume();
						});

						// Finalize the drop
						editor.setOnDragDone(DragEvent::consume);
						editors.add(i, editor);
						vBox.getChildren().add(i, editor);
					} else {
						editor = editors.get(i);
					}
					Property<T> property = editor.getControl().getProperty();
					if (!Objects.equals(property.getValue(), item)) {
						property.setValue(item);
					}
				}
				lock = false;
			};
			list.subscribe(setupEditors);
			Button addButton = new Button("Add");
			addButton.setOnAction(_ -> control.getProperty().getValue().add(control.getCreateElementHook().get()));
			addButton.disableProperty().bind(control.createElementHookProperty().map(Objects::isNull).orElse(true));
			BorderPane borderPane = new BorderPane();
			borderPane.setCenter(vBox);
			borderPane.setBottom(addButton);
			getChildren().setAll(borderPane);

			setupEditors.run();


		}
	}

}
