package com.ezfx.controls.editor.introspective;

import com.ezfx.controls.editor.Editor;
import com.ezfx.controls.editor.EditorAction;
import com.ezfx.controls.editor.EditorFactory;
import com.ezfx.controls.editor.ObjectEditor;
import com.ezfx.controls.editor.option.*;
import com.ezfx.controls.icons.Icons;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.stage.PopupWindow;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.ezfx.controls.editor.EditorFactory.DEFAULT_FACTORY;
import static com.ezfx.controls.editor.introspective.IntrospectorFX.DEFAULT_INTROSPECTOR;
import static java.lang.reflect.Modifier.*;

@SuppressWarnings("unchecked")
public class IntrospectingEditor<T> extends ObjectEditor<T> implements DelegatingEditor<T> {

	private static final Logger log = LoggerFactory.getLogger(IntrospectingEditor.class);

	public IntrospectingEditor(Class<T> type) {
		this(new SimpleObjectProperty<>(), type);
	}

	public IntrospectingEditor(T target) {
		this(new SimpleObjectProperty<>(target), (Class<T>) target.getClass());
	}

	public IntrospectingEditor(
			Property<T> property,
			Class<T> type) {
		this(property, type, DEFAULT_INTROSPECTOR, DEFAULT_FACTORY);
	}

	private final Binding<List<FieldOption<T>>> fieldOptions;
	private final Binding<List<MethodOption<T>>> methodOptions;
	private final Binding<List<? extends ConstructorOption<? extends T>>> constructorOptions;

	public IntrospectingEditor(
			Property<T> property,
			Class<T> type,
			Introspector introspector,
			EditorFactory factory) {
		super(property);
		setType(type);
		setIntrospector(introspector);
		setEditorFactory(factory);

		property.subscribe(value -> {
			if ( value != null && !getIntrospector().getPropertyInfo(value.getClass()).isEmpty()){
				setDelegate(new IntrospectingPropertiesEditor<>(value));
			}
		});

		fieldOptions = EasyBind.combine(introspectorProperty(), typeProperty(), Introspector::getFields)
				.map(fields -> fields.stream()
						.sorted(Comparator.comparing(Field::getName))
						.filter(field -> checkModifiers(field.getModifiers(), PUBLIC, STATIC, FINAL))
						.filter(field -> field.getType().isAssignableFrom(type))
						.map(this::build)
						.toList());

		methodOptions = EasyBind.combine(introspectorProperty(), typeProperty(), Introspector::getMethods)
				.map(methods -> methods.stream()
						.sorted(Comparator.comparing(Method::getParameterCount))
						.filter(method -> checkModifiers(method.getModifiers(), PUBLIC, STATIC))
						.filter(method -> method.getReturnType().isAssignableFrom(type))
						.map(this::build)
						.toList());

		MonadicBinding<List<? extends Constructor<? extends T>>> combine =
				EasyBind.combine(introspectorProperty(), typeProperty(), Introspector::getConstructors);
		constructorOptions = combine.map(constructors -> constructors.stream()
				.sorted(Comparator.comparing(Constructor::getParameterCount))
				.filter(constructor -> checkModifiers(constructor.getModifiers(), PUBLIC))
				.filter(constructor -> type.isAssignableFrom(constructor.getDeclaringClass()))
				.map(this::build)
				.toList());

		ObjectBinding<ObservableList<ValueOption<T>>> valueOptions = Bindings.createObjectBinding(() -> {
			ObservableList<ValueOption<T>> list = FXCollections.observableArrayList();
			List<FieldOption<T>> value = fieldOptions.getValue();
			list.addAll(value);
			return list;
		}, fieldOptions);

		ObjectBinding<ObservableList<BuilderOption<T>>> builderOptions = Bindings.createObjectBinding(() -> {
			ObservableList<BuilderOption<T>> list = FXCollections.observableArrayList();
			list.addAll(methodOptions.getValue());
			list.addAll((Collection<? extends BuilderOption<T>>) constructorOptions.getValue());
			return list;
		}, methodOptions, constructorOptions);

		EditorAction clear = new EditorAction();
		clear.setName("Set Value");
		clear.setIcon(Icons.PLUS);
		clear.setAction(() -> {

			Menu valueMenu = new Menu("Value");
			valueOptions.map(list -> list.stream().map(option -> {
				MenuItem menuItem = new MenuItem();
				menuItem.setText(option.getName());
				menuItem.setOnAction(_ -> property.setValue(option.getValue()));
				return menuItem;
			}).toList()).subscribe(items -> valueMenu.getItems().setAll(items));

			Menu builderMenu = new Menu("Builders");
			builderOptions.map(list -> list.stream().map(option -> {
				MenuItem menuItem = new MenuItem();
				menuItem.setText(option.getName());
				menuItem.setOnAction(_ -> {
					if (option instanceof ConstructorOption<T> constructorOption){
						if (constructorOption.getConstructor().getParameterCount() == 0) {
							property.setValue(option.buildEditor().getValue());
							return;
						}
					}
					BuilderDialog<T> dialog = new BuilderDialog<>(property, option.buildEditor());
					dialog.setHeaderText(option.getType().getSimpleName());
					dialog.show();
				});
				return menuItem;
			}).toList()).subscribe(items -> builderMenu.getItems().setAll(items));

			ContextMenu contextMenu = new ContextMenu();
			contextMenu.getItems().setAll(valueMenu, builderMenu);

			Bounds bounds = localToScreen(getBoundsInLocal());
			double x = bounds.getCenterX();
			double y = bounds.getCenterY();
			contextMenu.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
			contextMenu.show(this, x, y);
		});
		actionsProperty().add(clear);
	}


	private FieldOption<T> build(Field field) {
		String typeName = field.getType().getSimpleName();
		String fieldName = field.getName();
		String name = "%s.%s".formatted(typeName, fieldName);
		return new FieldOption<>(name, getType(), field);
	}

	private MethodOption<T> build(Method method) {
		Class<?> returnType = method.getReturnType();
		String typeName = returnType.getSimpleName();
		String methodName = method.getName();
		String parameterTypeNames = Arrays.stream(method.getParameters())
				.map(getIntrospector()::getParameterTypeName)
				.collect(Collectors.joining(", "));
		String name = "%s.%s(%s)".formatted(typeName, methodName, parameterTypeNames);
		return new MethodOption<>(name, getType(), method);
	}

	private <R extends T> ConstructorOption<R> build(Constructor<R> constructor) {
		Class<R> type = constructor.getDeclaringClass();
		String typeName = type.getSimpleName();
		String parameterTypeNames = Arrays.stream(constructor.getParameters())
				.map(getIntrospector()::getParameterTypeName)
				.collect(Collectors.joining(", "));
		String name = "new %s(%s)".formatted(typeName, parameterTypeNames);
		return new ConstructorOption<>(name, type, constructor);
	}


	@Override
	protected Skin<?> createDefaultSkin() {
		return new DelegatingEditorSkin<>(this);
	}

	// endregion Introspection


	private final ObjectProperty<Class<T>> type = new SimpleObjectProperty<>(this, "type");

	public ObjectProperty<Class<T>> typeProperty() {
		return this.type;
	}

	public Class<T> getType() {
		return this.typeProperty().get();
	}

	public void setType(Class<T> value) {
		this.typeProperty().set(value);
	}

	private final ObjectProperty<Introspector> introspector = new SimpleObjectProperty<>(this, "introspector");

	public ObjectProperty<Introspector> introspectorProperty() {
		return this.introspector;
	}

	public Introspector getIntrospector() {
		return this.introspectorProperty().get();
	}

	public void setIntrospector(Introspector value) {
		this.introspectorProperty().set(value);
	}

	private final ObjectProperty<EditorFactory> editorFactory = new SimpleObjectProperty<>(this, "editorFactory");

	public ObjectProperty<EditorFactory> editorFactoryProperty() {
		return this.editorFactory;
	}

	public EditorFactory getEditorFactory() {
		return this.editorFactoryProperty().get();
	}

	public void setEditorFactory(EditorFactory value) {
		this.editorFactoryProperty().set(value);
	}


	private static <T> T getField(Field field) {
		try {
			return (T) field.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean checkModifiers(Member member, int... modifiers) {
		return checkModifiers(member.getModifiers(), modifiers);
	}

	public boolean checkModifiers(int modifier, int... modifiers) {
		return Arrays.stream(modifiers)
				.mapToObj(m -> (modifier & m) != 0)
				.reduce((a, b) -> a && b)
				.orElse(true);
	}

	private final Property<Editor<T>> delegate = new SimpleObjectProperty<>(this, "delegate");

	@Override
	public Property<Editor<T>> delegateProperty() {
		return this.delegate;
	}

	public Editor<T> getDelegate() {
		return this.delegateProperty().getValue();
	}

	public void setDelegate(Editor<T> value) {
		this.delegateProperty().setValue(value);
	}


}
