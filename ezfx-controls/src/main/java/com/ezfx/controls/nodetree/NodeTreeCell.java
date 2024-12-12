package com.ezfx.controls.nodetree;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.misc.FilterableTreeItem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class NodeTreeCell extends TreeCell<Node> {
	private static final Image MISSING_ICON = Resources.image(Icons.class, "fx-icons/MissingIcon.png");
	private static final String STYLE_SHEET = Resources.css(NodeTreeCell.class, "NodeTreeCell.css");
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

	public NodeTreeCell() {
		getStylesheets().add(STYLE_SHEET);
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
		icon.imageProperty().bind(currentItemProperty().map(NodeTreeCell::getIcon));
		javaTypeLabel.textProperty().bind(currentItemProperty().map(NodeTreeCell::getJavaType));
		nodeIdLabel.textProperty().bind(currentItemProperty().flatMap(NodeTreeCell::getNodeId));
		styleClassLabel.textProperty().bind(currentItemProperty().flatMap(NodeTreeCell::getStyleClass));

		currentItemProperty().subscribe((oldValue, newValue) -> {
			Optional.ofNullable(oldValue).ifPresent(ov -> visibleCheckBox.selectedProperty().unbindBidirectional(ov.visibleProperty()));
			Optional.ofNullable(newValue).ifPresent(nv -> visibleCheckBox.selectedProperty().bindBidirectional(nv.visibleProperty()));
		});
		visibleCheckBox.disableProperty().bind(currentItemProperty().map(Node::visibleProperty).map(Property::isBound));

		treeItemProperty()
				.flatMap(treeItem -> treeItem instanceof FilterableTreeItem<Node> filterableTreeItem ?
						filterableTreeItem.predicateProperty() : null)
				.flatMap(filter -> currentItemProperty().map(filter::test).map(b -> !b))
				.subscribe(filtered -> pseudoClassStateChanged(FILTERED, filtered != null && filtered));
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

	private static ObservableValue<String> getNodeId(Node item) {
		return item.idProperty().map(id -> id == null || id.isEmpty() ? "" : "#%s".formatted(id));
	}

	private static ObservableValue<String> getStyleClass(Node item) {
		return Bindings.createStringBinding(
				() -> item.getStyleClass().stream()
						.map(".%s"::formatted)
						.collect(Collectors.joining(" ")),
				item.getStyleClass());
	}

	private static String getJavaType(Node node) {
		return JAVA_TYPE_CACHE.computeIfAbsent(node.getClass(), clazz -> {
			while (clazz.isAnonymousClass()) {
				clazz = clazz.getSuperclass();
			}
			String simpleName = clazz.getSimpleName();
			return simpleName.substring(simpleName.lastIndexOf(".") + 1);
		});
	}


	private static String getOrientationSuffix(Node item) {
		//Special cases for orientation of Separator and ScrollBar
		if (item instanceof Separator separator) {
			return getOrientationSuffix(separator.getOrientation());
		}
		if (item instanceof ScrollBar scrollBar) {
			return getOrientationSuffix(scrollBar.getOrientation());
		}
		return "";
	}

	private static String getOrientationSuffix(Orientation orientation) {
		if (orientation == null) return "";
		return switch (orientation) {
			case HORIZONTAL -> "-h";
			case VERTICAL -> "-v";
		};
	}

	private static Image getIcon(Node item) {
		Class<?> type = item.getClass();
		return ICON_CACHE.computeIfAbsent(type, clazz -> {
			do {
				Image icon = Resources.image(Icons.class, "fx-icons/%s%s.png"
						.formatted(clazz.getSimpleName(), getOrientationSuffix(item)));
				if (icon != null) {
					return icon;
				}
				clazz = clazz.getSuperclass();
			} while (clazz != null);
			return MISSING_ICON;
		});
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
