package com.ezfx.controls.editor;

import com.ezfx.controls.icons.Icons;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

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

	public void clearValue() {
		setValue(null);
	}

	private void setupActions() {
		EditorAction clearAction = new EditorAction();
		clearAction.setName("Clear");
		clearAction.setIcon(Icons.X);
		clearAction.setAction(this::clearValue);

		EditorAction setAction = new EditorAction();
		setAction.setName("Set Value");
		setAction.setIcon(Icons.PLUS);
		setAction.setAction(() -> {
			ChoiceDialog<T> dialog = new ChoiceDialog<>(getValue(), knownValues);
			dialog.initOwner(getScene().getWindow());
			dialog.setTitle("Value");
			dialog.setHeaderText("Choose a value.");
			dialog.getDialogPane().getButtonTypes().setAll(OK, CANCEL);
			dialog.showAndWait().ifPresent(this::setValue);
		});

		getActions().addAll(clearAction, setAction);
	}

	private final ListProperty<T> knownValues = new SimpleListProperty<>(this, "knownValues", FXCollections.observableArrayList());

	public ListProperty<T> knownValuesProperty() {
		return this.knownValues;
	}

	public ObservableList<T> getKnownValues() {
		return this.knownValuesProperty().getValue();
	}

	public void setKnownValues(ObservableList<T> value) {
		this.knownValuesProperty().setValue(value);
	}
}