package edworld.interparliadataset;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class ImprensaNacionalTest {
	private static final Pattern IMPRENSA_NACIONAL_DOC_TEXT = Pattern
			.compile("(?is)<Texto><!\\[CDATA\\[(.*?)\\]\\]></Texto>");
	private static final Pattern LEXML_NEXT_PAGE = Pattern.compile("(?is)<a href=\"([^\"]*)\">Próxima</a>");
	private static final Pattern LEXML_DOC_URN = Pattern.compile("href=\"/urn/(.*?)\"");
	private static final Pattern IMPRENSA_NACIONAL_DOC_SIGNER = Pattern.compile("(?is)<assina>(.*?)</assina>");
	private static final Pattern IMPRENSA_NACIONAL_DOC_URL = Pattern.compile("pdfPage=\"(.*?)\"");
	private static final Pattern IMPRENSA_NACIONAL_DOC_CATEGORY = Pattern.compile("artCategory=\"(.*?)\"");
	private static final Pattern IMPRENSA_NACIONAL_DOC_TYPE = Pattern.compile("artType=\"(.*?)\"");
	private static final Pattern IMPRENSA_NACIONAL_ZIP = Pattern.compile("href=\"(/documents/.*?)\"");

	@Test
	@Ignore
	public void loadDocumentsByMonthTest() throws IOException {
		for (int year = 2002; year <= 2018; year++)
			for (int month = 1; month <= 12; month++)
				loadDocumentsByMonth(year, month, new File(year + "/" + month));
	}

	private void loadDocumentsByMonth(int year, int month, File folder) throws IOException {
		loadDocumentsByMonthFromLexML(year, month);
		loadDocumentsByMonthFromImprensaNacional(year, month, folder);
	}

	private void loadDocumentsByMonthFromLexML(int year, int month) throws IOException {
		int decade = year - year % 10;
		String monthCode = Integer.toString(month + 100).substring(1);
		String url = "http://lexml.gov.br/busca/search?f1-tipoDocumento=Legisla%C3%A7%C3%A3o::Lei;f2-date=" + decade
				+ "s::" + year + "::" + monthCode + ";f3-localidade=Brasil";
		boolean nextPage = true;
		while (nextPage) {
			System.out.println(url);
			String html = Source.pageContent(url);
			indexDocumentsFromLexML(html);
			Optional<String> next = nextPage(html);
			if (next.isPresent()) {
				url = next.get();
				nextPage = true;
			} else
				nextPage = false;
		}
	}

	private void loadDocumentsByMonthFromImprensaNacional(int year, int month, File folder) throws IOException {
		folder.mkdirs();
		String monthName = LocalDate.of(year, month, 1).getMonth()
				.getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toLowerCase();
		String url = "http://www.imprensanacional.gov.br/dados-abertos/base-de-dados/publicacoes-do-dou/" + year + "/"
				+ monthName;
		System.out.println(url);
		String html = Source.pageContent(url);
		Set<String> zips = Source.uniqueOccurrences(html, IMPRENSA_NACIONAL_ZIP);
		for (String zip : zips) {
			try (ZipInputStream input = new ZipInputStream(
					new URL("http://www.imprensanacional.gov.br" + zip).openStream())) {
				ZipEntry zipEntry = input.getNextEntry();
				while (zipEntry != null) {
					String xml = IOUtils.toString(input, "UTF-8");
					if ((xml.contains("artType=\"LEI\"")) && (xml.contains("artCategory=\"Atos do Poder Executivo\"")
							|| xml.contains("artCategory=\"Atos do Poder Legislativo\""))) {
						FileUtils.write(new File(folder, zipEntry.getName()), xml, "UTF-8");
						System.out.println("\t" + zipEntry.getName());
						loadDocumentFromXml(xml);
					}
					zipEntry = input.getNextEntry();
				}
			}
		}
	}

	private void loadDocumentFromXml(String xml) {
		for (String artType : Source.uniqueOccurrences(xml, IMPRENSA_NACIONAL_DOC_TYPE))
			System.out.println("\t\tType: " + Source.unescapeHTML(artType));
		for (String artCategory : Source.uniqueOccurrences(xml, IMPRENSA_NACIONAL_DOC_CATEGORY))
			System.out.println("\t\tCategory: " + Source.unescapeHTML(artCategory));
		for (String pdfUrl : Source.uniqueOccurrences(xml, IMPRENSA_NACIONAL_DOC_URL))
			System.out.println("\t\tPDF URL: " + Source.unescapeHTML(pdfUrl));
		System.out.println("\t\tSignedBy: " + Source.uniqueOccurrences(xml, IMPRENSA_NACIONAL_DOC_SIGNER, "; "));
		for (String text : Source.uniqueOccurrences(xml, IMPRENSA_NACIONAL_DOC_TEXT))
			System.out.println("\t\tText: " + Source.removeMarkup(text).split("\\r?\\n")[0]);
	}

	private void indexDocumentsFromLexML(String html) {
		Set<String> urns = Source.uniqueOccurrences(html, LEXML_DOC_URN);
		for (String urn : urns)
			System.out.println("\t" + urn);
	}

	private Optional<String> nextPage(String html) {
		return Source.uniqueOccurrences(html, LEXML_NEXT_PAGE).stream().findFirst();
	}
}
