package edworld.interparliadataset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public abstract class Source {
	private static Pattern HTML_MARKUP = Pattern.compile("(?is)<[^/][^>]*>([^<]*)</[^>]*>");

	public abstract Document loadDocument(String id) throws IOException;

	protected String uniqueOccurrences(String pageContent, Pattern pattern, String separator) {
		StringJoiner joiner = new StringJoiner(separator);
		uniqueOccurrences(pageContent, pattern).stream().forEach(item -> joiner.add(item));
		return joiner.toString();
	}

	protected Set<String> uniqueOccurrences(String pageContent, Pattern pattern) {
		Set<String> occurrences = new LinkedHashSet<>();
		Matcher matcher = pattern.matcher(pageContent);
		while (matcher.find())
			occurrences.add(matcher.group(1));
		return occurrences;
	}

	protected String removeMarkup(String text) {
		String result = text.replace("<span class=\"super\">o</span>", "º");
		Matcher matcher = HTML_MARKUP.matcher(result);
		while (matcher.find()) {
			result = matcher.replaceAll("$1");
			matcher = HTML_MARKUP.matcher(result);
		}
		return result.replaceAll("^\\s*(.*?)\\s*$", "$1");
	}

	protected String unescapeHTML(String text) {
		return StringEscapeUtils.unescapeHtml4(text);
	}

	protected String pageContent(String url) throws IOException {
		try (InputStream stream = new URL(url).openStream(); Scanner scanner = new Scanner(stream, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}
	}
}