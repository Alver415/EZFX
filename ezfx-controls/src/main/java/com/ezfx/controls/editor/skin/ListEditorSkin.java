package com.ezfx.controls.editor.skin;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorView;
import com.ezfx.controls.editor.ListEditor;
import com.ezfx.controls.icons.SVGs;
import com.ezfx.controls.utils.Actions;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Subscription;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

import java.util.List;
import java.util.stream.Stream;

import static com.ezfx.controls.editor.factory.IntrospectingEditorFactory.DEFAULT_FACTORY;


public class ListEditorSkin<T> extends EditorSkin<ListEditor<T>, ObservableList<T>> {

	private final ObservableList<EditorView<T, Editor<T>>> wrappers = FXCollections.observableArrayList();

	boolean lock = false;

	private Subscription subscription = () -> {
	};

	public ListEditorSkin(ListEditor<T> listEditor) {
		super(listEditor);

		VBox vBox = new VBox();
		wrappers.addListener((ListChangeListener<EditorView<?, ?>>) l -> vBox.getChildren().setAll(l.getList()));

		listEditor.valueProperty().subscribe(newValue -> {
			subscription.unsubscribe();
			subscription = newValue.subscribe(this::rebuild);
			rebuild();
		});

		Action clear = Actions.newBuilder()
				.text("Clear")
				.graphic(SVGs.CLOSE.svg())
				.action(() -> getValue().clear())
				.build();
		Action plus = Actions.newBuilder()
				.text("Add")
				.graphic(SVGs.MAXIMIZE.svg())
				.action(() -> getValue().add(listEditor.getIntrospector().getDefaultValueForType(listEditor.getGenericType())))
				.build();

		Action minus = Actions.newBuilder()
				.text("Remove")
				.graphic(SVGs.MINIMIZE.svg())
				.action(() -> {
					if (getValue().isEmpty()) {
						getValue();
					} else {
						getValue().removeLast();
					}
				})
				.build();


		List<Button> list = Stream.of(clear, plus, minus).map(this::buildActionButton).toList();
		HBox actions = new HBox(4, list.toArray(new Node[0]));
		getChildren().setAll(new VBox(actions, vBox));


	}

	private void rebuild() {
		locked(() -> {
			ObservableList<T> list = FXCollections.observableArrayList();
			list.addAll(valueProperty().getValue());
			if (list.isEmpty()) {
				wrappers.clear();
			} else {
				int index = 0;
				for (; index < list.size(); index++) {
					if (index < wrappers.size()) {
						// Reset existing
						EditorView<T, Editor<T>> existing = wrappers.get(index);
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
							if (!lock) valueProperty().getValue().set(i, v);
						});
						//TODO: Remove dependence on DEFAULT_FACTORY
						Editor<T> editor = DEFAULT_FACTORY.buildEditor(type, property).orElseGet(Editor::new);
						;
						EditorView<T, Editor<T>> wrapper = new EditorView<>(editor);
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
