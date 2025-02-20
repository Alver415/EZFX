package com.ezfx.controls.explorer;

import com.ezfx.controls.misc.FilterableTreeItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;

public class GenericTreeCell<T> extends TreeCell<T> {

	private static final Map<Class<?>, Image> ICON_CACHE = new HashMap<>();
	private static final Map<Class<?>, String> JAVA_TYPE_CACHE = new HashMap<>();
	public static final PseudoClass FILTERED = PseudoClass.getPseudoClass("filtered");

	private final BorderPane borderPane;

	private final HBox left;
	private final ImageView icon;

	private final HBox center;
	private final Label styleClassLabel;
	private final Label nodeIdLabel;
	private final Label javaTypeLabel;

	private final HBox right;
	private final CheckBox visibleCheckBox;

	public GenericTreeCell() {
		setContentDisplay(ContentDisplay.LEFT);

		// Build Structure
		icon = new ImageView();
		icon.getStyleClass().add("icon");
		left = new HBox(icon);

		javaTypeLabel = new Label();
		javaTypeLabel.getStyleClass().add("java-type");
		nodeIdLabel = new Label();
		nodeIdLabel.getStyleClass().add("node-id");
		styleClassLabel = new Label();
		styleClassLabel.getStyleClass().add("style-class");
		center = new HBox(javaTypeLabel, nodeIdLabel, styleClassLabel);
		center.setAlignment(Pos.CENTER_LEFT);

		visibleCheckBox = new CheckBox();
		right = new HBox(visibleCheckBox);
		right.setAlignment(Pos.CENTER_RIGHT);

		borderPane = new BorderPane();
		borderPane.setLeft(left);
		borderPane.setCenter(center);
		borderPane.setRight(right);

		// Setup Bindings
		ObservableValue<TreeValue<?, ?>> treeValue = treeItemProperty()
				.flatMap(TreeItem::valueProperty)
				.map(cast -> (TreeValue<?, ?>) cast);

		icon.imageProperty().bind(treeValue.flatMap(TreeValue::observableIcon));
		javaTypeLabel.textProperty().bind(treeValue.flatMap(TreeValue::observableJavaType));
		nodeIdLabel.textProperty().bind(treeValue.flatMap(TreeValue::observableNodeId));
		styleClassLabel.textProperty().bind(treeValue.map(TreeValue::observableStyleClass).map(v -> String.join(", ", v)));
		visibleCheckBox.selectedProperty().bind(treeValue.flatMap(TreeValue::observableVisibility));

		treeItemProperty()
				.flatMap(treeItem -> treeItem instanceof FilterableTreeItem<T> filterableTreeItem ?
						filterableTreeItem.predicateProperty() : null)
				.flatMap(filter -> currentItemProperty().map(filter::test).map(b -> !b))
				.subscribe(filtered -> pseudoClassStateChanged(FILTERED, filtered != null && filtered));

	}


	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			currentItem.setValue(null);
			setGraphic(null);
		} else {
			currentItem.setValue(item);
			setGraphic(borderPane);
		}
	}

	private final Property<T> currentItem = new SimpleObjectProperty<>(this, "currentItem");

	public Property<T> currentItemProperty() {
		return this.currentItem;
	}

	public T getCurrentItem() {
		return this.currentItemProperty().getValue();
	}

	public void setCurrentItem(T value) {
		this.currentItemProperty().setValue(value);
	}

	private final BooleanProperty filtered = new SimpleBooleanProperty(this, "filtered");

	public BooleanProperty filteredProperty() {
		return this.filtered;
	}

	public Boolean isFiltered() {
		return this.filteredProperty().getValue();
	}

	public void setFiltered(Boolean value) {
		this.filteredProperty().setValue(value);
	}
}
