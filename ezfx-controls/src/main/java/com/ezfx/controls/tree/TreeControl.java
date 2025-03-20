package com.ezfx.controls.tree;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.ezfx.controls.utils.TreeViews.recursiveExpand;

public class TreeControl<T> extends Control {

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	private class DefaultSkin extends SkinBase<TreeControl<?>> {

		private final BorderPane borderPane;

		private final StringEditor filterField;
		private final TreeView<T> treeView;

		public DefaultSkin(TreeControl<T> control) {
			super(control);
			borderPane = new BorderPane();
			borderPane.getStylesheets().setAll(Resources.css(TreeControl.class, "TreeControl.css"));

			filterField = new StringEditor();
			filterField.setPadding(new Insets(4));
			filterField.setPromptText("Filter...");

			treeView = new TreeView<>();
			treeView.showRootProperty().bindBidirectional(control.showRootProperty());
			treeView.cellFactoryProperty().bind(control.cellFactoryProperty().map(cellFactory -> view -> {
				TreeCell<T> cell = cellFactory.call(view);
				cell.setOnMouseEntered(_ -> {
					if (cell.isEmpty() || cell.getItem() == null) return;
					hoveredItem.set(cell.getItem());
				});
				cell.setOnMouseExited(_ -> {
					if (cell.getItem() == null || hoveredItem.getValue() == cell.getItem()) {
						hoveredItem.set(null);
					}
				});
				return cell;
			}));
			treeView.selectionModelProperty()
					.flatMap(SelectionModel::selectedItemProperty)
					.flatMap(TreeItem::valueProperty)
					.subscribe(selectedItem::set);


			borderPane.setTop(filterField);
			borderPane.setCenter(treeView);

			treeView.rootProperty()
					.map(root -> root instanceof FilterableTreeItem<T> filterable ? filterable : null)
					.subscribe(root -> {
						if (root == null) return;
						recursiveExpand(root, 5, true);
						root.predicateProperty().bind(filterField.valueProperty().map(
								filterText -> treeValue -> filterFunction.map(ff -> ff.apply(filterText, treeValue)).getValue()));
					});

			treeView.rootProperty().bind(control.rootProperty().map(root -> new LazyFilterableTreeItem<>(root, getChildrenProvider())));

			getChildren().setAll(borderPane);
		}
	}

	private final Property<T> root = new SimpleObjectProperty<>(this, "root");

	public Property<T> rootProperty() {
		return this.root;
	}

	public T getRoot() {
		return this.rootProperty().getValue();
	}

	public void setRoot(T value) {
		this.rootProperty().setValue(value);
	}

	private final BooleanProperty showRoot = new SimpleBooleanProperty(this, "showRoot", true);

	public BooleanProperty showRootProperty() {
		return this.showRoot;
	}

	public Boolean getShowRoot() {
		return this.showRootProperty().getValue();
	}

	public void setShowRoot(Boolean value) {
		this.showRootProperty().setValue(value);
	}

	// region Functional Properties
	private final Property<Function<T, ObservableList<? extends T>>> childrenProvider = new SimpleObjectProperty<>(this, "childrenProvider");

	public Property<Function<T, ObservableList<? extends T>>> childrenProviderProperty() {
		return this.childrenProvider;
	}

	public Function<T, ObservableList<? extends T>> getChildrenProvider() {
		return this.childrenProviderProperty().getValue();
	}

	public void setChildrenProvider(Function<T, ObservableList<? extends T>> value) {
		this.childrenProviderProperty().setValue(value);
	}

	private final Property<Callback<TreeView<T>, TreeCell<T>>> cellFactory = new SimpleObjectProperty<>(this, "cellFactory");

	public Property<Callback<TreeView<T>, TreeCell<T>>> cellFactoryProperty() {
		return this.cellFactory;
	}

	public Callback<TreeView<T>, TreeCell<T>> getCellFactory() {
		return this.cellFactoryProperty().getValue();
	}

	public void setCellFactory(Callback<TreeView<T>, TreeCell<T>> value) {
		this.cellFactoryProperty().setValue(value);
	}

	private final Property<BiFunction<String, T, Boolean>> filterFunction = new SimpleObjectProperty<>(this, "filterFunction",
			(filter, item) -> item.getClass().getCanonicalName().toLowerCase().contains(filter.toLowerCase()));

	public Property<BiFunction<String, T, Boolean>> filterFunctionProperty() {
		return this.filterFunction;
	}

	public BiFunction<String, T, Boolean> getFilterFunction() {
		return this.filterFunctionProperty().getValue();
	}

	public void setFilterFunction(BiFunction<String, T, Boolean> value) {
		this.filterFunctionProperty().setValue(value);
	}

	// endregion Functional Properties


	// region Read Only Properties
	private final ReadOnlyObjectWrapper<T> selectedItem = new ReadOnlyObjectWrapper<>(this, "selectedItem");

	public ReadOnlyObjectProperty<T> selectedItemProperty() {
		return this.selectedItem.getReadOnlyProperty();
	}

	public T getSelectedItem() {
		return this.selectedItemProperty().getValue();
	}

	private final ReadOnlyObjectWrapper<T> hoveredItem = new ReadOnlyObjectWrapper<>(this, "hoveredItem");

	public ReadOnlyObjectProperty<T> hoveredItemProperty() {
		return this.hoveredItem.getReadOnlyProperty();
	}

	public T getHoveredItem() {
		return this.hoveredItemProperty().getValue();
	}

	// endregion Read Only Properties
}
