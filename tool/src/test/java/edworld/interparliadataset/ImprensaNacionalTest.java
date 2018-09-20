package edworld.interparliadataset;

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

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class ImprensaNacionalTest {
	@Test
	@Ignore
	public void loadDocumentsByMonthTest() throws IOException {
		loadDocumentsByMonth(2017, 7);
	}

	private void loadDocumentsByMonth(int year, int month) throws IOException {
		loadDocumentsByMonthFromLexML(year, month);
		loadDocumentsByMonthFromImprensaNacional(year, month);
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

	private void loadDocumentsByMonthFromImprensaNacional(int year, int month) throws IOException {
		String monthName = LocalDate.of(year, month, 1).getMonth()
				.getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).toLowerCase();
		String url = "http://www.imprensanacional.gov.br/dados-abertos/base-de-dados/publicacoes-do-dou/" + year + "/"
				+ monthName;
		System.out.println(url);
		String html = Source.pageContent(url);
		Set<String> zips = Source.uniqueOccurrences(html, Pattern.compile("href=\"(/documents/.*?)\""));
		for (String zip : zips) {
			try (ZipInputStream input = new ZipInputStream(
					new URL("http://www.imprensanacional.gov.br" + zip).openStream())) {
				ZipEntry zipEntry = input.getNextEntry();
				while (zipEntry != null) {
					System.out.println("\t" + zipEntry.getName());
					loadDocumentFromXml(IOUtils.toString(input, "UTF-8"));
					zipEntry = input.getNextEntry();
				}
			}
		}
	}

	private void loadDocumentFromXml(String xml) {
		for (String artType : Source.uniqueOccurrences(xml, Pattern.compile("artType=\"(.*?)\"")))
			System.out.println("\t\tType: " + Source.unescapeHTML(artType));
		for (String artCategory : Source.uniqueOccurrences(xml, Pattern.compile("artCategory=\"(.*?)\"")))
			System.out.println("\t\tCategory: " + Source.unescapeHTML(artCategory));
		for (String pdfUrl : Source.uniqueOccurrences(xml, Pattern.compile("pdfPage=\"(.*?)\"")))
			System.out.println("\t\tPDF URL: " + Source.unescapeHTML(pdfUrl));
		System.out.println(
				"\t\tSignedBy: " + Source.uniqueOccurrences(xml, Pattern.compile("(?is)<assina>(.*?)</assina>"), "; "));
//		for (String text : Source.uniqueOccurrences(xml,
//				Pattern.compile("(?is)<Texto><!\\[CDATA\\[(.*?)\\]\\]></Texto>")))
//			System.out.println("\t\tText: " + Source.removeMarkup(text));
	}

	private void indexDocumentsFromLexML(String html) {
		Set<String> urns = Source.uniqueOccurrences(html, Pattern.compile("href=\"/urn/(.*?)\""));
		for (String urn : urns)
			System.out.println("\t" + urn);
	}

	private Optional<String> nextPage(String html) {
		return Source.uniqueOccurrences(html, Pattern.compile("(?is)<a href=\"([^\"]*)\">Próxima</a>")).stream()
				.findFirst();
	}
}
