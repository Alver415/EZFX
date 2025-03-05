package com.ezfx.controls.tree;

import com.ezfx.controls.info.NodeInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SceneGraphTreeControl extends TreeControl<Node> {

	public SceneGraphTreeControl() {
		setChildrenProvider(CHILDREN_PROVIDER);
		setFilterFunction(FILTER_FUNCTION);
		setCellFactory(CELL_FACTORY);
	}

	public static final Callback<TreeView<Node>, TreeCell<Node>> CELL_FACTORY = _ -> new NodeTreeCell();

	public static final Function<Node, ObservableList<Node>> CHILDREN_PROVIDER = node -> {
		if (node instanceof Parent parent)
			return parent.getChildrenUnmodifiable();
		if (node instanceof SubScene subScene)
			return FXCollections.observableArrayList(subScene.getRoot());
		return FXCollections.emptyObservableList();
	};

	public static final BiFunction<String, Node, Boolean> FILTER_FUNCTION = (filterText, node) -> {
		if (filterText == null || filterText.isEmpty()) return true;
		filterText = filterText.toLowerCase();
		if (filterText.startsWith("#")) {
			if (node.getId() != null && node.getId().toLowerCase().contains(filterText.substring(1))) {
				return true;
			}
		} else if (filterText.startsWith(".")) {
			for (String styleClass : node.getStyleClass()) {
				if (styleClass.toLowerCase().contains(filterText.substring(1))) {
					return true;
				}
			}
		} else {
			if (node.getClass().getSimpleName().toLowerCase().contains(filterText)) {
				return true;
			}
		}
		return false;
	};

	public static class NodeTreeCell extends TreeCell<Node> {
		public static final PseudoClass FILTERED = PseudoClass.getPseudoClass("filtered");
		private final NodeInfo nodeInfo;

		private final HBox right;
		private final CheckBox visibleCheckBox;
		private final BorderPane borderPane;

		public NodeTreeCell() {
			setContentDisplay(ContentDisplay.LEFT);

			// Build Structure
			nodeInfo = new NodeInfo();
			nodeInfo.subjectProperty().bind(currentItem);

			visibleCheckBox = new CheckBox();
			right = new HBox(visibleCheckBox);
			right.setAlignment(Pos.CENTER_RIGHT);

			borderPane = new BorderPane();
			borderPane.setCenter(nodeInfo);
			borderPane.setRight(right);

			currentItemProperty().subscribe((oldValue, newValue) -> {
				Optional.ofNullable(oldValue).ifPresent(ov -> visibleCheckBox.selectedProperty().unbindBidirectional(ov.visibleProperty()));
				Optional.ofNullable(newValue).ifPresent(nv -> visibleCheckBox.selectedProperty().bindBidirectional(nv.visibleProperty()));
			});
			visibleCheckBox.disableProperty().bind(currentItemProperty().map(Node::visibleProperty).map(Property::isBound));

			treeItemProperty()
					.flatMap(treeItem -> treeItem instanceof FilterableTreeItem<Node> filterableTreeItem ?
							filterableTreeItem.predicateProperty() : null)
					.flatMap(filter -> currentItemProperty().map(filter::test).map(b -> !b))
					.orElse(false)
					.subscribe(filtered -> pseudoClassStateChanged(FILTERED, filtered));
		}

		@Override
		protected void updateItem(Node item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				currentItem.setValue(null);
				setGraphic(null);
			} else {
				currentItem.setValue(item);
				setGraphic(borderPane);
			}
		}


		private final Property<Node> currentItem = new SimpleObjectProperty<>(this, "currentItem");

		public Property<Node> currentItemProperty() {
			return this.currentItem;
		}

		public Node getCurrentItem() {
			return this.currentItemProperty().getValue();
		}

		public void setCurrentItem(Node value) {
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
}
