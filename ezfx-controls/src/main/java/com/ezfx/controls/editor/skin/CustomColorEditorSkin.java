package com.ezfx.controls.editor.skin;

import com.ezfx.base.utils.Colors;
import com.ezfx.base.utils.Converter;
import com.ezfx.base.utils.Converters;
import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.impl.javafx.ColorEditor;
import com.ezfx.controls.editor.impl.standard.IntegerEditor;
import com.ezfx.controls.editor.impl.standard.StringEditor;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Subscription;

import static com.ezfx.base.utils.ComplexBinding.bindBidirectional;

public class CustomColorEditorSkin extends EditorSkin<Editor<Color>, Color> {

	private ColorRectPane colorRectPane;
	private ControlsPane controlsPane;

	private StringEditor webField = null;
	private boolean showOpacitySlider = true;

	public CustomColorEditorSkin(ColorEditor editor) {
		super(editor);
		colorRectPane = new ColorRectPane();
		controlsPane = new ControlsPane();
		HBox hBox = new HBox(controlsPane, colorRectPane);
		hBox.getStyleClass().add("custom-color-dialog");
		getChildren().setAll(hBox);
	}


	// TODO: Move this subscription install/dispose pattern up to EditorSkin and refactor subclasses.
	// Would need to pass the internal skin property up so it can bind valueProperty to it.
	Subscription subscription;

	@Override
	public void install() {
		super.install();
		subscription = bindBidirectional(colorProperty(), valueProperty());
	}

	@Override
	public void dispose() {
		super.dispose();
		subscription.unsubscribe();
	}

	private final Property<Color> color = new SimpleObjectProperty<>(this, "color");

	public Property<Color> colorProperty() {
		return this.color;
	}

	public Color getColor() {
		return this.colorProperty().getValue();
	}

	public void setColor(Color value) {
		this.colorProperty().setValue(value);
	}

	/* ------------------------------------------------------------------------*/

	private class ColorRectPane extends HBox {

		private Pane colorRect;
		private Pane colorBar;
		private Pane colorRectOverlayOne;
		private Pane colorRectOverlayTwo;
		private Region colorRectIndicator;
		private Region colorBarIndicator;

		private boolean changeIsLocal = false;
		private DoubleProperty hue = new SimpleDoubleProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateHSBColor();
					changeIsLocal = false;
				}
			}
		};
		private DoubleProperty sat = new SimpleDoubleProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateHSBColor();
					changeIsLocal = false;
				}
			}
		};
		private DoubleProperty bright = new SimpleDoubleProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateHSBColor();
					changeIsLocal = false;
				}
			}
		};
		private IntegerProperty red = new SimpleIntegerProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateRGBColor();
					changeIsLocal = false;
				}
			}
		};

		private IntegerProperty green = new SimpleIntegerProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateRGBColor();
					changeIsLocal = false;
				}
			}
		};

		private IntegerProperty blue = new SimpleIntegerProperty(-1) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					updateRGBColor();
					changeIsLocal = false;
				}
			}
		};

		private DoubleProperty alpha = new SimpleDoubleProperty(100) {
			@Override
			protected void invalidated() {
				if (!changeIsLocal) {
					changeIsLocal = true;
					Color current = getColor();
					setColor(new Color(
							current.getRed(),
							current.getGreen(),
							current.getBlue(),
							clamp(alpha.get() / 100)));
					changeIsLocal = false;
				}
			}
		};

		private void updateRGBColor() {
			Color newColor = Color.rgb(red.get(), green.get(), blue.get(), clamp(alpha.get() / 100));
			hue.set(newColor.getHue());
			sat.set(newColor.getSaturation() * 100);
			bright.set(newColor.getBrightness() * 100);
			setColor(newColor);
		}

		private void updateHSBColor() {
			Color newColor = Color.hsb(hue.get(), clamp(sat.get() / 100),
					clamp(bright.get() / 100), clamp(alpha.get() / 100));
			red.set(doubleToInt(newColor.getRed()));
			green.set(doubleToInt(newColor.getGreen()));
			blue.set(doubleToInt(newColor.getBlue()));
			setColor(newColor);
		}

		private void colorChanged() {
			if (!changeIsLocal) {
				changeIsLocal = true;
				hue.set(getColor().getHue());
				sat.set(getColor().getSaturation() * 100);
				bright.set(getColor().getBrightness() * 100);
				red.set(doubleToInt(getColor().getRed()));
				green.set(doubleToInt(getColor().getGreen()));
				blue.set(doubleToInt(getColor().getBlue()));
				changeIsLocal = false;
			}
		}

		public ColorRectPane() {
			getStyleClass().add("color-rect-pane");

			colorProperty().addListener((_, _, _) -> colorChanged());

			colorRectIndicator = new Region();
			colorRectIndicator.setId("color-rect-indicator");
			colorRectIndicator.setManaged(false);
			colorRectIndicator.setMouseTransparent(true);
			colorRectIndicator.setCache(true);

			final Pane colorRectOpacityContainer = new StackPane();

			colorRect = new StackPane();
			colorRect.getStyleClass().addAll("color-rect", "transparent-pattern");

			Pane colorRectHue = new Pane();
			hue.map(hue -> Color.hsb(hue.doubleValue(), 1, 1)).map(Background::fill).subscribe(colorRectHue::setBackground);

			colorRectOverlayOne = new Pane();
			colorRectOverlayOne.getStyleClass().add("color-rect");
			colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
					new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
							new Stop(0, Color.rgb(255, 255, 255, 1)),
							new Stop(1, Color.rgb(255, 255, 255, 0))),
					CornerRadii.EMPTY, Insets.EMPTY)));

			EventHandler<MouseEvent> rectMouseHandler = event -> {
				final double x = event.getX();
				final double y = event.getY();
				sat.set(clamp(x / colorRect.getWidth()) * 100);
				bright.set(100 - (clamp(y / colorRect.getHeight()) * 100));
			};

			colorRectOverlayTwo = new Pane();
			colorRectOverlayTwo.getStyleClass().addAll("color-rect");
			colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
					new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
							new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
					CornerRadii.EMPTY, Insets.EMPTY)));
			colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
			colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

			Pane colorRectBlackBorder = new Pane();
			colorRectBlackBorder.setMouseTransparent(true);
			colorRectBlackBorder.getStyleClass().addAll("color-rect", "color-rect-border");

			colorBar = new Pane();
			colorBar.getStyleClass().add("color-bar");
			colorBar.setMinWidth(12);
			colorBar.setBackground(createColorBarBackground());

			colorBarIndicator = new Region();
			colorBarIndicator.setId("color-bar-indicator");
			colorBarIndicator.setMouseTransparent(true);
			colorBarIndicator.setCache(true);

			colorRectIndicator.layoutXProperty().bind(sat.divide(100).multiply(colorRect.widthProperty()));
			colorRectIndicator.layoutYProperty().bind(Bindings.subtract(1, bright.divide(100)).multiply(colorRect.heightProperty()));
			colorBarIndicator.layoutYProperty().bind(hue.divide(360).multiply(colorBar.heightProperty()));
			colorRectOpacityContainer.opacityProperty().bind(alpha.divide(100));

			EventHandler<MouseEvent> barMouseHandler = event -> {
				final double y = event.getY();
				hue.set(clamp(y / colorRect.getHeight()) * 360);
			};

			colorBar.setOnMouseDragged(barMouseHandler);
			colorBar.setOnMousePressed(barMouseHandler);

			colorBar.getChildren().setAll(colorBarIndicator);
			colorRectOpacityContainer.getChildren().setAll(colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
			colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, colorRectIndicator);
			setHgrow(colorRect, Priority.SOMETIMES);
			getChildren().addAll(colorRect, colorBar);
		}

		private void updateValues() {
			if (getColor() == null) {
				setColor(Color.TRANSPARENT);
			}
			changeIsLocal = true;
			//Initialize hue, sat, bright, color, red, green and blue
			hue.set(getColor().getHue());
			sat.set(getColor().getSaturation() * 100);
			bright.set(getColor().getBrightness() * 100);
			alpha.set(getColor().getOpacity() * 100);
			setColor(Color.hsb(hue.get(), clamp(sat.get() / 100), clamp(bright.get() / 100),
					clamp(alpha.get() / 100)));
			red.set(doubleToInt(getColor().getRed()));
			green.set(doubleToInt(getColor().getGreen()));
			blue.set(doubleToInt(getColor().getBlue()));
			changeIsLocal = false;
		}

		@Override
		protected void layoutChildren() {
			super.layoutChildren();

			// to maintain default size
			colorRectIndicator.autosize();
			// to maintain square size
			double size = Math.min(colorRect.getWidth(), colorRect.getHeight());
			colorRect.resize(size, size);
			colorBar.resize(colorBar.getWidth(), size);
		}
	}

	/* ------------------------------------------------------------------------*/

	private class ControlsPane extends VBox {

		private Region newColorRect;
		private StackPane currentAndNewColor;
		private Region currentNewColorBorder;
		private ToggleButton hsbButton;
		private ToggleButton rgbButton;
		private ToggleButton webButton;
		private HBox hBox;

		private Label labels[] = new Label[4];
		private Slider sliders[] = new Slider[4];
		private IntegerEditor fields[] = new IntegerEditor[4];
		private Label units[] = new Label[4];
		private final Property<Number>[] bindedProperties = new Property[4];
		private final Subscription[] subscriptions = new Subscription[4];
		private Region whiteBox;

		private GridPane settingsPane = new GridPane();

		public ControlsPane() {
			getStyleClass().add("controls-pane");

			currentNewColorBorder = new Region();
			currentNewColorBorder.setId("current-new-color-border");

			newColorRect = new Region();
			newColorRect.getStyleClass().add("color-rect");
			newColorRect.setId("new-color");
			colorProperty().map(Background::fill).subscribe(newColorRect::setBackground);

			whiteBox = new Region();
			whiteBox.getStyleClass().add("customcolor-controls-background");

			hsbButton = new ToggleButton("HSB");
			hsbButton.getStyleClass().add("left-pill");
			rgbButton = new ToggleButton("RGB");
			rgbButton.getStyleClass().add("center-pill");
			webButton = new ToggleButton("Web");
			webButton.getStyleClass().add("right-pill");
			final ToggleGroup group = new ToggleGroup();

			hBox = new HBox();
			hBox.setAlignment(Pos.CENTER);
			hBox.getChildren().addAll(hsbButton, rgbButton, webButton);

			Region spacer1 = new Region();
			spacer1.setId("spacer1");
			Region spacer2 = new Region();
			spacer2.setId("spacer2");
			Region leftSpacer = new Region();
			leftSpacer.setId("spacer-side");
			Region rightSpacer = new Region();
			rightSpacer.setId("spacer-side");
			Region bottomSpacer = new Region();
			bottomSpacer.setId("spacer-bottom");

			currentAndNewColor = new StackPane();
			currentAndNewColor.getStyleClass().add("current-new-color-grid");
			currentAndNewColor.getChildren().add(newColorRect);

			settingsPane = new GridPane();
			settingsPane.setId("settings-pane");
			settingsPane.getColumnConstraints().addAll(new ColumnConstraints(),
					new ColumnConstraints(), new ColumnConstraints(),
					new ColumnConstraints(), new ColumnConstraints(),
					new ColumnConstraints());
			settingsPane.getColumnConstraints().get(0).setHgrow(Priority.NEVER);
			settingsPane.getColumnConstraints().get(2).setHgrow(Priority.ALWAYS);
			settingsPane.getColumnConstraints().get(3).setHgrow(Priority.NEVER);
			settingsPane.getColumnConstraints().get(4).setHgrow(Priority.NEVER);
			settingsPane.getColumnConstraints().get(5).setHgrow(Priority.NEVER);
			settingsPane.add(whiteBox, 0, 0, 6, 5);
			settingsPane.add(hBox, 0, 0, 6, 1);
			settingsPane.add(leftSpacer, 0, 0);
			settingsPane.add(rightSpacer, 5, 0);
			settingsPane.add(bottomSpacer, 0, 4);

			webField = new StringEditor();
			webField.getStyleClass().add("web-field");
			bindBidirectional(webField.valueProperty(), colorProperty(),
					Converter.of(string -> Color.web(string, getOpacity()), Colors::toWeb));
			webField.visibleProperty().bind(group.selectedToggleProperty().isEqualTo(webButton));
			settingsPane.add(webField, 2, 1);

			// Color settings Grid Pane
			for (int i = 0; i < 4; i++) {
				labels[i] = new Label();
				labels[i].getStyleClass().add("settings-label");

				sliders[i] = new Slider();

				fields[i] = new IntegerEditor();
				fields[i].getStyleClass().add("color-input-field");

				units[i] = new Label(i == 0 ? "\u00B0" : "%");
				units[i].getStyleClass().add("settings-unit");

				if (i > 0 && i < 3) {
					// first row and opacity labels are always visible
					// second and third row labels are not visible in Web page
					labels[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
				}
				if (i < 3) {
					// sliders and fields shouldn't be visible in Web page
					sliders[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
					fields[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
					units[i].visibleProperty().bind(group.selectedToggleProperty().isEqualTo(hsbButton));
				}
				int row = 1 + i;
				if (i == 3) {
					// opacity row is shifted one gridPane row down
					row++;
				}

				// JDK-8161449 - hide the opacity slider
				if (i == 3 && !showOpacitySlider) {
					continue;
				}

				settingsPane.add(labels[i], 1, row);
				settingsPane.add(sliders[i], 2, row);
				settingsPane.add(fields[i], 3, row);
				settingsPane.add(units[i], 4, row);
			}

			set(3, "Opacity:", 100, colorRectPane.alpha);

			hsbButton.setToggleGroup(group);
			rgbButton.setToggleGroup(group);
			webButton.setToggleGroup(group);
			group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue == null) {
					group.selectToggle(oldValue);
				} else {
					if (newValue == hsbButton) {
						showHSBSettings();
					} else if (newValue == rgbButton) {
						showRGBSettings();
					} else {
						showWebSettings();
					}
				}
			});
			group.selectToggle(hsbButton);

			getChildren().addAll(currentAndNewColor, settingsPane);
		}

		private void showHSBSettings() {
			set(0, "Hue:", 360, colorRectPane.hue);
			set(1, "Saturation:", 100, colorRectPane.sat);
			set(2, "Brightness:", 100, colorRectPane.bright);
		}

		private void showRGBSettings() {
			set(0, "Red:", 255, colorRectPane.red);
			set(1, "Green:", 255, colorRectPane.green);
			set(2, "Blue:", 255, colorRectPane.blue);
		}

		private void showWebSettings() {
			labels[0].setText("Web:");
		}


		private void set(int row, String caption, int maxValue, Property<Number> prop) {
			labels[row].setText(caption);
			if (subscriptions[row] != null) {
				subscriptions[row].unsubscribe();
			}
			sliders[row].setMax(maxValue);
			labels[row].setLabelFor(sliders[row]);
			fields[row].setMax(maxValue);

			subscriptions[row] = Subscription.combine(
					bindBidirectional(fields[row].valueProperty(), prop, Converters.NUMBER_TO_INTEGER.inverted()),
					bindBidirectional(sliders[row].valueProperty(), prop)
			);

			bindedProperties[row] = prop;
		}
	}

	static double clamp(double value) {
		return value < 0 ? 0 : value > 1 ? 1 : value;
	}

	private static Background createColorBarBackground() {
		return new Background(new BackgroundFill(createHueGradient(),
				CornerRadii.EMPTY, Insets.EMPTY));
	}

	private static LinearGradient createHueGradient() {
		double offset;
		Stop[] stops = new Stop[255];
		for (int y = 0; y < 255; y++) {
			offset = 1 - (1.0 / 255) * y;
			int h = (int) ((y / 255.0) * 360);
			stops[y] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
		}
		return new LinearGradient(0f, 1f, 0f, 0f, true, CycleMethod.NO_CYCLE, stops);
	}

	private static int doubleToInt(double value) {
		return (int) (value * 255 + 0.5); // Adding 0.5 for rounding only
	}
}