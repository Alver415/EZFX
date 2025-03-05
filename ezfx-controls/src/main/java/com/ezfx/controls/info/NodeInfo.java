package com.ezfx.controls.info;

import com.ezfx.base.utils.Resources;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class NodeInfo extends Control {

	private final Property<Node> subject = new SimpleObjectProperty<>(this, "subject");

	public Property<Node> subjectProperty() {
		return this.subject;
	}

	public Node getSubject() {
		return this.subjectProperty().getValue();
	}

	public void setSubject(Node value) {
		this.subjectProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DefaultSkin(this);
	}

	private static class DefaultSkin extends SkinBase<NodeInfo> {

		private static final String STYLE_SHEET = Resources.css(NodeInfo.class, "NodeInfo.css");

		private final ImageView iconView;

		private final Label typeNameLabel;
		private final Label styleClassLabel;
		private final Label nodeIdLabel;

		public DefaultSkin(NodeInfo control) {
			super(control);
			consumeMouseEvents(false);

			// Build Structure
			iconView = new ImageView();
			iconView.getStyleClass().add("icon");
			HBox left = new HBox(iconView);

			typeNameLabel = new Label();
			typeNameLabel.setMinWidth(Region.USE_PREF_SIZE);
			typeNameLabel.getStyleClass().add("java-type");
			nodeIdLabel = new Label();
			nodeIdLabel.setMinWidth(Region.USE_PREF_SIZE);
			nodeIdLabel.getStyleClass().add("node-id");
			styleClassLabel = new Label();
			styleClassLabel.setMinWidth(Region.USE_PREF_SIZE);
			styleClassLabel.getStyleClass().add("style-class");
			HBox center = new HBox(4, typeNameLabel, nodeIdLabel, styleClassLabel);
			center.setAlignment(Pos.CENTER_LEFT);

			BorderPane borderPane = new BorderPane();
			borderPane.getStylesheets().add(STYLE_SHEET);
			borderPane.setLeft(left);
			borderPane.setCenter(center);
			getChildren().setAll(borderPane);

			// Setup Bindings

			ObservableValue<String> override = control.subjectProperty()
					.map(Node::getProperties)
					.flatMap(map -> (ObservableValue<? extends String>) map.get("NAME_OVERRIDE"));
			ObservableValue<String> typeName = control.subjectProperty().map(NodeInfoHelper.CACHING::typeName);

			ObjectBinding<String> nameText = Bindings.createObjectBinding(() -> {
				String o = override.getValue();
				String t = typeName.getValue();
				return o == null ? t : o;
			}, override, typeName);
			iconView.imageProperty().bind(control.subjectProperty().map(NodeInfoHelper.CACHING::icon));
			typeNameLabel.textProperty().bind(nameText);
			nodeIdLabel.textProperty().bind(control.subjectProperty().flatMap(NodeInfoHelper.CACHING::nodeId));
			styleClassLabel.textProperty().bind(control.subjectProperty().flatMap(NodeInfoHelper.CACHING::styleClass));
		}
	}
}
