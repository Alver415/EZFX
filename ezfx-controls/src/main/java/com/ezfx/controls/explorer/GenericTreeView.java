package com.ezfx.controls.explorer;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import com.ezfx.controls.misc.FilterableTreeItem;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GenericTreeView<A, B> extends Region {
	private static final String STYLE_SHEET = Resources.css(GenericTreeView.class, "GenericTreeView.css");

	private final TreeView<TreeValue<A, B>> treeView;
	private final StringEditor filterField;
	private final BorderPane borderPane;

	public GenericTreeView() {
		getStylesheets().add(STYLE_SHEET);

		filterField = new StringEditor();
		filterField.setPadding(new Insets(4));
		filterField.setPromptText("Filter...");

		treeView = new TreeView<>();
		treeView.setCellFactory(_ -> new GenericTreeCell<>(){
			{
				setOnMouseEntered(_ -> {
					if (isEmpty() || getItem() == null || getItem().getValue() == null) return;
					hoveredItem.setValue(getItem().getValue());
				});
				setOnMouseExited(_ -> {
					if (getItem() == null  || hoveredItem.getValue() == getItem().getValue()) {
						hoveredItem.setValue(null);
					}
				});
			}
		});
		treeView.rootProperty().map(root -> root instanceof FilterableTreeItem<TreeValue<A, B>> filterable ? filterable : null)
				.subscribe(root -> {
					if (root == null) return;
					root.predicateProperty().bind(filterField.valueProperty().map(filterText -> treeValue -> filter(filterText, treeValue)));
				});

		borderPane = new BorderPane();
		borderPane.setTop(filterField);
		borderPane.setCenter(treeView);
		VBox.setVgrow(treeView, Priority.ALWAYS);


		KeyCodeCombination CTRL_F = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (CTRL_F.match(event)) {
				borderPane.setTop(borderPane.getTop() == filterField ? null : filterField);
			}
		});

		getChildren().setAll(borderPane);

		treeView.selectionModelProperty()
				.flatMap(SelectionModel::selectedItemProperty)
				.flatMap(TreeItem::valueProperty)
				.flatMap(TreeValue::valueProperty)
				.subscribe(selectedItem::set);
	}


	private static boolean filter(String filterText, TreeValue<?, ?> value) {
		if (filterText == null || filterText.isEmpty()) return true;
		filterText = filterText.toLowerCase();
		if (filterText.startsWith("#")) {
			String id = value.observableNodeId().getValue();
			if (id != null && id.toLowerCase().contains(filterText.substring(1))) {
				return true;
			}
		} else if (filterText.startsWith(".")) {
			for (String styleClass : value.observableStyleClass()) {
				if (styleClass.toLowerCase().contains(filterText.substring(1))) {
					return true;
				}
			}
		} else {
			if (value.getValue().getClass().getSimpleName().toLowerCase().contains(filterText)) {
				return true;
			}
		}
		return false;
	}

	public void setRoot(A object) {
		treeView.setRoot(new GenericTreeItem<>(object));
		treeView.getRoot().setExpanded(true);
	}

	@Override
	protected void layoutChildren() {
		double width = getWidth() - getInsets().getLeft() - getInsets().getRight();
		double height = getHeight() - getInsets().getTop() - getInsets().getBottom();

		layoutInArea(borderPane, getInsets().getLeft(), getInsets().getTop(), width, height, 0, HPos.CENTER, VPos.CENTER);
	}

	private final ReadOnlyObjectWrapper<A> hoveredItem = new ReadOnlyObjectWrapper<>(this, "hoveredItem");

	public ReadOnlyObjectProperty<A> hoveredItemProperty() {
		return this.hoveredItem.getReadOnlyProperty();
	}

	public A getHoveredItem() {
		return this.hoveredItem.getValue();
	}

	public void setHoveredItem(A value) {
		this.hoveredItem.setValue(value);
	}

	private final ReadOnlyObjectWrapper<A> selectedItem = new ReadOnlyObjectWrapper<>(this, "selectedItem");

	public ReadOnlyObjectProperty<A> selectedItemProperty() {
		return this.selectedItem.getReadOnlyProperty();
	}

	public A getSelectedItem() {
		return this.selectedItem.getValue();
	}

	public void setSelectedItem(A value) {
		this.selectedItem.setValue(value);
	}
}
