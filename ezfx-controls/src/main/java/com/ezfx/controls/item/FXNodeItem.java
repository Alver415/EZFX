package com.ezfx.controls.item;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class FXNodeItem<T extends Node> extends FXItemBase<T, FXNodeItem<?>> {

	private final ObservableValue<String> tertiaryInfo;

	protected FXNodeItem(FXItemFactory factory, T node) {
		super(factory, node);
		bindBidirectional(visibleProperty(), node.visibleProperty());
		tertiaryInfo = Bindings.createStringBinding(
				() -> String.join(".", node.getStyleClass()),
				node.getStyleClass())
				.map(string -> !string.isEmpty() ? ".%s".formatted(string) : null);
	}


	@Override
	public ObservableValue<String> getSecondaryInfo() {
		return get().idProperty().map("#%s"::formatted);
	}

	@Override
	public ObservableValue<String> getTertiaryInfo() {
		return tertiaryInfo;
	}

}