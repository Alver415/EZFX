package com.ezfx.fxml;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

class FXMLSerializer extends StdSerializer<Node> {

	private static final Logger log = LoggerFactory.getLogger(FXMLSerializer.class);

	protected ToXmlGenerator xml;
	FXMLSerializer() {
		super(Node.class);
	}
	FXMLSerializer(Class<Node> t) {
		super(t);
	}

	@Override
	public void serialize(Node node, JsonGenerator generator, SerializerProvider provider) {
		try {
			xml = (ToXmlGenerator) generator;

			recursiveVisitChildren(node, v -> classes.add(v.getClass()));
			writeStartDocument();
			writeImports();
			serializeRoot(node);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void serializeRoot(Object object) throws Exception {
		xml.setNextName(createType(object));
		xml.writeStartObject();

		xml.setNextIsAttribute(true);
		xml.writeStringField("xmlns", "http://javafx.com/javafx");
		xml.writeStringField("xmlns:fx", "http://javafx.com/javafx");

		serializeObjectInternal(object);

		xml.writeEndObject();
	}
	protected void serializeObjectInternal(Object object) throws Exception {
		Map<String, Object> childElements = new LinkedHashMap<>();

		xml.setNextIsAttribute(true);
		BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
		for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
			String name = propertyDescriptor.getName();
			//Ignore if not readable and writable.
			if (propertyDescriptor.getWriteMethod() == null || propertyDescriptor.getReadMethod() == null) {
				log.debug("Skipping attribute {} because it is not both readable and writable", name);
				continue;
			}
			//Ignore if null.
			Object value = propertyDescriptor.getReadMethod().invoke(object);
			if (value == null) {
				log.debug("Skipping attribute {} because null value.", name);
				continue;
			}

			Class<?> type = value.getClass();
			if (isSimpleType(type) || type.isEnum()) {
				//Ignore if same as default value.
				Object defaultValue = getDefaultValue(object, propertyDescriptor);
				if (defaultValue != null && Objects.equals(value, defaultValue)) {
					log.debug("Skipping attribute {} because the value is the same as the default value: {}", name, value);
					continue;
				}

				//Write simple string field.
				xml.writeStringField(name, String.valueOf(value));
			} else if (Node.class.isAssignableFrom(type)) {
				//Track complex elements.
				childElements.put(name, value);
			}  else if (Image.class.isAssignableFrom(type) && value instanceof Image image) {
				startWrappedElement(name);
				xml.setNextName(createType(image));
				xml.writeStartObject();
				xml.setNextIsAttribute(true);
				xml.writeStringField("url", image.getUrl());
				xml.writeEndObject();
				endWrappedElement();
			} else {
				log.warn("Failed to serialize attribute {}. Unimplemented type: {}", name, type);
			}
		}

		if (object instanceof Pane parent) {
			Method getChildrenMethod = parent.getClass().getMethod("getChildren");

			if (Arrays.stream(getChildrenMethod.getAnnotations()).anyMatch(annotation -> annotation instanceof FXMLIgnore)) {
				log.debug("Skipping getChildren because FXMLIgnore annotation");
			} else if (!Modifier.isPublic(getChildrenMethod.getModifiers())) {
				log.debug("Skipping getChildren because method is not public");
			} else {
				startWrappedElement("children");
				for (Node child : parent.getChildrenUnmodifiable()) {
					serializeObject(child);
				}
				endWrappedElement();
			}
		}
		for (Map.Entry<String, Object> entry : childElements.entrySet()) {
			startWrappedElement(entry.getKey());
			serializeObject(entry.getValue());
			endWrappedElement();
		}

	}

	Map<Class<?>, Map<String, Object>> defaultValuesMap = new HashMap<>();
	protected Object getDefaultValue(Object object, PropertyDescriptor propertyDescriptor) {
		if (!defaultValuesMap.containsKey(object.getClass())) {
			defaultValuesMap.put(object.getClass(), new HashMap<>());
		}
		Map<String, Object> values = defaultValuesMap.get(object.getClass());
		if (!values.containsKey(propertyDescriptor.getName())) {
			try {
				Object value = Arrays.stream(object.getClass().getDeclaredConstructors())
						.filter(constructor -> constructor.getParameterCount() == 0)
						.map(constructor -> readDefaultValue(propertyDescriptor, constructor))
						.findFirst().orElseGet(() -> null);
				values.put(propertyDescriptor.getName(), value);
			} catch (Exception e) {
				values.put(propertyDescriptor.getName(), null);
			}
		}
		return values.get(propertyDescriptor.getName());
	}
	protected static Object readDefaultValue(PropertyDescriptor p, Constructor<?> c) {
		try {
			return p.getReadMethod().invoke(c.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	protected void startWrappedElement(String name) throws IOException {
		xml.writeFieldName(name);
		xml.writeStartObject();
		xml.writeFieldName(name);
		xml.writeStartArray();
	}
	protected void endWrappedElement() throws IOException {
		xml.writeEndArray();
		xml.writeEndObject();
	}
	protected void serializeObject(Object object) throws Exception {
		xml.setNextName(createType(object));
		xml.writeStartObject();
		serializeObjectInternal(object);
		xml.writeEndObject();
	}

	protected void writeImports() throws Exception {
		xml.writeRaw("<?import " + Image.class.getName() + "?>\r\n");
		xml.writeRaw("<?import " + WritableImage.class.getName() + "?>\r\n");
		for (Class<?> clazz : classes) {
			writeImport(clazz.getName());
		}
	}
	protected void writeImport(String text) throws Exception {
		xml.writeRaw("<?import " + text + "?>");
		xml.writeRaw(System.lineSeparator());
	}

	protected final Set<Class<?>> classes = new HashSet<>();
	protected QName createType(Object object) {
		Class<? extends Object> toImport = object.getClass();
		classes.add(toImport);
		return new QName(toImport.getSimpleName());
	}

	protected void recursiveVisitChildren(Object object, Consumer<Object> visitor) {
		visitor.accept(object);
		if (object instanceof Parent) {
			((Parent) object).getChildrenUnmodifiable()
					.forEach(n -> recursiveVisitChildren(n, visitor));
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

	protected void writeStartDocument() throws XMLStreamException, IOException {
		xml.getStaxWriter().writeStartDocument();
		xml.writeRaw(System.lineSeparator());
		xml.writeRaw(System.lineSeparator());
	}
}
