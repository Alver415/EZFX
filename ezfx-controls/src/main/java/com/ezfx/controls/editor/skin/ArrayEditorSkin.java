package com.ezfx.controls.editor.skin;

import com.ezfx.base.observable.ObservableObjectArray;
import com.ezfx.base.observable.ObservableObjectArrayImpl;
import com.ezfx.controls.editor.*;
import com.ezfx.controls.icons.Icons;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Subscription;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Stream;


@SuppressWarnings("unchecked")
public class ArrayEditorSkin<T> extends EditorSkin<ArrayEditor<T>, ObservableObjectArray<T>> {

	private final ObservableList<EditorWrapper<T, Editor<T>>> wrappers = FXCollections.observableArrayList();

	boolean lock = false;

	private Subscription subscription = () -> {
	};

	EditorFactory factory = new EditorFactory();

	public ArrayEditorSkin(ArrayEditor<T> listEditor) {
		super(listEditor);

		VBox vBox = new VBox();
		wrappers.addListener((ListChangeListener<EditorWrapper<?, ?>>) l -> vBox.getChildren().setAll(l.getList()));

		listEditor.property().subscribe(newValue -> {
			subscription.unsubscribe();
			subscription = newValue.subscribe(this::rebuild);
			rebuild();
		});

		EditorAction clear = new EditorAction();
		clear.setName("Clear");
		clear.setIcon(Icons.X);
		clear.setAction(() -> property().getValue().clear());

		EditorAction plus = new EditorAction();
		plus.setName("Add");
		plus.setIcon(Icons.PLUS);
		plus.setAction(() -> {
			T newValue = listEditor.getIntrospector().getDefaultValueForType(listEditor.getGenericType());
			ObservableObjectArray<T> array = property().getValue();
			int newSize = array.size() + 1;
			array.resize(newSize);
			array.ensureCapacity(newSize);
			array.set(newSize - 1, newValue);
		});

		EditorAction minus = new EditorAction();
		minus.setName("Remove");
		minus.setIcon(Icons.MINUS);
		minus.setAction(() -> {
			ObservableObjectArray<T> array = property().getValue();
			if (array.size() > 0) {
				array.resize(array.size() - 1);
			}
		});

		List<Button> list = Stream.of(clear, plus, minus).map(this::buildActionButton).toList();
		HBox actions = new HBox(4, list.toArray(new Node[0]));
		getChildren().setAll(new VBox(actions, vBox));

	}

	private void rebuild() {
		locked(() -> {
			ObservableObjectArray<T> originalArray = property().getValue();
//			T[] oldArray = originalArray.toArray((T[]) Array.newInstance(editor().getGenericType(), 0));
			if (originalArray.size() == 0) {
				wrappers.clear();
			} else {
				int index = 0;
				for (; index < originalArray.size(); index++) {
					if (index < wrappers.size()) {
						// Reset existing
						EditorWrapper<T, Editor<T>> existing = wrappers.get(index);
						T item = originalArray.get(index);
						boolean changed = existing.getEditor().getValue() != item;
						if (changed) {
							existing.getEditor().setValue(item);
						}
					} else {
						// Create new
						T item = originalArray.get(index);
						Class<T> type = editor().getGenericType();
						Property<T> property = new SimpleObjectProperty<>(item);
						int i = index;
						property.addListener((_, _, v) -> {
							if (!lock){
								Class<T> genericType = editor().getGenericType();
								ObservableObjectArray<T> orig = property().getValue();
								T[] intermediate = (T[]) Array.newInstance(editor().getGenericType(), orig.size());
								ObservableObjectArrayImpl<T> newA = new ObservableObjectArrayImpl<>(genericType, intermediate);
								orig.copyTo(0, newA, 0, orig.size());
								newA.set(i,v);
								property().setValue(newA);
							}
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

//			T[] newArray = originalArray.toArray((T[]) Array.newInstance(editor().getGenericType(), 0));
//
//			boolean equals = Arrays.equals(oldArray, newArray);
//			if (!equals) {
//				Class<T> genericType = editor().getGenericType();
//				ObservableObjectArrayImpl<T> value = new ObservableObjectArrayImpl<>(genericType, originalArray);
//				property().setValue(value);
//			}

			subscription.unsubscribe();
			subscription = property().getValue().subscribe(this::rebuild);
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

	private Button buildActionButton(EditorAction action) {
		Button button = new Button();
		button.getStyleClass().add("icon-button");
		button.setGraphic(new ImageView(action.getIcon()));
		button.setTooltip(new Tooltip(action.getName()));
		button.onActionProperty().bind(action.actionProperty().map(a -> _ -> a.run()));
		return button;
	}
}
