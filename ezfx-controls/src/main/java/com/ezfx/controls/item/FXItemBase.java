package com.ezfx.controls.item;

import com.ezfx.base.utils.Memoizer;
import com.ezfx.base.utils.ObservableConstant;
import com.ezfx.base.utils.Resources;
import com.ezfx.controls.icons.Icons;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.function.Function;

public class FXItemBase<T, C extends FXItem<?, ?>> implements FXItem<T, C> {
	private static final Image MISSING_ICON = Resources.image(Icons.class, "fx-icons/MissingIcon.png");

	protected final FXItemFactory factory;
	protected final T item;
	protected final ObservableList<C> children = FXCollections.observableArrayList();
	protected final Property<Boolean> visible = new SimpleBooleanProperty();

	protected FXItemBase(FXItemFactory factory, T item) {
		this.factory = factory;
		this.item = item;
	}

	@Override
	public T get() {
		return item;
	}

	@Override
	public ObservableList<? extends C> getChildren() {
		return children;
	}

	@Override
	public Property<Boolean> visibleProperty() {
		return visible;
	}

	private static final Function<Class<?>, Image> iconMemoized = Memoizer.memoize(type -> {
		do {
			Image icon = Resources.image(Icons.class, "fx-icons/%s.png".formatted(type.getSimpleName()));
			if (icon != null) {
				return icon;
			}
			type = type.getSuperclass();
		} while (type != null);
		return MISSING_ICON;
	});

	@Override
	public ObservableValue<Image> getThumbnailIcon() {
		return ObservableConstant.constant(iconMemoized.apply(get().getClass()));
	}

	@Override
	public ObservableValue<String> getPrimaryInfo() {
		return ObservableConstant.constant(get().getClass().getSimpleName());
	}

	@Override
	public ObservableValue<String> getSecondaryInfo() {
		return ObservableConstant.none();
	}

	@Override
	public ObservableValue<String> getTertiaryInfo() {
		return ObservableConstant.none();
	}

}