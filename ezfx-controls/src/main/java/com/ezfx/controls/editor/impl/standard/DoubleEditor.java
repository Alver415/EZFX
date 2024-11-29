package com.ezfx.controls.editor.impl.standard;

import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.skin.DoubleFieldSkin;
import com.ezfx.controls.editor.skin.EditorSkin;
import com.ezfx.controls.icons.Icons;
import com.ezfx.controls.misc.RepeatingButton;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;
import static com.ezfx.base.utils.Converters.STRING_TO_DOUBLE;

public class DoubleEditor extends ObjectEditor<Double> {

	public DoubleEditor(DoubleProperty property) {
		this(property.asObject());
	}

	public DoubleEditor(DoubleProperty property, double min, double max) {
		this(property.asObject(), min, max);
	}

	public DoubleEditor(Property<Double> property) {
		this(property, Double.MIN_VALUE, Double.MAX_VALUE);
	}

	public DoubleEditor(Property<Double> property, double min, double max) {
		super(property);
		setMin(min);
		setMax(max);
	}

	private final DoubleProperty max = new SimpleDoubleProperty(this, "max");

	public DoubleProperty maxProperty() {
		return this.max;
	}

	public Double getMax() {
		return this.maxProperty().get();
	}

	public void setMax(Double value) {
		this.maxProperty().set(value);
	}

	private final DoubleProperty min = new SimpleDoubleProperty(this, "min");

	public DoubleProperty minProperty() {
		return this.min;
	}

	public Double getMin() {
		return this.minProperty().get();
	}

	public void setMin(Double value) {
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
		return new DoubleFieldSkin(this);
	}


}
