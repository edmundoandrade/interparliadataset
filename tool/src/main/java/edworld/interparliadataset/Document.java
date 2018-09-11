package edworld.interparliadataset;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Document {
	private static String CSV_SEPARATOR = ",";
	private String id;
	private String journalId;
	private String metadata;
	private String txtLang;
	private String htmlLang;
	private String pdfLang;
	private String journalLang;
	List<String[]> texts = new ArrayList<>();
	String[] txtLanguages;

	public Document(String id) {
		this.id = id;
	}

	public static void metadataHeaderToCsv(PrintWriter out) {
		out.print("id");
		out.print(CSV_SEPARATOR);
		out.print("journalId");
		out.print(CSV_SEPARATOR);
		out.print("metadata");
		out.print(CSV_SEPARATOR);
		out.print("txtLang");
		out.print(CSV_SEPARATOR);
		out.print("htmlLang");
		out.print(CSV_SEPARATOR);
		out.print("pdfLang");
		out.print(CSV_SEPARATOR);
		out.print("journalLang");
		out.println();
	}

	public static void textHeaderToCsv(String[] languages, PrintWriter out) {
		out.print("id");
		out.print(CSV_SEPARATOR);
		out.print("seq");
		for (String lang : languages) {
			out.print(CSV_SEPARATOR);
			out.print(lang);
		}
		out.println();
	}

	public Document metadataToCsv(PrintWriter out) {
		out.print(quoteData(id));
		out.print(CSV_SEPARATOR);
		out.print(journalId);
		out.print(CSV_SEPARATOR);
		out.print(quoteData(metadata));
		out.print(CSV_SEPARATOR);
		out.print(txtLang);
		out.print(CSV_SEPARATOR);
		out.print(htmlLang);
		out.print(CSV_SEPARATOR);
		out.print(pdfLang);
		out.print(CSV_SEPARATOR);
		out.print(journalLang);
		out.println();
		return this;
	}

	public Document textToCsv(String[] languages, PrintWriter out) {
		int textSeq = 1;
		for (String[] text : texts) {
			out.print(quoteData(id));
			out.print(CSV_SEPARATOR);
			out.print(textSeq);
			for (String lang : languages) {
				out.print(CSV_SEPARATOR);
				int languageIndex = 0;
				for (String txtLanguage : txtLanguages) {
					if (txtLanguage.equalsIgnoreCase(lang) && text[languageIndex] != null) {
						out.print(quoteData(text[languageIndex]));
						break;
					}
					languageIndex++;
				}
			}
			out.println();
			textSeq++;
		}
		return this;
	}

	private String quoteData(String text) {
		return "\"" + text.replaceAll("\"", "\"\"") + "\"";
	}

	public String[] txtLanguages() {
		return txtLanguages;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJournalId() {
		return journalId;
	}

	public void setJournalId(String journalId) {
		this.journalId = journalId;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getTxtLang() {
		return txtLang;
	}

	public void setTxtLang(String txtLang) {
		this.txtLang = txtLang;
		txtLanguages = txtLang.isEmpty() ? new String[0] : txtLang.split("\\+");
	}

	public String getHtmlLang() {
		return htmlLang;
	}

	public void setHtmlLang(String htmlLang) {
		this.htmlLang = htmlLang;
	}

	public String getPdfLang() {
		return pdfLang;
	}

	public void setPdfLang(String pdfLang) {
		this.pdfLang = pdfLang;
	}

	public String getJournalLang() {
		return journalLang;
	}

	public void setJournalLang(String journalLang) {
		this.journalLang = journalLang;
	}

	public List<String[]> getTexts() {
		return texts;
	}
}
