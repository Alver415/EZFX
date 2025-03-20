package com.ezfx.fxml;

import com.ezfx.base.introspector.EZFXIntrospector;
import com.ezfx.base.introspector.PropertyInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.skin.LabelSkin;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class FXMLSerializer extends StdSerializer<Node> {

	private static final Logger log = LoggerFactory.getLogger(FXMLSerializer.class);
	private static final Map<Class<?>, Object> DEFAULT_OBJECTS = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Map<String, Object>> DEFAULT_PROPERTY_VALUES = new ConcurrentHashMap<>();

	static {
		// com.sun.javafx.scene.control.LabeledText;
		Label label = new Label();
		label.setSkin(new LabelSkin(label));
		Node labelText = label.getChildrenUnmodifiable().getFirst();
		DEFAULT_OBJECTS.putIfAbsent(labelText.getClass(), labelText);
	}

	FXMLSerializer() {
		super(Node.class);
	}

	@Override
	public void serialize(Node node, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
		Serializer generator = new Serializer(node, (ToXmlGenerator) jsonGenerator, provider);
		try {
			generator.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class Serializer {
		private final Node node;
		private final ToXmlGenerator actualGenerator;
		private final SerializerProvider provider;
		private ToXmlGenerator generator;

		private final Set<Class<?>> classes = new HashSet<>();

		public Serializer(Node node, ToXmlGenerator generator, SerializerProvider provider) {
			this.node = node;
			this.actualGenerator = generator;
			this.provider = provider;
		}

		public void run() throws Exception {
			//TODO: Clean up this dry run. Shouldn't need to execute the entire serialization twice.
			//Dry run against dummy generator to discover list of classes needed for import.
			this.generator = (ToXmlGenerator) actualGenerator.getCodec().getFactory().createGenerator(new StringWriter());
			serializeRoot(node);
			this.generator = actualGenerator;

			//Start Document
			generator.getStaxWriter().writeStartDocument();
			generator.writeRaw(System.lineSeparator());
			generator.writeRaw(System.lineSeparator());

			//Imports
			List<String> packages = classes.stream()
					.map(Class::getName)
					.distinct()
					.sorted()
					.toList();
			for (String packageName : packages) {
				generator.writeRaw("<?import %s?>".formatted(packageName));
				generator.writeRaw(System.lineSeparator());
			}
			generator.writeRaw(System.lineSeparator());

			serializeRoot(node);
		}

		protected void serializeRoot(Object object) throws Exception {
			generator.setNextName(createType(object.getClass()));
			generator.writeStartObject();

			generator.setNextIsAttribute(true);
			generator.writeStringField("xmlns", "http://javafx.com/javafx");
			generator.writeStringField("xmlns:fx", "http://javafx.com/javafx");

			serializeObjectInternal(object);

			generator.writeEndObject();
		}

		protected void serializeObjectInternal(Object object) throws Exception {
			Map<String, Object> childElements = new LinkedHashMap<>();

			generator.setNextIsAttribute(true);
			Class<?> clazz = object.getClass();
			for (PropertyInfo propertyInfo : EZFXIntrospector.DEFAULT_INTROSPECTOR.getPropertyInfo(clazz)) {
				String name = propertyInfo.name();
				//Ignore if not readable and writable.
				if (propertyInfo.setter() == null || propertyInfo.getter() == null) {
					log.debug("Skipping attribute {} because it is not both readable and writable", name);
					continue;
				}
				//Ignore if null.
				Method getter = propertyInfo.getter();
				if (!getter.canAccess(object)) {
					log.debug("Skipping attribute {} because can't access.", name);
					continue;
				}
				Object value = getter.invoke(object);
				if (value == null) {
					log.debug("Skipping attribute {} because null value.", name);
					continue;
				}

				Class<?> type = value.getClass();
				if (isSimpleType(type) || type.isEnum()) {
					//Ignore if same as default value.
					Object defaultValue = getDefaultValue(object, propertyInfo);
					if (defaultValue != null && Objects.equals(value, defaultValue)) {
						log.debug("Skipping attribute {} because the value is the same as the default value: {}", name, value);
						continue;
					}

					//Write simple string field.
					generator.writeStringField(name, String.valueOf(value));
				} else if (Node.class.isAssignableFrom(type)) {
					//Track complex elements.
					childElements.put(name, value);
				} else if (Image.class.isAssignableFrom(type) && value instanceof Image image) {
					startWrappedElement(name);
					generator.setNextName(createType(image.getClass()));
					generator.writeStartObject();
					generator.setNextIsAttribute(true);
					generator.writeStringField("url", image.getUrl());
					generator.writeEndObject();
					endWrappedElement();
				} else if (Color.class.isAssignableFrom(type) ||
						Font.class.isAssignableFrom(type) ||
						Point3D.class.isAssignableFrom(type) ||
						Insets.class.isAssignableFrom(type) ||
						Background.class.isAssignableFrom(type) ||
						Border.class.isAssignableFrom(type) ||
						Insets.class.isAssignableFrom(type)) {
					// TODO: Actually implement all the listed serialization
					//Ignore if same as default value.
					Object defaultValue = getDefaultValue(object, propertyInfo);
					if (defaultValue != null && Objects.equals(value, defaultValue)) {
						log.debug("Skipping child element {} because the value is the same as the default value: {}", name, value);
						continue;
					}

					//Track complex elements.
					childElements.put(name, value);
				} else if ("eventDispatcher".equals(name)) {
					//TODO: Cleanup
					log.debug("Failed to serialize attribute {}.{} because of unimplemented type: {}", clazz.getSimpleName(), name, type);
				} else {
					log.warn("Failed to serialize attribute {}.{} because of unimplemented type: {}", clazz.getSimpleName(), name, type);
				}
			}
			if (object instanceof Node node) {
				Parent parent = node.getParent();
				if (parent != null) {
					Class<? extends Parent> parentClass = parent.getClass();
					Method[] methods = parentClass.getMethods();
					for (Method method : methods) {
						if (method.getName().startsWith("get")) {
							String propertyName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
							if (Modifier.isStatic(method.getModifiers())) {
								Parameter[] parameters = method.getParameters();
								if (parameters.length == 1) {
									if (parameters[0].getType() == Node.class) {
										Object value = method.invoke(null, object);
										if (value != null) {
											generator.writeStringField(parentClass.getSimpleName() + "." + propertyName, String.valueOf(value));
										}
									}
								}
							}
						}
					}
				}
			}

			for (Map.Entry<String, Object> entry : childElements.entrySet()) {
				startWrappedElement(entry.getKey());
				serializeObject(entry.getValue());
				endWrappedElement();
			}

			if (object instanceof Pane parent) {
				Method getChildrenMethod = parent.getClass().getMethod("getChildrenUnmodifiable");

				if (Arrays.stream(getChildrenMethod.getAnnotations()).anyMatch(annotation -> annotation instanceof FXMLIgnore)) {
					log.debug("Skipping getChildren because FXMLIgnore annotation");
				} else if (!Modifier.isPublic(getChildrenMethod.getModifiers())) {
					log.debug("Skipping getChildren because method is not public");
				} else if (parent instanceof BorderPane borderPane) {
					log.debug("Skipping getChildren because BorderPane uses top/right/bottom/left/center");
				} else {
					startWrappedElement("children");
					for (Node child : parent.getChildrenUnmodifiable()) {
						if (!childElements.containsValue(child)) {
							serializeObject(child);
						}
					}
					endWrappedElement();
				}
			}
		}

		protected void startWrappedElement(String name) throws IOException {
			generator.writeFieldName(name);
			generator.writeStartObject();
			generator.writeFieldName(name);
			generator.writeStartArray();
		}

		protected void endWrappedElement() throws IOException {
			generator.writeEndArray();
			generator.writeEndObject();
		}

		protected void serializeObject(Object object) throws Exception {
			generator.setNextName(createType(object.getClass()));
			generator.writeStartObject();
			serializeObjectInternal(object);
			generator.writeEndObject();
		}

		protected QName createType(Class<?> clazz) {
			classes.add(clazz);
			return new QName(clazz.getSimpleName());
		}
	}

	protected static boolean isSimpleType(Class<?> type) {
		return SIMPLE_TYPES.contains(type);
	}

	protected static final Set<Class<?>> SIMPLE_TYPES = Set.of(
			String.class,
			Double.class,
			Float.class,
			Long.class,
			Integer.class,
			Short.class,
			Character.class,
			Byte.class,
			Boolean.class);

	private static Object getDefaultValue(Object object, PropertyInfo propertyInfo) {
		try {
			return DEFAULT_PROPERTY_VALUES.computeIfAbsent(object.getClass(), _ -> new ConcurrentHashMap<>())
					.computeIfAbsent(propertyInfo.name(), _ -> getDefaultPropertyValue(object.getClass(), propertyInfo));
		} catch (Exception e) {
			log.debug("Failed to get default value: %s".formatted(propertyInfo), e);
			return null;
		}
	}

	private static Object getDefaultPropertyValue(Class<?> clazz, PropertyInfo propertyInfo) {
		try {
			Object defaultInstance = getDefaultInstance(clazz);
			return propertyInfo.getter().invoke(defaultInstance);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private static Object getDefaultInstance(Class<?> clazz) {
		return DEFAULT_OBJECTS.computeIfAbsent(clazz, _ -> createDefaultInstance(clazz));
	}

	private static Object createDefaultInstance(Class<?> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (InstantiationException |
		         IllegalAccessException |
		         InvocationTargetException |
		         NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

}
