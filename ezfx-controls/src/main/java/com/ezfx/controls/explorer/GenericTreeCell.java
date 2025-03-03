package com.ezfx.controls.explorer;

import com.ezfx.controls.info.NodeInfo;
import com.ezfx.controls.misc.FilterableTreeItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class GenericTreeCell<T> extends TreeCell<T> {

	public static final PseudoClass FILTERED = PseudoClass.getPseudoClass("filtered");

	private final BorderPane borderPane;

	private final NodeInfo center;

	private final HBox right;
	private final CheckBox visibleCheckBox;

	public GenericTreeCell() {
		setContentDisplay(ContentDisplay.LEFT);

		// Build Structure
		center = new NodeInfo();
		center.subjectProperty().bind(currentItem.map(object -> object instanceof TreeValue.NodeTreeValue ntv ? ntv.getValue() : null));

		visibleCheckBox = new CheckBox();
		right = new HBox(visibleCheckBox);
		right.setAlignment(Pos.CENTER_RIGHT);

		borderPane = new BorderPane();
		borderPane.setCenter(center);
		borderPane.setRight(right);

		// Setup Bindings
		ObservableValue<TreeValue<?, ?>> treeValue = treeItemProperty()
				.flatMap(TreeItem::valueProperty)
				.map(cast -> (TreeValue<?, ?>) cast);

		visibleCheckBox.selectedProperty().bind(treeValue.flatMap(TreeValue::observableVisibility));

		treeItemProperty()
				.flatMap(treeItem -> treeItem instanceof FilterableTreeItem<T> filterableTreeItem ?
						filterableTreeItem.predicateProperty() : null)
				.flatMap(filter -> currentItemProperty().map(filter::test).map(b -> !b))
				.subscribe(filtered -> pseudoClassStateChanged(FILTERED, filtered != null && filtered));

		setOnMouseEntered(e -> treeViewProperty().flatMap(Node::hoverProperty).getValue());

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
