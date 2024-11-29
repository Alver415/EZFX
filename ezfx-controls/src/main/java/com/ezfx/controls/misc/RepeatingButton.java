package com.ezfx.controls.misc;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;


public class RepeatingButton extends Button {

	public RepeatingButton(Node graphic) {
		super();
		setGraphic(graphic);
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

		ObjectBinding<KeyFrame> keyframeBinding = Bindings.createObjectBinding(
				() -> new KeyFrame(getInterval(), getOnAction()),
				intervalProperty(), onActionProperty());
		keyframeBinding.subscribe(keyFrame -> timeline.getKeyFrames().setAll(keyFrame));

		setOnMousePressed(_ -> timeline.playFromStart());
		setOnMouseReleased(_ -> timeline.stop());
		setOnMouseExited(_ -> timeline.stop());
	}

	private final Property<Duration> interval = new SimpleObjectProperty<>(this, "interval", Duration.millis(100));

	public Property<Duration> intervalProperty() {
		return this.interval;
	}

	public Duration getInterval() {
		return this.intervalProperty().getValue();
	}

	public void setInterval(Duration value) {
		this.intervalProperty().setValue(value);
	}
}
