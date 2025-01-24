package com.ezfx.app.stage;

import com.ezfx.base.utils.Resources;
import com.ezfx.controls.editor.introspective.ActionIntrospector;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionProxy;
import org.controlsfx.control.action.ActionUtils;

import java.util.List;
import java.util.function.Function;

public class WindowsStageDecorationSkin<T extends StageDecoration> extends StageDecorationSkin<T> {
	private static final String STYLE_SHEET = Resources.css(WindowsStageDecorationSkin.class, "WindowsStageDecorationSkin.css");

	public WindowsStageDecorationSkin(T control) {
		super(control);
		window.getStylesheets().setAll(STYLE_SHEET);
		buttonBar.getChildren().setAll(minimizeButton, resizeButton, closeButton);
	}
}
