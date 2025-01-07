package com.ezfx.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PathProcessor {

	private final List<Rule> rules = new ArrayList<>();

	// Adds a rule to the processor
	public void addRule(String rule) {
		boolean isNegated = rule.startsWith("!");
		String cleanRule = isNegated ? rule.substring(1) : rule;

		Pattern pattern = Pattern.compile(globToRegex(cleanRule));
		rules.add(new Rule(pattern, isNegated));
	}

	// Loads rules from a file (e.g., .gitignore)
	public void loadRules(Path filePath) throws IOException {
		List<String> lines = Files.readAllLines(filePath);
		for (String line : lines) {
			if (!line.trim().isEmpty() && !line.startsWith("#")) { // Ignore comments and empty lines
				addRule(line.trim());
			}
		}
	}

	// Checks if a file path should be ignored
	public boolean shouldIgnore(Path path) {
		String filePath = path.toString();
		boolean isIgnored = false;

		for (Rule rule : rules) {
			if (rule.matches(filePath)) {
				isIgnored = !rule.isNegated; // Negated rules override
			}
		}

		return isIgnored;
	}

	// Converts a glob pattern to a regex pattern
	private static String globToRegex(String glob) {
		StringBuilder regex = new StringBuilder();
		for (int i = 0; i < glob.length(); i++) {
			char c = glob.charAt(i);
			switch (c) {
				case '*':
					regex.append(".*");
					break;
				case '?':
					regex.append(".");
					break;
				case '.':
					regex.append("\\.");
					break;
				case '/':
					regex.append("[/\\\\]");
					break;
				default:
					regex.append(Pattern.quote(String.valueOf(c)));
			}
		}
		return regex.toString();
	}

	// Inner class to represent a rule
	private static class Rule {
		private final Pattern pattern;
		private final boolean isNegated;

		public Rule(Pattern pattern, boolean isNegated) {
			this.pattern = pattern;
			this.isNegated = isNegated;
		}

		public boolean matches(String filePath) {
			return pattern.matcher(filePath).matches();
		}

		public boolean isNegated() {
			return isNegated;
		}
	}

	public static void main(String[] args) throws IOException {
		PathProcessor processor = new PathProcessor();
		processor.addRule("*.tmp");
		processor.addRule("!important.tmp");
		processor.addRule("build/");
		processor.loadRules(Path.of(".gitignore"));

		Path file1 = Path.of("test.tmp");
		Path file2 = Path.of("important.tmp");
		Path file3 = Path.of("build/file.txt");

		System.out.println(processor.shouldIgnore(file1)); // true
		System.out.println(processor.shouldIgnore(file2)); // false
		System.out.println(processor.shouldIgnore(file3)); // true
	}
}