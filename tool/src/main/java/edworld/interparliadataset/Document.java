package edworld.interparliadataset;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Document {
	private static String CSV_SEPARATOR = ",";
	private String docId;
	private String lang;
	private String url;
	private String lastPublicationUrl;
	private String authority;
	private String title;
	private String alternativeTitles;
	private String introduction;
	private LocalDateTime date;
	private LocalDateTime firstPublicationDate;
	private LocalDateTime lastPublicationDate;
	private String textUrl;
	private String htmlUrl;
	private String pdfUrl;
	private List<DocumentSentence> sentences = new ArrayList<>();

	public Document(String docId, String lang) {
		this.docId = docId;
		this.lang = lang;
	}

	public static void metadataHeaderToCsv(PrintWriter out) {
		out.print("docId");
		out.print(CSV_SEPARATOR);
		out.print("lang");
		out.print(CSV_SEPARATOR);
		out.print("url");
		out.print(CSV_SEPARATOR);
		out.print("lastPublicationUrl");
		out.print(CSV_SEPARATOR);
		out.print("authority");
		out.print(CSV_SEPARATOR);
		out.print("title");
		out.print(CSV_SEPARATOR);
		out.print("alternativeTitles");
		out.print(CSV_SEPARATOR);
		out.print("introduction");
		out.print(CSV_SEPARATOR);
		out.print("date");
		out.print(CSV_SEPARATOR);
		out.print("firstPublicationDate");
		out.print(CSV_SEPARATOR);
		out.print("lastPublicationDate");
		out.print(CSV_SEPARATOR);
		out.print("textUrl");
		out.print(CSV_SEPARATOR);
		out.print("htmlUrl");
		out.print(CSV_SEPARATOR);
		out.print("pdfUrl");
		out.println();
	}

	public static void textHeaderToCsv(PrintWriter out) {
		out.print("docId");
		out.print(CSV_SEPARATOR);
		out.print("seq");
		out.print(CSV_SEPARATOR);
		out.print("lang");
		out.print(CSV_SEPARATOR);
		out.print("sentence");
		out.println();
	}

	public Document metadataToCsv(PrintWriter out) {
		out.print(quoteData(docId));
		out.print(CSV_SEPARATOR);
		out.print(lang);
		out.print(CSV_SEPARATOR);
		out.print(quoteData(url));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(lastPublicationUrl));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(authority));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(title));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(alternativeTitles));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(introduction));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(date));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(firstPublicationDate));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(lastPublicationDate));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(textUrl));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(htmlUrl));
		out.print(CSV_SEPARATOR);
		out.print(quoteData(pdfUrl));
		out.println();
		return this;
	}

	public static void textToCsv(PrintWriter out, List<Document> documents) {
		int sequence = 1;
		boolean found = true;
		while (found) {
			found = false;
			for (Document document : documents)
				for (DocumentSentence sentence : document.getSentences())
					if (sequence == sentence.getSeq()) {
						out.print(quoteData(document.getDocId()));
						out.print(CSV_SEPARATOR);
						out.print(sentence.getSeq());
						out.print(CSV_SEPARATOR);
						out.print(document.getLang());
						out.print(CSV_SEPARATOR);
						out.print(quoteData(sentence.getSentence()));
						out.println();
						found = true;
						break;
					}
			sequence++;
		}
	}

	private static String quoteData(String text) {
		if (text == null)
			return "";
		return "\"" + text.replaceAll("\"", "\"\"") + "\"";
	}

	private String quoteData(LocalDateTime date) {
		if (date == null)
			return "";
		return "\"" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\"";
	}

	public String getDocId() {
		return docId;
	}

	public String getLang() {
		return lang;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLastPublicationUrl() {
		return lastPublicationUrl;
	}

	public void setLastPublicationUrl(String lastPublicationUrl) {
		this.lastPublicationUrl = lastPublicationUrl;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlternativeTitles() {
		return alternativeTitles;
	}

	public void setAlternativeTitles(String alternativeTitles) {
		this.alternativeTitles = alternativeTitles;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public LocalDateTime getFirstPublicationDate() {
		return firstPublicationDate;
	}

	public void setFirstPublicationDate(LocalDateTime firstPublicationDate) {
		this.firstPublicationDate = firstPublicationDate;
	}

	public LocalDateTime getLastPublicationDate() {
		return lastPublicationDate;
	}

	public void setLastPublicationDate(LocalDateTime lastPublicationDate) {
		this.lastPublicationDate = lastPublicationDate;
	}

	public String getTextUrl() {
		return textUrl;
	}

	public void setTextUrl(String textUrl) {
		this.textUrl = textUrl;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public List<DocumentSentence> getSentences() {
		return sentences;
	}
}
