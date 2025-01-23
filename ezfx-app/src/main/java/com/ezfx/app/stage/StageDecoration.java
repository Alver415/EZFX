package com.ezfx.app.stage;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.css.converter.PaintConverter;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StageDecoration extends Control {

	public StageDecoration() {
		setBackground(Background.fill(Color.TRANSPARENT));
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new StageDecorationSkin<>(this);
	}

	private final Property<Parent> root = new SimpleObjectProperty<>(this, "root", new StackPane());

	public Property<Parent> rootProperty() {
		return this.root;
	}

	public Parent getRoot() {
		return this.rootProperty().getValue();
	}

	public void setRoot(Parent value) {
		this.rootProperty().setValue(value);
	}

	private final BooleanProperty showTitleBar = new SimpleBooleanProperty(this, "showTitleBar", true);

	public BooleanProperty showTitleBarProperty() {
		return this.showTitleBar;
	}

	public Boolean getShowTitleBar() {
		return this.showTitleBarProperty().getValue();
	}

	public void setShowTitleBar(Boolean value) {
		this.showTitleBarProperty().setValue(value);
	}

	private final StyleableObjectProperty<Paint> sceneFill = new SimpleStyleableObjectProperty<>(StyleableProperties.SCENE_FILL, this, "sceneFill", Color.TRANSPARENT);

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
						return !control.sceneFill.isBound();
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
