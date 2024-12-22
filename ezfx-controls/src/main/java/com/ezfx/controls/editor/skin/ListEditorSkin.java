package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Subscription;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;


public class ListEditorSkin<T> extends EditorSkin<ListEditor<T>, ObservableList<T>> {

	private final ObservableList<EditorWrapper<T, Editor<T>>> wrappers = FXCollections.observableArrayList();

	boolean lock = false;

	private Subscription subscription = () -> {
	};

	EditorFactory factory = new EditorFactory();
	public ListEditorSkin(ListEditor<T> listEditor) {
		super(listEditor);

		VBox vBox = new VBox();
		wrappers.addListener((ListChangeListener<EditorWrapper<?, ?>>) l -> vBox.getChildren().setAll(l.getList()));

		listEditor.valueProperty().subscribe(newValue -> {
			subscription.unsubscribe();
			subscription = newValue.subscribe(this::rebuild);
			rebuild();
		});

//		EditorAction clear = new EditorAction();
//		clear.setName("Clear");
//		clear.setIcon(Icons.X);
//		clear.setAction(() -> property().getValue().clear());
//
//		EditorAction plus = new EditorAction();
//		plus.setName("Add");
//		plus.setIcon(Icons.PLUS);
//		plus.setAction(() -> property().getValue().add(listEditor.getIntrospector().getDefaultValueForType(listEditor.getGenericType())));
//
//		EditorAction minus = new EditorAction();
//		minus.setName("Remove");
//		minus.setIcon(Icons.MINUS);
//		minus.setAction(() -> {
//			ObservableList<T> list = property().getValue();
//			if (!list.isEmpty()) list.removeLast();
//		});

//		List<Button> list = Stream.of(clear, plus, minus).map(this::buildActionButton).toList();
//		HBox actions = new HBox(4, list.toArray(new Node[0]));
//		getChildren().setAll(new VBox(actions, vBox));

		getChildren().setAll(new VBox(vBox));

	}

	private void rebuild() {
		locked(() -> {
			ObservableList<T> list = FXCollections.observableArrayList();
			list.addAll(property().getValue());
			if (list.isEmpty()) {
				wrappers.clear();
			} else {
				int index = 0;
				for (; index < list.size(); index++) {
					if (index < wrappers.size()) {
						// Reset existing
						EditorWrapper<T, Editor<T>> existing = wrappers.get(index);
						T item = list.get(index);
						boolean changed = existing.getEditor().getValue() == item;
						if (changed) {
							existing.getEditor().setValue(item);
						}
					} else {
						// Create new
						T item = list.get(index);
						Class<T> type = (Class<T>) item.getClass();
						Property<T> property = new SimpleObjectProperty<>(item);
						int i = index;
						property.addListener((_, _, v) -> {
							if (!lock) property().getValue().set(i, v);
						});
						Editor<T> editor = factory.buildEditor(type, property);
						EditorWrapper<T, Editor<T>> wrapper = new EditorWrapper<>(editor);
						wrapper.nameProperty().bind(Bindings.createIntegerBinding(
								() -> wrappers.indexOf(wrapper), wrappers).map("[%s]"::formatted));
						wrappers.add(index, wrapper);
					}
				}

				for (; index < wrappers.size(); index++) {
					wrappers.remove(index);
				}
			}
			subscription.unsubscribe();
			subscription = list.subscribe(this::rebuild);
		});
	}

	private void locked(Runnable runnable) {
		try {
			lock = true;
			runnable.run();
		} finally {
			lock = false;
		}
	}

	private Button buildActionButton(Action action) {
		return ActionUtils.createButton(action);
//		Button button = new Button();
//		button.getStyleClass().add("icon-button");
//		button.setGraphic(new ImageView(action.getIcon()));
//		button.setTooltip(new Tooltip(action.getName()));
//		button.onActionProperty().bind(action.actionProperty().map(a -> _ -> a.run()));
//		return button;
	}
}
