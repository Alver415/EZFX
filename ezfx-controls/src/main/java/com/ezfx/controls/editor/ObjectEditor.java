package com.ezfx.controls.editor;

import com.ezfx.controls.editor.impl.standard.SelectionEditor;
import com.ezfx.controls.editor.introspective.ActionIntrospector;
import com.ezfx.controls.editor.introspective.EditorDialog;
import com.ezfx.controls.icons.Icons;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionMap;
import org.controlsfx.control.action.ActionProxy;

import java.util.Map;

public abstract class ObjectEditor<T> extends Editor<T> {

	public ObjectEditor() {
		this(new SimpleObjectProperty<>());
	}

	public ObjectEditor(T initialValue) {
		this(new SimpleObjectProperty<>(initialValue));
	}

	public ObjectEditor(Property<T> property) {
		super(property);
		setupActions();
	}

	@ActionProxy(id="clearValue", text="Clear", longText = "Set value to null", graphic = "font>FontAwesome|TIMES")
	public void clearValue(ActionEvent event) {
		setValue(null);
	}

	private void setupActions() {
		ActionIntrospector.register(this);
		Action clearAction = ActionIntrospector.action("clearValue");
//
//		Action setAction = new Action();
//		setAction.setName("Set Value");
//		setAction.setIcon(Icons.PLUS);
//		setAction.setAction(() -> {
//			ObservableValue<ObservableList<String>> names = knownValues.map(Map::keySet).map(FXCollections::observableArrayList);
////			ChoiceDialog<String> dialog = new ChoiceDialog<>(null, names.getValue());
////			dialog.initOwner(getScene().getWindow());
////			dialog.setTitle("Value");
////			dialog.setHeaderText("Choose a value.");
////			dialog.getDialogPane().getButtonTypes().setAll(OK, CANCEL);
////			dialog.showAndWait().map(knownValues::get).ifPresent(this::setValue);
//			SelectionEditor<String> selectionEditor = new SelectionEditor<>(names.getValue());
//			SimpleStringProperty selectedName = new SimpleStringProperty();
//			EditorDialog<String> bd = new EditorDialog<>(selectedName, selectionEditor);
//			bd.show();
//			selectedName.addListener((_, _, value) -> property().setValue(knownValues.get(value)));
//		});

		getActions().addAll(clearAction);
	}

	private final MapProperty<String, T> knownValues = new SimpleMapProperty<>(this, "knownValues", FXCollections.observableHashMap());

	public MapProperty<String, T> knownValuesProperty() {
		return this.knownValues;
	}

	public ObservableMap<String, T> getKnownValues() {
		return this.knownValuesProperty().getValue();
	}

	public void setKnownValues(ObservableMap<String, T> value) {
		this.knownValuesProperty().setValue(value);
	}

//	private final ListProperty<T> knownValues = new SimpleListProperty<>(this, "knownValues", FXCollections.observableArrayList());
//
//	public ListProperty<T> knownValuesProperty() {
//		return this.knownValues;
//	}
//
//	public ObservableList<T> getKnownValues() {
//		return this.knownValuesProperty().getValue();
//	}
//
//	public void setKnownValues(ObservableList<T> value) {
//		this.knownValuesProperty().setValue(value);
//	}
}