package com.ezfx.filesystem;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GlobFilter implements Predicate<Path> {

	private final List<Rule> rules;

	private GlobFilter(List<Rule> rules) {
		this.rules = rules;
	}

	public static GlobFilter parse(Path path) throws IOException {
		try (Stream<String> lines = Files.lines(path)) {
			return new GlobFilter(lines.map(Rule::parse).toList());
		}
	}

	public static GlobFilter parse(String string) {
		return parse(string.split("\n"));
	}

	public static GlobFilter parse(String... rules) {
		return parse(List.of(rules));
	}

	public static GlobFilter parse(Collection<String> rules) {
		return new GlobFilter(rules.stream().map(Rule::parse).toList());
	}

	@Override
	public boolean test(Path path) {
		boolean matched = false;
		for (Rule rule : rules) {
			PathMatcher matcher = rule.getMatcher();
			if (matcher.matches(path)) {
				matched = !rule.negated;
			}
		}
		return matched;
	}

	public boolean test(String path) {
		return test(Path.of(path));
	}

	public record Rule(String pattern, boolean negated) {
		private static final Map<String, PathMatcher> MATCHER_MAP = new ConcurrentHashMap<>();
		public Rule(String pattern, boolean negated){
			this.pattern = pattern;
			this.negated = negated;

			MATCHER_MAP.computeIfAbsent(pattern,
					_ -> FileSystems.getDefault().getPathMatcher("glob:%s".formatted(pattern)));
		}
		public static Rule parse(String line) {
			boolean negated = line.startsWith("!");
			String pattern = negated ? line.substring(1) : line;
			return new Rule(pattern, negated);
		}

		public PathMatcher getMatcher() {
			return MATCHER_MAP.get(pattern);
		}
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GlobFilter that)) return false;

		return rules.equals(that.rules);
	}

	@Override
	public int hashCode() {
		return rules.hashCode();
	}
}
