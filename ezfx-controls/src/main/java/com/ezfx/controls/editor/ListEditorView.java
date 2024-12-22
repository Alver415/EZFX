package com.ezfx.controls.editor;

import com.ezfx.controls.misc.DraggableListCell;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

public class ListEditorView<T> extends ListView<T> {

	private static final Logger log = LoggerFactory.getLogger(ListEditorView.class);

	private final ObservableMap<Integer, Editor<T>> editors = FXCollections.observableMap(new TreeMap<>());
	private final Button addButton = new Button("Add");

	boolean lock = false;

	private final ListEditor<T> editor;
	public ListEditorView(ListEditor<T> editor) {
		setMinSize(200,200);
		this.editor = editor;
		DoubleBinding height = Bindings.createDoubleBinding(() -> {
			double sum = 0;
			for (int i = 0; i < editors.size() && i < getItems().size(); i++) {
				sum += editors.get(i).getHeight();
			}
			return Math.max(20, sum + addButton.getHeight());
		}, editors, addButton.heightProperty(), itemsProperty());
		prefHeightProperty().bind(height);
		minHeightProperty().bind(height);
		maxHeightProperty().bind(height);
		setPlaceholder(addButton);

		setCellFactory(_ -> new ListEditorCell());
		itemsProperty().bind(editor.valueProperty());

		ListChangeListener<T> listener = change -> {
			lock = true;
			try {
				while (change.next()) {
					if (change.wasPermutated()) {
						for (int i = change.getFrom(); i < change.getTo(); ++i) {
							//permutate
						}
					} else if (change.wasUpdated()) {
						//update item
					} else {
						for (T remitem : change.getRemoved()) {
						}
						int addedSize = change.getAddedSize();
						List<? extends T> added = change.getAddedSubList();
						for (int i = 0; i < addedSize; i++) {
							int index = i + change.getFrom();
							setupEditor(index, added.get(i));
						}
					}
				}
			} finally {
				lock = false;
			}
		};
		Consumer<ObservableList<T>> setupEditors = list -> {
			for (int i = 0; i < list.size(); i++) {
				setupEditor(i, list.get(i));
			}
		};
		editor.valueProperty().addListener((_, oldValue, newValue) -> {
			try {
				lock = true;
				if (oldValue != null) {
					oldValue.removeListener(listener);
				}
				if (newValue != null) {
					newValue.addListener(listener);
				}
				setupEditors.accept(newValue);
			} finally {
				lock = false;
			}
		});
	}

	private Editor<T> setupEditor(int index, T item) {
		if (!editors.containsKey(index)) {
			//noinspection unchecked
			Class<T> clazz = (Class<T>) item.getClass();
			SimpleObjectProperty<T> property = new SimpleObjectProperty<>(item);
			property.addListener((_, oldValue, newValue) -> {
				if (!lock) editor.valueProperty().getValue().set(index, newValue);
			});
			Editor<T> editor = new EditorFactory().buildEditor(clazz, property);

			editors.put(index, editor);
			return editor;
		} else {
			Editor<T> editor = editors.get(index);
			try {
				lock = true;
				editor.valueProperty().setValue(item);
			} finally {
				lock = false;
			}
			return editor;
		}
	}

	private class ListEditorCell extends DraggableListCell<T> {

		private final BooleanProperty showAddButton = new SimpleBooleanProperty(this, "showAddButton");
		private final ObjectProperty<Editor<T>> editorProperty = new SimpleObjectProperty<>();
		private final ObjectBinding<Node> content;

		public ListEditorCell() {
			super();
			setContentDisplay(ContentDisplay.RIGHT);
			ObservableValue<Integer> listSize = listViewProperty().flatMap(ListView::itemsProperty).map(List::size).orElse(0);
			showAddButton.bind(Bindings.createBooleanBinding(
					() -> getListView() != null && getIndex() == listSize.getValue(),
					indexProperty(), listSize, listViewProperty()));

			content = Bindings.createObjectBinding(() -> {
				if (showAddButton.get()) {
					return addButton;
				} else if (editorProperty.get() != null) {
					Text label = new Text();
					label.textProperty().bind(indexProperty().map("[%d]"::formatted));
					return new HBox(label, editorProperty.get());
				} else {
					return null;
				}
			}, showAddButton, editorProperty, indexProperty());
			graphicProperty().bind(content);

		}

		@Override
		protected void updateItem(T item, boolean empty) {
			super.updateItem(item, empty);
			int index = getIndex(); // Get the index of this cell
//			textProperty().bind(indexProperty().map("[%d]"::formatted));

			if (empty || item == null) {
				editorProperty.set(null);
			} else {
				editorProperty.set(setupEditor(index, item));
			}
		}
	}
}
