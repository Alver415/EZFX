package com.ezfx.controls.utils;

import javafx.scene.control.TextFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface TextFormatters {

	Logger log = LoggerFactory.getLogger(TextFormatters.class);

	UnaryOperator<TextFormatter.Change> FILTER_SHORT = filter(predicate(Short::parseShort));
	UnaryOperator<TextFormatter.Change> FILTER_INTEGER = filter(predicate(Integer::parseInt));
	UnaryOperator<TextFormatter.Change> FILTER_LONG = filter(predicate(Long::parseLong));
	UnaryOperator<TextFormatter.Change> FILTER_FLOAT = filter(predicate(Float::parseFloat));
	UnaryOperator<TextFormatter.Change> FILTER_DOUBLE = filter(predicate(Double::parseDouble));

	private static Predicate<TextFormatter.Change> predicate(Consumer<String> consumer) {
		return change -> {
			try {
				consumer.accept(change.getControlNewText());
				return true;
			} catch (Exception e) {
				return false;
			}
		};
	}

	//TODO: This specific logic should be moved out, probably to NumberEditor class
	static UnaryOperator<TextFormatter.Change> filter(Predicate<TextFormatter.Change> filter) {
		return change -> {
			if (filter.test(change)) return change;
			if (change.getControlNewText().isEmpty()){
				change.setRange(0, change.getControlText().length());
				change.setText("");
				return change;
			}
			if ("-".equals(change.getText())) {

				// if user types or pastes a "-" in middle of current text,
				// toggle sign of value:

				if (change.getControlText().startsWith("-")) {
					// if we currently start with a "-", remove first character:
					change.setText("");
					change.setRange(0, 1);
					// since we're deleting a character instead of adding one,
					// the caret position needs to move back one, instead of
					// moving forward one, so we modify the proposed change to
					// move the caret two places earlier than the proposed change:
					change.setCaretPosition(change.getCaretPosition() - 2);
					change.setAnchor(change.getAnchor() - 2);
				} else {
					// otherwise just insert at the beginning of the text:
					change.setRange(0, 0);
				}
				return change;
			}
			return null;
		};
	}
}
