package com.ezfx.controls.item;

import javafx.stage.Stage;

public class FXStageItem<T extends Stage> extends FXWindowItem<T> {
	protected FXStageItem(FXItemFactory factory, T stage) {
		super(factory, stage);
	}
}