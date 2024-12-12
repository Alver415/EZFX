package com.ezfx.controls.nodetree;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeTreeTableCell extends TreeTableCell<Node, Node> {
	private static final Image MISSING_ICON = Resources.image(Icons.class, "fx-icons/MissingIcon.png");
	private static final Map<Class<?>, Image> ICON_MAP = new HashMap<>();

	public NodeTreeTableCell() {
		setContentDisplay(ContentDisplay.LEFT);
	}

	@Override
	protected void updateItem(Node item, boolean empty) {
		super.updateItem(item, empty);
		if (item == null || empty) {
			clear();
			return;
		}

		// Text is id
		textProperty().bind(Bindings.createStringBinding(() ->
				item.getId() == null || item.getId().isEmpty() ? "" :
						", #%s".formatted(item.getId()), item.idProperty()));

		// Tooltip is CSS classes
		tooltipProperty().bind(Bindings.createObjectBinding(() -> {
			if (item.getStyleClass().isEmpty()) return null;
			Tooltip tooltip = new Tooltip();
			tooltip.textProperty().bind(Bindings.createStringBinding(() ->
							item.getStyleClass().stream()
									.map(".%s"::formatted)
									.collect(Collectors.joining(" ", "Style Classes: ", "")),
					item.getStyleClass()));
			return tooltip;
		}, item.getStyleClass()));

		// Graphic is icon for associated class and the class name
		graphicProperty().bind(Bindings.createObjectBinding(() -> {
			Class<?> clazz = item.getClass();
			while (clazz.isAnonymousClass()) {
				clazz = clazz.getSuperclass();
			}
			String name = clazz.getName();
			name = name.substring(name.lastIndexOf(".") + 1);
			Image icon = getIcon(item);
			Label type = new Label(name);
			type.setStyle("-fx-font-weight:bold;");
			return new HBox(new ImageView(icon), type);
		}));
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
