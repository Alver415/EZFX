package com.ezfx.polyglot.test;

import com.ezfx.polyglot.ContextFX;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PolyglotJavaTest {

	private static final Logger log = LoggerFactory.getLogger(PolyglotJavaTest.class);

	private final Context context = Context.newBuilder("java")
			.allowAllAccess(true)
			.logHandler(System.out)
			.build();

	private final ContextFX contextFX = new ContextFX(context);

	@Test
	public void testBoolean() throws Exception {
		boolean result = contextFX.execute("java", """
				true || false
				""");
		log.info(String.valueOf(result));
	}

	@Test
	public void testInteger() throws Exception {
		int result = contextFX.execute("java", """
				2 + 2
				""");
		log.info(String.valueOf(result));
	}

	@Test
	public void testDouble() throws Exception {
		double result = contextFX.execute("java", """
				2 * 3.14
				""");
		log.info(String.valueOf(result));
	}

	@Test
	public void testArrayList() throws Exception {
		ArrayList<String> result = contextFX.execute("java", """
				List<String> list = new ArrayList<>();
				list.add("Hello from JavaScript");
				list.add("Another item");
				list;
				""");
		log.info(String.valueOf(result));
	}

}
