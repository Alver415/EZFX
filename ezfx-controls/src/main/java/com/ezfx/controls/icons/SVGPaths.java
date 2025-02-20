package com.ezfx.controls.icons;

import com.ezfx.base.utils.Resources;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface SVGPaths {

//	String MINIMIZE = "M 3.5 7.5 H 12.5 V 7.5 H 3.5 L 3.5 7.5";
//	String MAXIMIZE = "M 3 3 H 13 V 13 H 3 L 3 3";
//	String RESTORE = "M 3 3 H 13 V 13 H 3 L 3 3";
//	String CLOSE = "M 3 3 L 13 13 M 3 13 L 13 3";
//	String GEAR = "M 7 2 L 7 3.1015625 A 5.0000002 5.0000002 0 0 0 5.2460938 3.8320312 L 4.4648438 3.0507812 L 3.0507812 4.4648438 L 3.8320312 5.2460938 A 5.0000002 5.0000002 0 0 0 3.1054688 7 L 2 7 L 2 9 L 3.1015625 9 A 5.0000002 5.0000002 0 0 0 3.8320312 10.753906 L 3.0507812 11.535156 L 4.4648438 12.949219 L 5.2460938 12.167969 A 5.0000002 5.0000002 0 0 0 7 12.894531 L 7 14 L 9 14 L 9 12.898438 A 5.0000002 5.0000002 0 0 0 10.753906 12.167969 L 11.535156 12.949219 L 12.949219 11.535156 L 12.167969 10.753906 A 5.0000002 5.0000002 0 0 0 12.894531 9 L 14 9 L 14 7 L 12.898438 7 A 5.0000002 5.0000002 0 0 0 12.167969 5.2460938 L 12.949219 4.4648438 L 11.535156 3.0507812 L 10.753906 3.8320312 A 5.0000002 5.0000002 0 0 0 9 3.1054688 L 9 2 L 7 2 z M 8 4 A 4.0000002 4.0000002 0 0 1 10.869141 5.2128906 A 4.0000002 4.0000002 0 0 1 12 8 A 4.0000002 4.0000002 0 0 1 8 12 A 4.0000002 4.0000002 0 0 1 4 8 A 4.0000002 4.0000002 0 0 1 8 4 z ";
//	String GEAR2 = "M 7 2 L 7 3.1015625 A 5.0000002 5.0000002 0 0 0 5.2460938 3.8320312 L 4.4648438 3.0507812 L 3.0507812 4.4648438 L 3.8320312 5.2460938 A 5.0000002 5.0000002 0 0 0 3.1054688 7 L 2 7 L 2 9 L 3.1015625 9 A 5.0000002 5.0000002 0 0 0 3.8320312 10.753906 L 3.0507812 11.535156 L 4.4648438 12.949219 L 5.2460938 12.167969 A 5.0000002 5.0000002 0 0 0 7 12.894531 L 7 14 L 9 14 L 9 12.898438 A 5.0000002 5.0000002 0 0 0 10.753906 12.167969 L 11.535156 12.949219 L 12.949219 11.535156 L 12.167969 10.753906 A 5.0000002 5.0000002 0 0 0 12.894531 9 L 14 9 L 14 7 L 12.898438 7 A 5.0000002 5.0000002 0 0 0 12.167969 5.2460938 L 12.949219 4.4648438 L 11.535156 3.0507812 L 10.753906 3.8320312 A 5.0000002 5.0000002 0 0 0 9 3.1054688 L 9 2 L 7 2 z M 8 5 A 2.9999999 2.9999999 0 0 1 11 8 A 2.9999999 2.9999999 0 0 1 8 11 A 2.9999999 2.9999999 0 0 1 5 8 A 2.9999999 2.9999999 0 0 1 8 5 z ";

	static SVGPath of(String content) {
		SVGPath svgPath = new SVGPath();
		svgPath.setContent(content);
		svgPath.setStrokeType(StrokeType.CENTERED);
		svgPath.setStroke(Color.BLACK);
		svgPath.setStrokeWidth(1);
		svgPath.setFill(Color.BLACK);
		svgPath.setFillRule(FillRule.NON_ZERO);
		return svgPath;
	}

	static Group parse(String string) throws IOException, ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newDefaultInstance();
		SAXParser parser = factory.newSAXParser();
		SVGHandler handler = new SVGHandler();
		parser.parse(new InputSource(new StringReader(string)), handler);

		return handler.getRoot();
	}
	static Group parse(Path path) throws IOException, ParserConfigurationException, SAXException {
		return parse(path.toFile());
	}
	static Group parse(File file) throws IOException, ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newDefaultInstance();
		SAXParser parser = factory.newSAXParser();
		SVGHandler handler = new SVGHandler();
		parser.parse(file, handler);

		return handler.getRoot();
	}
	static Group _parse(InputStream inputStream) {
		try {
			SAXParserFactory factory = SAXParserFactory.newDefaultInstance();
			SAXParser parser = factory.newSAXParser();
			SVGHandler handler = new SVGHandler();
			parser.parse(inputStream, handler);

			return handler.getRoot();
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	class SVGHandler extends DefaultHandler {
		private static final String GROUP = "g";
		private static final String PATH = "path";

		private Group root = new Group();
		private Group group;
		private SVGPath path;

		public Group getRoot() {
			return root;
		}

		@Override
		public void startDocument() throws SAXException {
			root = new Group();
			group = root;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			switch (qName) {
				case GROUP:
					Group newGroup = new Group();
					group.getChildren().add(newGroup);
					group = newGroup;
					break;
				case PATH:
					path = new SVGPath();
					String[] styles = Optional.ofNullable(attributes.getValue("style"))
							.map(String::trim)
							.map(s -> s.split(";"))
							.orElse(new String[0]);
					for (String style : styles) {
						String[] split = style.trim().split(":");
						String key = split[0].trim();
						String value = split[1].trim();
						switch (key){
							case "stroke" -> path.setStroke(Paint.valueOf(value));
							case "stroke-type" -> path.setStrokeType(StrokeType.valueOf(value.toUpperCase()));
							case "stroke-width" -> path.setStrokeWidth(Double.parseDouble(value));
							case "stroke-dashoffset" -> path.setStrokeDashOffset(Double.parseDouble(value));
							case "stroke-linecap" -> path.setStrokeLineCap(StrokeLineCap.valueOf(value.toUpperCase()));
							case "fill" -> path.setFill(Paint.valueOf(value));
							case "fill-rule" -> path.setFillRule(value.equalsIgnoreCase("NONZERO") ? FillRule.NON_ZERO : FillRule.EVEN_ODD);

						}
					}
					String data = attributes.getValue("d");
					validateSVGPathData(data);
					path.setContent(data);
					break;
			}
		}


		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			switch (qName) {
				case GROUP:
					group = (Group) group.getParent();
					break;
				case PATH:
					group.getChildren().add(path);
					break;
			}
		}

		// TODO: Fix regex pattern to properly validate svg
		private static final Pattern PATH_PATTERN = Pattern.compile("^([MmLlHhVvCcSsQqTtAaZz][0-9.,\\-\\s]+)+$");
		private static void validateSVGPathData(String data) throws SAXException {
			Matcher matcher = PATH_PATTERN.matcher(data);
			if (!matcher.matches()){
//				throw new SAXException("Invalid SVG Path: %s".formatted(data));
			}
		}
	}
}
