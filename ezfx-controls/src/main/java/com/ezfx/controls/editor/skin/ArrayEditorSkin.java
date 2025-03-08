package com.ezfx.controls.editor.skin;

import com.ezfx.base.observable.ObservableObjectArray;
import com.ezfx.base.observable.ObservableObjectArrayImpl;
import com.ezfx.controls.editor.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.util.Subscription;

import java.lang.reflect.Array;

import static com.ezfx.controls.editor.factory.IntrospectingEditorFactory.DEFAULT_FACTORY;


@SuppressWarnings("unchecked")
public class ArrayEditorSkin<T> extends EditorSkinBase<ArrayEditor<T>, ObservableObjectArray<T>> {

	private final ObservableList<EditorView<T, Editor<T>>> wrappers = FXCollections.observableArrayList();

	boolean lock = false;

	private Subscription subscription = () -> {
	};

	public ArrayEditorSkin(ArrayEditor<T> listEditor) {
		super(listEditor);

		VBox vBox = new VBox();
		wrappers.addListener((ListChangeListener<EditorView<?, ?>>) l -> vBox.getChildren().setAll(l.getList()));

		listEditor.valueProperty().subscribe(newValue -> {
			subscription.unsubscribe();
			subscription = newValue.subscribe(this::rebuild);
			rebuild();
		});

		getChildren().setAll(new VBox(vBox));

	}

	private void rebuild() {
		locked(() -> {
			ObservableObjectArray<T> originalArray = valueProperty().getValue();
			if (originalArray.size() == 0) {
				wrappers.clear();
			} else {
				int index = 0;
				for (; index < originalArray.size(); index++) {
					if (index < wrappers.size()) {
						// Reset existing
						EditorView<T, Editor<T>> existing = wrappers.get(index);
						T item = originalArray.get(index);
						boolean changed = existing.getEditor().getValue() != item;
						if (changed) {
							existing.getEditor().setValue(item);
						}
					} else {
						// Create new
						T item = originalArray.get(index);
						Class<T> type = editor.getGenericType();
						Property<T> property = new SimpleObjectProperty<>(item);
						int i = index;
						property.addListener((_, _, v) -> {
							if (!lock) {
								Class<T> genericType = editor.getGenericType();
								ObservableObjectArray<T> orig = valueProperty().getValue();
								T[] intermediate = (T[]) Array.newInstance(editor.getGenericType(), orig.size());
								ObservableObjectArrayImpl<T> newA = new ObservableObjectArrayImpl<>(genericType, intermediate);
								orig.copyTo(0, newA, 0, orig.size());
								newA.set(i, v);
								valueProperty().setValue(newA);
							}
						});
						Editor<T> editor = DEFAULT_FACTORY.buildEditor(type, property).orElseGet(EditorBase::new);
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
			subscription = valueProperty().getValue().subscribe(this::rebuild);
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
}
