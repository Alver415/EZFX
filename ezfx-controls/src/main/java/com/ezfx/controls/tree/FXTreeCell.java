package com.ezfx.controls.tree;


import com.ezfx.controls.info.FXItem;
import com.ezfx.controls.info.FXItemInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class FXTreeCell extends TreeCell<FXItem<?, ?>> {
	public static final PseudoClass FILTERED = PseudoClass.getPseudoClass("filtered");
	private final FXItemInfo itemInfo;

	private final HBox right;
	private final CheckBox visibleCheckBox;
	private final BorderPane borderPane;

	public FXTreeCell() {
		setContentDisplay(ContentDisplay.LEFT);

		// Build Structure
		itemInfo = new FXItemInfo();
		itemInfo.subjectProperty().bind(currentItem);

		visibleCheckBox = new CheckBox();
		right = new HBox(visibleCheckBox);
		right.setAlignment(Pos.CENTER_RIGHT);

		borderPane = new BorderPane();
		borderPane.setCenter(itemInfo);
		borderPane.setRight(right);

		currentItemProperty().subscribe((oldValue, newValue) -> {
			Optional.ofNullable(oldValue).ifPresent(ov -> visibleCheckBox.selectedProperty().unbindBidirectional(ov.visibleProperty()));
			Optional.ofNullable(newValue).ifPresent(nv -> visibleCheckBox.selectedProperty().bindBidirectional(nv.visibleProperty()));
		});
		visibleCheckBox.disableProperty().bind(currentItemProperty().map(FXItem::visibleProperty).map(Property::isBound));

		treeItemProperty()
				.flatMap(treeItem -> treeItem instanceof FilterableTreeItem<FXItem<?, ?>> filterableTreeItem ?
						filterableTreeItem.predicateProperty() : null)
				.flatMap(filter -> currentItemProperty().map(filter::test).map(b -> !b))
				.orElse(false)
				.subscribe(filtered -> pseudoClassStateChanged(FILTERED, filtered));
	}

	@Override
	protected void updateItem(FXItem<?, ?> item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			currentItem.setValue(null);
			setGraphic(null);
		} else {
			currentItem.setValue(item);
			setGraphic(borderPane);
		}
	}


	private final Property<FXItem<?, ?>> currentItem = new SimpleObjectProperty<>(this, "currentItem");

	public Property<FXItem<?, ?>> currentItemProperty() {
		return this.currentItem;
	}

	public FXItem<?, ?> getCurrentItem() {
		return this.currentItemProperty().getValue();
	}

	public void setCurrentItem(FXItem<?, ?> value) {
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
