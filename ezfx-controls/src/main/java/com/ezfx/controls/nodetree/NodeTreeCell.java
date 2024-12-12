package com.ezfx.controls.nodetree;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.misc.FilterableTreeItem;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeTreeCell extends TreeCell<Node> {
	private static final Image MISSING_ICON = Resources.image(Icons.class, "fx-icons/MissingIcon.png");
	private static final String STYLE_SHEET = Resources.css(NodeTreeCell.class, "NodeTreeCell.css");
	private static final Map<Class<?>, Image> ICON_MAP = new HashMap<>();

	public static final PseudoClass FILTERED = PseudoClass.getPseudoClass("filtered");

	public NodeTreeCell() {
		getStylesheets().add(STYLE_SHEET);
		setContentDisplay(ContentDisplay.LEFT);
	}

	@Override
	protected void updateItem(Node item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			clear();
			return;
		}
		Class<?> clazz = item.getClass();
		boolean isAnonymous = clazz.isAnonymousClass();
		while (clazz.isAnonymousClass()) {
			clazz = clazz.getSuperclass();
		}
		String name = clazz.getName();
		name = name.substring(name.lastIndexOf(".") + 1);
		Image icon = getIcon(item);
		Label type = new Label(name);
		type.getStyleClass().add("java-type");
		if (isAnonymous) {
			type.setTooltip(new Tooltip(item.getClass().getName()));
		}

		Label id = new Label();
		id.getStyleClass().add("fx-id");
		id.textProperty().bind(Bindings.createStringBinding(() ->
				item.getId() == null || item.getId().isEmpty() ?
						"" : " #%s".formatted(item.getId()), item.idProperty()));

		CheckBox visible = new CheckBox();
		visible.selectedProperty().bindBidirectional(item.visibleProperty());

		Label styleClasses = new Label();
		styleClasses.getStyleClass().add("style-class");
		styleClasses.textProperty().bind(
				Bindings.createStringBinding(() -> item.getStyleClass().stream()
								.map(".%s"::formatted)
								.collect(Collectors.joining(", ")),
						item.getStyleClass()));

		HBox hbox = new HBox(new ImageView(icon), type, id, styleClasses);
		BorderPane borderPane = new BorderPane(hbox);
		borderPane.setRight(visible);

		setGraphic(borderPane);

		if (getTreeItem() instanceof FilterableTreeItem<Node> filterableTreeItem) {
			filterableTreeItem.predicateProperty().subscribe(predicate -> {
				boolean matched = predicate == null || predicate.test(item);
				pseudoClassStateChanged(FILTERED, !matched);
			});
		}
	}

	private void clear() {
		textProperty().unbind();
		graphicProperty().unbind();
		tooltipProperty().unbind();
		setText(null);
		setGraphic(null);
		setTooltip(null);
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
		return ICON_MAP.computeIfAbsent(type, clazz -> {
			Image icon;
			do {
				icon = Resources.image(Icons.class, "fx-icons/%s%s.png".formatted(clazz.getSimpleName(), getOrientationSuffix(item)));
				clazz = clazz.getSuperclass();
			} while (icon == null && clazz != null);

			if (icon == null) {
				return MISSING_ICON;
			}
			return icon;
		});
	}
}
