package com.ezfx.controls.editor;

import com.ezfx.controls.editor.introspective.ActionIntrospector;
import com.ezfx.controls.editor.skin.EditorSkin;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.control.action.ActionProxy;

import java.util.List;
import java.util.Optional;

public class Editor<T> extends Control {

	private final T initialValue;
	private final Property<T> value;

	public Editor() {
		this(new SimpleObjectProperty<>());
	}

	public Editor(Property<T> value) {
		this.value = value;
		setFocusTraversable(false);
		if (value.isBound()) setDisable(true);

		initialValue = value.getValue();
		setupActions();


	}

	@ActionProxy(id = "clear", text = "Clear", longText = "Set value to null", graphic = "font>FontAwesome|TIMES")
	public void clear() {
		setValue(null);
	}

	@ActionProxy(id = "reset", text = "Reset", longText = "Reset value to initial value", graphic = "font>FontAwesome|UNDO")
	public void reset() {
		setValue(initialValue);
	}

	private void setupActions() {
		ActionIntrospector.register(this);
		getActions().addAll(ActionIntrospector.actions("reset", "clear"));

		SkinRegistry skinRegistry = new SkinRegistry();
		ObservableList<SkinRegistry.SkinOption<Editor<T>>> skinBuilders = skinRegistry.getSkinBuilder(this);
		if (skinBuilders != null && !skinBuilders.isEmpty()) {
			List<Action> list = skinBuilders.stream().map(option ->
					new Action(option.getName(), _ -> this.setSkin(option.getFunction().apply(this)))
			).toList();
			getActions().add(new ActionGroup("Skins", list));
		}
	}

	public Property<T> valueProperty() {
		return value;
	}

	public T getValue() {
		return valueProperty().getValue();
	}

	public void setValue(T value) {
		this.value.setValue(value);
	}

	private final ListProperty<Action> actions = new SimpleListProperty<>(this, "actions", FXCollections.observableArrayList());

	public ListProperty<Action> actionsProperty() {
		return this.actions;
	}

	public ObservableList<Action> getActions() {
		return this.actionsProperty().getValue();
	}

	public void setActions(ObservableList<Action> value) {
		this.actionsProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new EditorSkin<>(this);
	}

	@Override
	public String toString() {
		String className = Optional.of(getClass()).map(Class::getSimpleName).orElse(getClass().getName());
		return "%s{%s=%s}".formatted(className, value.getName(), value.getValue());
	}
}