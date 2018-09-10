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

public class EuropeanUnion {
	private static Pattern JOURNAL_ID = Pattern.compile("/legal-content/[^\"]*/TXT/\\?uri=(OJ:[^\"]*)");
	private static Pattern TXT_LANG = Pattern.compile("/legal-content/([^\"]*)/ALL/\\?uri=CELEX");
	private static Pattern HTML_LANG = Pattern.compile("/legal-content/([^\"]*)/TXT/HTML/\\?uri=CELEX");
	private static Pattern PDF_LANG = Pattern.compile("/legal-content/([^\"]*)/TXT/PDF/\\?uri=CELEX");
	private static Pattern JOURNAL_LANG = Pattern.compile("/legal-content/([^\"]*)/TXT/\\?uri=OJ:");
	private static Pattern METADATA = Pattern
			.compile("(?is)<li xmlns=\"http://www.w3.org/1999/xhtml\">\\s*(.*?)\\s*</li>");
	private static Pattern PARAGRAPH = Pattern.compile("(?is)<p[^>]*>\\s*(.*?)\\s*</p>");
	private static Pattern HTML_MARKUP = Pattern.compile("<[^/][^>]*>([^<]*)</[^>]*>");

	public Document loadDocument(String idCELEX, String[] languages) throws IOException {
		Document document = new Document("CELEX", idCELEX);
		String pageContent = pageContent("https://eur-lex.europa.eu/legal-content/EN/ALL/?uri=CELEX:" + idCELEX);
		pageContent = pageContent.substring(pageContent.indexOf("availableLanguageFormat"));
		document.setJournalId(uniqueOccurrences(pageContent, JOURNAL_ID, "-"));
		document.setTxtLang(uniqueOccurrences(pageContent, TXT_LANG, "-"));
		document.setHtmlLang(uniqueOccurrences(pageContent, HTML_LANG, "-"));
		document.setPdfLang(uniqueOccurrences(pageContent, PDF_LANG, "-"));
		document.setJournalLang(uniqueOccurrences(pageContent, JOURNAL_LANG, "-"));
		document.setMetadata(uniqueOccurrences(pageContent, METADATA, " | "));
		int languageIndex = 0;
		for (String lang : document.txtLanguages()) {
			for (String language : languages)
				if (language.equalsIgnoreCase(lang)) {
					loadBlocks(idCELEX, lang, languageIndex, document);
					break;
				}
			languageIndex++;
		}
		return document;
	}

	private void loadBlocks(String idCELEX, String lang, int languageIndex, Document document) throws IOException {
		String pageContent = pageContent(
				"https://eur-lex.europa.eu/legal-content/" + lang + "/TXT/?uri=CELEX:" + idCELEX);
		int start = pageContent.indexOf("textTabContent");
		int end = pageContent.indexOf("doc-end");
		pageContent = pageContent.substring(start, end);
		loadBlocks(pageContent, languageIndex, document);
	}

	private String pageContent(String url) throws IOException {
		try (InputStream stream = new URL(url).openStream(); Scanner scanner = new Scanner(stream, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}
	}

	private String uniqueOccurrences(String pageContent, Pattern pattern, String separator) {
		Set<String> occurrences = new LinkedHashSet<>();
		Matcher matcher = pattern.matcher(pageContent);
		while (matcher.find())
			occurrences.add(matcher.group(1));
		StringJoiner joiner = new StringJoiner(separator);
		occurrences.stream().forEach(item -> joiner.add(item));
		return joiner.toString();
	}

	private void loadBlocks(String pageContent, int languageIndex, Document document) {
		int index = 0;
		Matcher matcher = PARAGRAPH.matcher(pageContent);
		while (matcher.find()) {
			String block = removeMarkup(matcher.group(1));
			if (combinedWithItemLetter(block)) {
				int sep = block.indexOf(')') + 1;
				if (index == document.getBlocks().size())
					document.getBlocks().add(new String[document.getTxtLang().length()]);
				document.getBlocks().get(index)[languageIndex] = block.substring(0, sep);
				index++;
				block = block.substring(sep).trim();
			}
			if (index == document.getBlocks().size())
				document.getBlocks().add(new String[document.getTxtLang().length()]);
			document.getBlocks().get(index)[languageIndex] = block;
			index++;
		}
	}

	private String removeMarkup(String block) {
		String result = block.replace("<span class=\"super\">o</span>", "º");
		Matcher matcher = HTML_MARKUP.matcher(result);
		while (matcher.find()) {
			result = matcher.replaceAll("$1");
			matcher = HTML_MARKUP.matcher(result);
		}
		return result.replaceAll("^\\s*(.*?)\\s*$", "$1");
	}

	private boolean combinedWithItemLetter(String block) {
		return block.matches("\\(?[a-zA-Z]\\).*[a-zA-Z].*");
	}
}
