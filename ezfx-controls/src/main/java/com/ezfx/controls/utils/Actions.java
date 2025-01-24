package com.ezfx.controls.utils;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.controlsfx.control.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface Actions {

	static Action build(String text, String longText, Node graphic, Consumer<ActionEvent> action) {
		return newBuilder()
				.text(text)
				.longText(longText)
				.graphic(graphic)
				.action(action)
				.build();
	}

	static Action build(String text, String longText, Node graphic, Runnable action) {
		return newBuilder()
				.text(text)
				.longText(longText)
				.graphic(graphic)
				.action(action)
				.build();
	}

	static Builder newBuilder() {
		return new Builder();
	}

	class Builder {
		private String text;
		private ObservableValue<String> textBinding;

		private String longText;
		private ObservableValue<String> longTextBinding;

		private Node graphic;
		private ObservableValue<Node> graphicBinding;

		private Consumer<ActionEvent> action;

		private List<String> styleClass;

		public Builder() {
			this.styleClass = new ArrayList<>(List.of("action"));
			this.action = action -> {
			};
		}

		public Builder text(String text) {
			this.text = text;
			return this;
		}

		public Builder text(ObservableValue<String> textBinding) {
			this.textBinding = textBinding;
			return this;
		}

		public Builder longText(String longText) {
			this.longText = longText;
			return this;
		}

		public Builder longText(ObservableValue<String> longTextBinding) {
			this.longTextBinding = longTextBinding;
			return this;
		}

		public Builder graphic(Node graphic) {
			this.graphic = graphic;
			return this;
		}

		public Builder graphic(ObservableValue<Node> graphicBinding) {
			this.graphicBinding = graphicBinding;
			return this;
		}

		public Builder styleClass(String... styleClass) {
			return styleClass(List.of(styleClass));
		}

		public Builder styleClass(List<String> styleClass) {
			this.styleClass.addAll(styleClass);
			return this;
		}

		public Builder action(Consumer<ActionEvent> action) {
			this.action = action;
			return this;
		}

		public Builder action(Runnable action) {
			this.action = _ -> action.run();
			return this;
		}

		public Action build() {
			Action actionObj = new Action(action);
			actionObj.setText(text);
			actionObj.setGraphic(graphic);
			actionObj.setLongText(longText);
			actionObj.getStyleClass().setAll(styleClass);

			if (textBinding != null) {
				actionObj.textProperty().bind(textBinding);
			}
			if (longTextBinding != null) {
				actionObj.longTextProperty().bind(longTextBinding);
			}
			if (graphicBinding != null) {
				actionObj.graphicProperty().bind(graphicBinding);
			}
			return actionObj;
		}
	}
}
