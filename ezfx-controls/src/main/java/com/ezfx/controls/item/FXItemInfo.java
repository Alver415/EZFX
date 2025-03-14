package com.ezfx.controls.item;

import com.ezfx.base.utils.CachedProxy;
import com.ezfx.base.utils.Memoizer;
import com.ezfx.base.utils.ObservableConstant;
import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.Objects;
import java.util.function.Function;
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
			iconView.imageProperty().bind(control.subjectProperty().flatMap(FXItem::getThumbnailIcon));
			primaryLabel.textProperty().bind(control.subjectProperty().flatMap(FXItem::getPrimaryInfo));
			tertiaryLabel.textProperty().bind(control.subjectProperty().flatMap(FXItem::getSecondaryInfo));
			secondaryLabel.textProperty().bind(control.subjectProperty().flatMap(FXItem::getTertiaryInfo));
		}
	}

}
