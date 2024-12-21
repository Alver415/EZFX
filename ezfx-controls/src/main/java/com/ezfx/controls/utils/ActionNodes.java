package com.ezfx.controls.utils;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionGroup;

import java.util.Collection;

import static org.controlsfx.control.action.ActionUtils.*;

public class ActionNodes {


	public static ButtonBar createButtonBar(Collection<? extends Action> actions) {
		return updateButtonBar(new ButtonBar(), actions, ActionTextBehavior.SHOW);
	}

	public static ButtonBar createButtonBar(Collection<? extends Action> actions, ActionTextBehavior textBehavior) {
		return updateButtonBar(new ButtonBar(), actions, textBehavior);
	}

	/**
	 * Takes the provided {@link Collection} of {@link Action} (or subclasses,
	 * such as {@link ActionGroup}) instances and updates a {@link ButtonBar}
	 * populated with appropriate {@link Node nodes} bound to the provided
	 * {@link Action actions}. Previous content of button bar is removed
	 *
	 * @param buttonBar The {@link ButtonBar buttonBar} to update
	 * @param actions   The {@link Action actions} to place on the {@link ButtonBar}.
	 * @return A {@link ButtonBar} that contains {@link Node nodes} which are bound
	 * to the state of the provided {@link Action}
	 */
	public static ButtonBar updateButtonBar(ButtonBar buttonBar, Collection<? extends Action> actions, ActionTextBehavior textBehavior) {
		buttonBar.getButtons().clear();
		for (Action action : actions) {
			if (action instanceof ActionGroup) {
				// no-op
			} else if (action == ACTION_SPAN || action == ACTION_SEPARATOR || action == null) {
				// no-op
			} else {
				Button button = createButton(action, textBehavior);
				int size = 20;
				buttonBar.getButtons().add(button);
			}
		}

		return buttonBar;
	}
}
