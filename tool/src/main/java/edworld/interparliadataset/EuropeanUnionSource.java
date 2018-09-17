package edworld.interparliadataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.neovisionaries.i18n.LanguageAlpha3Code;

public class EuropeanUnionSource extends Source {
	private String[] languages;

	public EuropeanUnionSource(String[] languages) {
		this.languages = languages;
	}

	private static Pattern JOURNAL_ID = Pattern.compile("/legal-content/[^\"]*/TXT/\\?uri=(OJ:[^\"]*)");
	private static Pattern XML_METADATA = Pattern.compile("(/download-notice\\.html[^\"]*)");
	private static Pattern EXPRESSION_LANGUAGE = Pattern
			.compile("(?is)<EXPRESSION_USES_LANGUAGE[^>]*>.*?<IDENTIFIER>(.*?)</IDENTIFIER>");
	private static Pattern EXPRESSION_MANIFESTATION_TYPE = Pattern.compile(
			"(?is)<EXPRESSION_USES_LANGUAGE[^>]*>.*?<IDENTIFIER>(.*?)</IDENTIFIER>.*?</EXPRESSION_USES_LANGUAGE>.*?</EXPRESSION>(.*?)</EMBEDDED_NOTICE>");
	private static Pattern CREATED_BY = Pattern.compile("(?is)<CREATED_BY[^>]*>.*?<PREFLABEL>(.*?)</PREFLABEL>.");

	@Override
	public List<Document> loadDocuments(String docId) throws IOException {
		List<Document> documents = new ArrayList<>();
		String pageContent = pageContent(translateUrl("https://eur-lex.europa.eu/legal-content/EN/ALL/?uri=" + docId));
		String jornalId = uniqueOccurrences(pageContent, JOURNAL_ID, "+");
		String xmlMetadata = pageContent(translateUrl(
				"https://eur-lex.europa.eu" + unescapeHTML(uniqueOccurrences(pageContent, XML_METADATA, "+"))));
		String docLangs = uniqueOccurrences(xmlMetadata, EXPRESSION_LANGUAGE, "+");
		for (String lang3 : docLangs.split("\\+")) {
			String lang2 = toLang2(lang3);
			for (String language : languages)
				if (language.equalsIgnoreCase(lang2)) {
					String journalUrl = "https://eur-lex.europa.eu/legal-content/" + lang2.toUpperCase() + "/TXT/?uri="
							+ jornalId;
					Document document = new Document(docId, lang2);
					document.setAuthority(uniqueOccurrences(xmlMetadata, CREATED_BY, "; "));
					document.setFirstPublicationUrl(journalUrl);
					document.setLastPublicationUrl(journalUrl);
					if (hasExpression(xmlMetadata, lang2, "xhtml")) {
						document.setHtmlUrl("https://eur-lex.europa.eu/legal-content/" + lang2.toUpperCase()
								+ "/TXT/HTML/?uri=" + docId);
						document.setTextUrl("https://eur-lex.europa.eu/legal-content/" + lang2.toUpperCase()
								+ "/TXT/?uri=" + docId);
						String pageLangContent = pageContent(translateUrl(document.getTextUrl()));
						pageContent = pageContent.substring(pageContent.indexOf("Languages, formats and link to OJ"));
						loadTexts(pageLangContent, document);
					}
					if (hasExpression(xmlMetadata, lang2, "pdf"))
						document.setPdfUrl("https://eur-lex.europa.eu/legal-content/" + lang2.toUpperCase()
								+ "/TXT/PDF/?uri=" + docId);
					documents.add(document);
					break;
				}
		}
		return documents;
	}

	private boolean hasExpression(String xmlMetadata, String lang, String format) {
		Matcher matcher = EXPRESSION_MANIFESTATION_TYPE.matcher(xmlMetadata);
		while (matcher.find()) {
			String lang2 = toLang2(matcher.group(1));
			if (lang2.equalsIgnoreCase(lang))
				return matcher.group(2).contains("<MANIFESTATION manifestation-type=\"" + format);
		}
		return false;
	}

	private void loadTexts(String pageContent, Document document) {
		int start = pageContent.indexOf("textTabContent");
		int end = pageContent.indexOf("doc-end");
		pageContent = pageContent.substring(start, end);
		int sequence = 1;
		Matcher matcher = PARAGRAPH.matcher(pageContent);
		while (matcher.find()) {
			String text = removeMarkup(matcher.group(1));
			if (combinedWithItemLetter(text)) {
				int sep = text.indexOf(')') + 1;
				document.getSentences().add(new DocumentSentence(sequence, text.substring(0, sep)));
				text = text.substring(sep).trim();
				sequence++;
			}
			document.getSentences().add(new DocumentSentence(sequence, text));
			sequence++;
		}
	}

	private boolean combinedWithItemLetter(String text) {
		return text.matches("\\(?[a-zA-Z]\\).*[a-zA-Z].*");
	}

	private String toLang2(String lang3) {
		return LanguageAlpha3Code.valueOf(lang3.toLowerCase()).getAlpha2().toString();
	}
}
