package com.ezfx.controls.item;

import com.ezfx.base.utils.ObservableConstant;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

public class FXNodeItem<T extends Node> extends FXItemBase<T, FXNodeItem<?>> {

	private final StringBinding tertiaryInfo;

	protected FXNodeItem(FXItemFactory factory, T node) {
		super(factory, node);
		tertiaryInfo = Bindings.createStringBinding(
				() -> String.join(".", node.getStyleClass()),
				node.getStyleClass());
	}


	@Override
	public ObservableValue<String> getSecondaryInfo() {
		return get().idProperty();
	}

	@Override
	public ObservableValue<String> getTertiaryInfo() {
		return tertiaryInfo;
	}

}