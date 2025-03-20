package com.ezfx.base.introspector;

import com.ezfx.base.utils.CachedProxy;
import com.ezfx.base.utils.EZFXCollections;
import javafx.beans.NamedArg;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class EZFXIntrospector extends StandardIntrospector {

	public static final Introspector DEFAULT_INTROSPECTOR = CachedProxy.wrap(new EZFXIntrospector(), Introspector.class);


	@Override
	public List<PropertyInfo> getDeclaredPropertyInfo(Type type) {
		List<PropertyInfo> propertyInfo = super.getDeclaredPropertyInfo(type);
		Stream<PropertyInfo> propertyInfoStream = recategorize(propertyInfo);
		Comparator<PropertyInfo> comparator = Comparator.comparing(p -> p.getter().getReturnType().getSimpleName());
		Comparator<PropertyInfo> byTypeThenName = comparator.thenComparing(PropertyInfo::displayName);
		return propertyInfoStream.sorted(byTypeThenName).toList();
	}

	@Override
	public List<PropertyInfo> getPropertyInfo(Type type) {
		List<PropertyInfo> propertyInfo = super.getPropertyInfo(type);
		Stream<PropertyInfo> propertyInfoStream = recategorize(propertyInfo);
		Comparator<PropertyInfo> comparator = Comparator.comparing(p -> p.getter().getReturnType().getSimpleName());
		Comparator<PropertyInfo> byTypeThenName = comparator.thenComparing(PropertyInfo::displayName);
		return propertyInfoStream.sorted(byTypeThenName).toList();
	}

	// TODO: Remove this custom logic. Move to wherever its needed, but not here.
	private static Stream<PropertyInfo> recategorize(List<PropertyInfo> original) {
		return original.stream().map(propertyInfo -> {
			Class<?> clazz = propertyInfo.property().getDeclaringClass();
			Category category = propertyInfo.category();
			int order = propertyInfo.order();
			if (Node.class.equals(clazz)) {
				Class<?> propertyType = propertyInfo.getter().getReturnType();
				boolean isEventHandler = propertyType.isAssignableFrom(EventHandler.class);
				if (propertyInfo.name().startsWith("on") && isEventHandler) {
					order = Integer.MAX_VALUE;
					category = Category.of("Event Handlers", Integer.MAX_VALUE - 1);
				}
				if (propertyInfo.name().startsWith("accessible")) {
					order = Integer.MAX_VALUE;
					category = Category.of("Accessibility", Integer.MAX_VALUE);
				}
			}
			return new PropertyInfo(
					propertyInfo.name(),
					propertyInfo.displayName(),
					category,
					order,
					propertyInfo.property(),
					propertyInfo.setter(),
					propertyInfo.getter());
		});
	}


	private static final Image EMPTY_IMAGE = new WritableImage(16, 16);

	private static final BackgroundFill DEFAULT_BACKGROUND_FILL = new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY);
	private static final BackgroundImage DEFAULT_BACKGROUND_IMAGE = new BackgroundImage(
			new WritableImage(16, 16), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
			BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

	private static final BorderStroke DEFAULT_BORDERSTROKE = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);

	//TODO: Flesh out all JavaFX related defaults
	@SuppressWarnings("unchecked")
	public <S> S getDefaultValueForType(Class<S> type) {
		S defaultValue = super.getDefaultValueForType(type);
		if (defaultValue != null) {
			return defaultValue;
		} else if (type.isArray()) {
			return (S) EZFXCollections.observableObjectArray(type.getComponentType());
		} else if (ListProperty.class.isAssignableFrom(type)) {
			return (S) new SimpleListProperty<>(FXCollections.observableArrayList());
		} else if (ObservableList.class.isAssignableFrom(type)) {
			return (S) FXCollections.observableArrayList();
		} else if (List.class.isAssignableFrom(type)) {
			return (S) FXCollections.observableArrayList();
		} else if (MapProperty.class.isAssignableFrom(type)) {
			return (S) new SimpleMapProperty<>(FXCollections.observableHashMap());
		} else if (ObservableMap.class.isAssignableFrom(type)) {
			return (S) FXCollections.observableHashMap();
		} else if (Map.class.isAssignableFrom(type)) {
			return (S) FXCollections.observableHashMap();
		} else if (Insets.class.equals(type)) {
			return (S) Insets.EMPTY;
		} else if (BackgroundFill.class.equals(type)) {
			return (S) DEFAULT_BACKGROUND_FILL;
		} else if (BackgroundImage.class.equals(type)) {
			return (S) DEFAULT_BACKGROUND_IMAGE;
		} else if (BorderStroke.class.equals(type)) {
			return (S) DEFAULT_BORDERSTROKE;
		} else if (Image.class.equals(type) || Image.class.isAssignableFrom(type) || type.isAssignableFrom(Image.class)) {
			return (S) EMPTY_IMAGE;
		} else if (Point3D.class.equals(type)) {
			return (S) Point3D.ZERO;
		} else if (Point2D.class.equals(type)) {
			return (S) Point2D.ZERO;
		}
		return null;
	}


	/**
	 * This checks for the @NamedArg annotation to derive the parameter's name,
	 * but defaults to the super implementation if not found.
	 */
	@Override
	public String getParameterName(Parameter parameter) {
		return Optional.ofNullable(parameter.getAnnotation(NamedArg.class)).map(NamedArg::value)
				.orElseGet(() -> super.getParameterName(parameter));
	}
}
