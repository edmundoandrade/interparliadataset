package edworld.interparliadataset;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EuropeanUnionSource extends Source {
	private String[] languages;

	public EuropeanUnionSource(String[] languages) {
		this.languages = languages;
	}

	private static Pattern JOURNAL_ID = Pattern.compile("/legal-content/[^\"]*/TXT/\\?uri=(OJ:[^\"]*)");
	private static Pattern TXT_LANG = Pattern.compile("/legal-content/([^\"]*)/ALL/\\?uri=CELEX");
	private static Pattern HTML_LANG = Pattern.compile("/legal-content/([^\"]*)/TXT/HTML/\\?uri=CELEX");
	private static Pattern PDF_LANG = Pattern.compile("/legal-content/([^\"]*)/TXT/PDF/\\?uri=CELEX");
	private static Pattern JOURNAL_LANG = Pattern.compile("/legal-content/([^\"]*)/TXT/\\?uri=OJ:");
	private static Pattern METADATA = Pattern
			.compile("(?is)<li xmlns=\"http://www\\.w3\\.org/1999/xhtml\">\\s*(.*?)\\s*</li>");

	@Override
	public Document loadDocument(String id) throws IOException {
		Document document = new Document(id);
		String pageContent = pageContent(translateUrl("https://eur-lex.europa.eu/legal-content/EN/ALL/?uri=" + id));
		pageContent = pageContent.substring(pageContent.indexOf("Languages, formats and link to OJ"));
		document.setJournalId(uniqueOccurrences(pageContent, JOURNAL_ID, "+"));
		document.setTxtLang(uniqueOccurrences(pageContent, TXT_LANG, "+"));
		document.setHtmlLang(uniqueOccurrences(pageContent, HTML_LANG, "+"));
		document.setPdfLang(uniqueOccurrences(pageContent, PDF_LANG, "+"));
		document.setJournalLang(uniqueOccurrences(pageContent, JOURNAL_LANG, "+"));
		document.setMetadata(uniqueOccurrences(pageContent, METADATA, " | "));
		int languageIndex = 0;
		for (String lang : document.txtLanguages()) {
			for (String language : languages)
				if (language.equalsIgnoreCase(lang)) {
					loadTexts(id, lang, languageIndex, document);
					break;
				}
			languageIndex++;
		}
		return document;
	}

	private void loadTexts(String id, String lang, int languageIndex, Document document) throws IOException {
		String pageContent = pageContent(
				translateUrl("https://eur-lex.europa.eu/legal-content/" + lang + "/TXT/?uri=" + id));
		int start = pageContent.indexOf("textTabContent");
		int end = pageContent.indexOf("doc-end");
		pageContent = pageContent.substring(start, end);
		loadTexts(pageContent, languageIndex, document);
	}

	private void loadTexts(String pageContent, int languageIndex, Document document) {
		int index = 0;
		Matcher matcher = PARAGRAPH.matcher(pageContent);
		while (matcher.find()) {
			String text = removeMarkup(matcher.group(1));
			if (combinedWithItemLetter(text)) {
				int sep = text.indexOf(')') + 1;
				if (index == document.getTexts().size())
					document.getTexts().add(new String[document.txtLanguages().length]);
				document.getTexts().get(index)[languageIndex] = text.substring(0, sep);
				index++;
				text = text.substring(sep).trim();
			}
			if (index == document.getTexts().size())
				document.getTexts().add(new String[document.txtLanguages().length]);
			document.getTexts().get(index)[languageIndex] = text;
			index++;
		}
	}

	private boolean combinedWithItemLetter(String text) {
		return text.matches("\\(?[a-zA-Z]\\).*[a-zA-Z].*");
	}
}
