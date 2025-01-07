package com.ezfx.controls.misc;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ProgressView extends Control {

	public ProgressView(){
		this(null);
	}
	public ProgressView(String text){
		setText(text);
	}

	private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress");

	public DoubleProperty progressProperty() {
		return this.progress;
	}

	public Double getProgress() {
		return this.progressProperty().getValue();
	}

	public void setProgress(Double value) {
		this.progressProperty().setValue(value);
	}
	private final StringProperty text = new SimpleStringProperty(this, "text");

	public StringProperty textProperty() {
		return this.text;
	}

	public String getText() {
		return this.textProperty().getValue();
	}

	public void setText(String value) {
		this.textProperty().setValue(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ProgressViewSkin(this);
	}

	public static class ProgressViewSkin extends SkinBase<ProgressView> {

		protected ProgressViewSkin(ProgressView control) {
			super(control);
			Text text = new Text();
			ProgressBar progressBar = new ProgressBar();
			StackPane stackPane = new StackPane(progressBar, text);

			text.textProperty().bind(control.textProperty());
			progressBar.progressProperty().bind(control.progressProperty());

			getChildren().setAll(stackPane);
		}
	}
}
