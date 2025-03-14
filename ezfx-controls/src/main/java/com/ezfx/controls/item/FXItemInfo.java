package com.ezfx.controls.item;

import com.ezfx.base.utils.CachedProxy;
import com.ezfx.base.utils.ObservableConstant;
import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

public class FXItemInfo extends Control {

	private final Property<FXItem<?, ?>> subject = new SimpleObjectProperty<>(this, "subject");

	public Property<FXItem<?, ?>> subjectProperty() {
		return this.subject;
	}

	public FXItem<?, ?> getSubject() {
		return this.subjectProperty().getValue();
	}

	public void setSubject(FXItem<?, ?> value) {
		this.subjectProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	private static class DefaultSkin extends SkinBase<FXItemInfo> {

		private static final String STYLE_SHEET = Resources.css(FXItemInfo.class, "FXItemInfo.css");

		private final ImageView iconView;

		private final Label primaryLabel;
		private final Label secondaryLabel;
		private final Label tertiaryLabel;

		public DefaultSkin(FXItemInfo control) {
			super(control);
			consumeMouseEvents(false);

			// Build Structure
			iconView = new ImageView();
			iconView.getStyleClass().add("icon");
			HBox left = new HBox(iconView);

			primaryLabel = new Label();
			primaryLabel.setMinWidth(Region.USE_PREF_SIZE);
			primaryLabel.getStyleClass().add("java-type");
			tertiaryLabel = new Label();
			tertiaryLabel.setMinWidth(Region.USE_PREF_SIZE);
			tertiaryLabel.getStyleClass().add("node-id");
			secondaryLabel = new Label();
			secondaryLabel.setMinWidth(Region.USE_PREF_SIZE);
			secondaryLabel.getStyleClass().add("style-class");
			HBox center = new HBox(4, primaryLabel, tertiaryLabel, secondaryLabel);
			center.setAlignment(Pos.CENTER_LEFT);

			BorderPane borderPane = new BorderPane();
			borderPane.getStylesheets().add(STYLE_SHEET);
			borderPane.setLeft(left);
			borderPane.setCenter(center);
			getChildren().setAll(borderPane);

			// Setup Bindings
			iconView.imageProperty().bind(control.subjectProperty().flatMap(FXItemInfoHelper.CACHING::icon));
			primaryLabel.textProperty().bind(control.subjectProperty().flatMap(FXItemInfoHelper.CACHING::primary));
			tertiaryLabel.textProperty().bind(control.subjectProperty().flatMap(FXItemInfoHelper.CACHING::secondary));
			secondaryLabel.textProperty().bind(control.subjectProperty().flatMap(FXItemInfoHelper.CACHING::tertiary));
		}
	}
	public interface IFXItemInfoHelper {
		<T> ObservableValue<Image> icon(FXItem<T, ?> item);
		<T> ObservableValue<String> primary(FXItem<T, ?> item);
		<T> ObservableValue<String> secondary(FXItem<T, ?> item);
		<T> ObservableValue<String> tertiary(FXItem<T, ?> item);
	}

	private static class FXItemInfoHelper implements IFXItemInfoHelper {

		private static final Image MISSING_ICON = Resources.image(Icons.class, "fx-icons/MissingIcon.png");
		private static final IFXItemInfoHelper CACHING = CachedProxy.wrap(new FXItemInfoHelper(), IFXItemInfoHelper.class);

		private FXItemInfoHelper() {
		}

		public <T> ObservableValue<Image> icon(FXItem<T, ?> item) {
			T object = item.get();
			Class<?> type = object.getClass();
			do {
				Image icon = Resources.image(Icons.class, "fx-icons/%s%s.png"
						.formatted(type.getSimpleName(), getOrientationSuffix(object)));
				if (icon != null) {
					return new SimpleObjectProperty<>(icon);
				}
				type = type.getSuperclass();
			} while (type != null);
			return new SimpleObjectProperty<>(MISSING_ICON);
		}

		public <T> ObservableValue<String> primary(FXItem<T, ?> item) {
			T object = item.get();
			Class<?> type = object.getClass();
			while (type.isAnonymousClass()) {
				type = type.getSuperclass();
			}
			String simpleName = type.getSimpleName();
			String substring = simpleName.substring(simpleName.lastIndexOf(".") + 1);
			return new SimpleStringProperty(substring);
		}

		public <T> ObservableValue<String> secondary(FXItem<T, ?> item) {
			T object = item.get();
			if (object instanceof Node node) {
				return node.idProperty().map("#%s"::formatted);
			}
			return ObservableConstant.none();
		}

		public <T> ObservableValue<String> tertiary(FXItem<T, ?> item) {
			T object = item.get();
			if (object instanceof Node node) {
				return Bindings.createStringBinding(() -> node.getStyleClass().stream()
								.map(".%s"::formatted)
								.collect(joining()),
						node.getStyleClass());
			}
			return ObservableConstant.none();
		}

		public <T> ObservableValue<String> info(FXItem<T, ?> item) {
			T object = item.get();
			if (object instanceof Node node) {
				String className = node.getClass().getSimpleName();
				ObservableValue<String> nodeId = secondary(item);
				ObservableValue<String> styleClasses = tertiary(item);
				return Bindings.createStringBinding(() ->
								Stream.of(className, nodeId.getValue(), styleClasses.getValue())
										.filter(Objects::nonNull)
										.filter(not(String::isEmpty))
										.collect(joining(" ")),
						node.idProperty(), node.getStyleClass());
			}
			return ObservableConstant.none();
		}


		private static String getOrientationSuffix(Object object) {
			//Special cases for orientation of Separator and ScrollBar
			if (object instanceof Separator separator) {
				return getOrientationSuffix(separator.getOrientation());
			}
			if (object instanceof ScrollBar scrollBar) {
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

}
