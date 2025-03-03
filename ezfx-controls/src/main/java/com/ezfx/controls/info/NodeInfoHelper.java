package com.ezfx.controls.info;

import com.ezfx.base.utils.CachedProxy;
import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

public class NodeInfoHelper implements INodeInfoHelper {

	private static final Image MISSING_ICON = Resources.image(Icons.class, "fx-icons/MissingIcon.png");
	public static final INodeInfoHelper CACHING = CachedProxy.wrap(new NodeInfoHelper(), INodeInfoHelper.class);

	private NodeInfoHelper() {
	}

	public Image icon(Node item) {
		Class<?> type = item.getClass();
		do {
			Image icon = Resources.image(Icons.class, "fx-icons/%s%s.png"
					.formatted(type.getSimpleName(), getOrientationSuffix(item)));
			if (icon != null) {
				return icon;
			}
			type = type.getSuperclass();
		} while (type != null);
		return MISSING_ICON;
	}

	public String typeName(Node node) {
		Class<?> type = node.getClass();
		while (type.isAnonymousClass()) {
			type = type.getSuperclass();
		}
		String simpleName = type.getSimpleName();
		return simpleName.substring(simpleName.lastIndexOf(".") + 1);
	}

	public ObservableValue<String> nodeId(Node node) {
		return node.idProperty().map("#%s"::formatted);
	}

	public ObservableValue<String> styleClass(Node node) {
		return Bindings.createStringBinding(() -> node.getStyleClass().stream()
						.map(".%s"::formatted)
						.collect(joining()),
				node.getStyleClass());
	}

	public ObservableValue<String> info(Node node) {
		String className = node.getClass().getSimpleName();
		ObservableValue<String> nodeId = nodeId(node);
		ObservableValue<String> styleClasses = styleClass(node);
		return Bindings.createStringBinding(() ->
						Stream.of(className, nodeId.getValue(), styleClasses.getValue())
								.filter(Objects::nonNull)
								.filter(not(String::isEmpty))
								.collect(joining(" ")),
				node.idProperty(), node.getStyleClass());
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

}
