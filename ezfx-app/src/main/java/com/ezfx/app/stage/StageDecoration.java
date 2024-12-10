package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.css.converter.PaintConverter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StageDecoration extends Control {

	private final Property<Parent> root = new SimpleObjectProperty<>(this, "root");

	public Property<Parent> rootProperty() {
		return this.root;
	}

	public Parent getRoot() {
		return this.rootProperty().getValue();
	}

	public void setRoot(Parent value) {
		this.rootProperty().setValue(value);
	}

	public StageDecoration() {
		getStylesheets().add(Resources.css(StageDecorationSkin.class, "StageDecoration.css"));
		getStyleClass().add("stage-decoration");
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new StageDecorationSkin(this);
	}

	private StyleableObjectProperty<Paint> sceneFill = new SimpleStyleableObjectProperty<>(StyleableProperties.SCENE_FILL, this, "sceneFill");

	public Paint getSceneFill() {
		return sceneFill.get();
	}

	public void setSceneFill(Paint value) {
		sceneFill.set(value);
	}

	public StyleableObjectProperty<Paint> sceneFillProperty() {
		return sceneFill;
	}

	// Add a StyleableProperty to the control's styleable properties list
	private static class StyleableProperties {
		private static final CssMetaData<StageDecoration, Paint> SCENE_FILL =
				new CssMetaData<>("-fx-scene-fill", PaintConverter.getInstance()) {
					@Override
					public boolean isSettable(StageDecoration control) {
						return control.sceneFill == null || !control.sceneFill.isBound();
					}

					@Override
					public StyleableProperty<Paint> getStyleableProperty(StageDecoration control) {
						return control.sceneFillProperty();
					}
				};

		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

		static {
			List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
			styleables.add(SCENE_FILL);
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return StyleableProperties.STYLEABLES;
	}

}
