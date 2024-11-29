package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.FloatFieldSkin;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.control.Skin;

public class FloatEditor extends ObjectEditor<Float> {

	public FloatEditor() {
		this(new SimpleFloatProperty(0));
	}

	public FloatEditor(FloatProperty property) {
		this(property.asObject());
	}

	public FloatEditor(FloatProperty property, float min, float max) {
		this(property.asObject(), min, max);
	}

	public FloatEditor(Property<Float> property) {
		this(property, Float.MIN_VALUE, Float.MAX_VALUE);
	}

	public FloatEditor(Property<Float> property, float min, float max) {
		super(property);
		setMin(min);
		setMax(max);
	}

	private final FloatProperty max = new SimpleFloatProperty(this, "max");

	public FloatProperty maxProperty() {
		return this.max;
	}

	public Float getMax() {
		return this.maxProperty().get();
	}

	public void setMax(Float value) {
		this.maxProperty().set(value);
	}

	private final FloatProperty min = new SimpleFloatProperty(this, "min");

	public FloatProperty minProperty() {
		return this.min;
	}

	public Float getMin() {
		return this.minProperty().get();
	}

	public void setMin(Float value) {
		this.minProperty().set(value);
	}

	private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(this, "orientation", Orientation.HORIZONTAL);

	public ObjectProperty<Orientation> orientationProperty() {
		return this.orientation;
	}

	public Orientation getOrientation() {
		return this.orientationProperty().get();
	}

	public void setOrientation(Orientation value) {
		this.orientationProperty().set(value);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new FloatFieldSkin(this);
	}

}
