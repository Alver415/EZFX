package com.ezfx.controls.editor.code;

import com.ezfx.base.utils.Resources;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * This is an extension of RichTextFX's CodeArea that stretches the line number bar vertically along the entire area.
 * Adapted from: <a href="https://stackoverflow.com/questions/53593467/richtextfx-line-numbers-column-extended-for-entire-text-area">Stack Overflow</a>
 */
public class CodeArea extends org.fxmisc.richtext.CodeArea {

	private static final String STYLE_SHEET = Resources.css(CodeArea.class, "CodeArea.css");
	protected final Rectangle gutterRect = new Rectangle();

	public CodeArea() {
		super();
		getStylesheets().add(STYLE_SHEET);
		gutterRect.heightProperty().bind(this.heightProperty());
		gutterRect.getStyleClass().add("lineno");
	}

	@Override
	protected void layoutChildren() {
		ObservableList<Node> children = getChildren();
		if (children.isEmpty() || children.getFirst() != gutterRect) {
			children.addFirst(gutterRect);
		}
		int index = visibleParToAllParIndex(0);
		double width = getParagraphGraphic(index).prefWidth(-1);
		gutterRect.setWidth(width);
		super.layoutChildren();
	}
}