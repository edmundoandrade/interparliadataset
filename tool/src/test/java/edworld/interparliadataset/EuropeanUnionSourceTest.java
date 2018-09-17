package edworld.interparliadataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class EuropeanUnionSourceTest {
	private EuropeanUnionSource source = new EuropeanUnionSource("en+pt+es+fr".split("\\+")) {
		@Override
		protected String translateUrl(String url) {
			String resourcePath = url.replaceAll(
					"https://eur-lex.europa.eu/legal-content/([A-Z]+)/([A-Z]+)/\\?uri=CELEX:(.*)",
					"/CELEX-$3-$1-$2.html");
			if (url.equals(resourcePath)) {
				resourcePath = url.replaceAll(
						"https://eur-lex.europa.eu/download-notice.html\\?legalContentId=cellar:(.*?)\\&.*",
						"/download-notice-$1.xml");
				if (url.equals(resourcePath))
					return url;
			}
			return getClass().getResource(resourcePath.replaceAll("[:;,=&]", "_")).toString();
		}
	};

	@Test
	public void loadOneDocumentInSpecifiedLanguages() throws IOException {
		List<Document> documents = source.loadDocuments("CELEX:32017R2403");
		assertEquals(4, documents.size());
		assertEquals("fr", documents.get(0).getLang());
		assertEquals("Council of the European Union; European Parliament", documents.get(0).getAuthority());
		assertEquals("https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=OJ:L:2017:347:TOC",
				documents.get(0).getFirstPublicationUrl());
		assertEquals("https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=OJ:L:2017:347:TOC",
				documents.get(0).getLastPublicationUrl());
		assertEquals("https://eur-lex.europa.eu/legal-content/FR/TXT/?uri=CELEX:32017R2403",
				documents.get(0).getTextUrl());
		assertEquals("https://eur-lex.europa.eu/legal-content/FR/TXT/HTML/?uri=CELEX:32017R2403",
				documents.get(0).getHtmlUrl());
		assertEquals("https://eur-lex.europa.eu/legal-content/FR/TXT/PDF/?uri=CELEX:32017R2403",
				documents.get(0).getPdfUrl());
		assertEquals(645, documents.get(0).getSentences().size());
		assertEquals(7, documents.get(0).getSentences().get(6).getSeq());
		assertEquals(
				"relatif à la gestion durable des flottes de pêche externes et abrogeant le règlement (CE) nº 1006/2008 du Conseil",
				documents.get(0).getSentences().get(6).getSentence());
		assertEquals("en", documents.get(1).getLang());
		assertEquals("https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=OJ:L:2017:347:TOC",
				documents.get(1).getFirstPublicationUrl());
		assertEquals("https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=OJ:L:2017:347:TOC",
				documents.get(1).getLastPublicationUrl());
		assertEquals("https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX:32017R2403",
				documents.get(1).getTextUrl());
		assertEquals("https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32017R2403",
				documents.get(1).getHtmlUrl());
		assertEquals("https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32017R2403",
				documents.get(1).getPdfUrl());
		assertEquals(645, documents.get(1).getSentences().size());
		assertEquals(7, documents.get(1).getSentences().get(6).getSeq());
		assertEquals(
				"on the sustainable management of external fishing fleets, and repealing Council Regulation (EC) No 1006/2008",
				documents.get(1).getSentences().get(6).getSentence());
		assertEquals("es", documents.get(2).getLang());
		assertEquals(645, documents.get(2).getSentences().size());
		assertEquals(7, documents.get(2).getSentences().get(6).getSeq());
		assertEquals(
				"sobre la gestión sostenible de las flotas pesqueras exteriores y por el que se deroga el Reglamento (CE) n.º 1006/2008 del Consejo",
				documents.get(2).getSentences().get(6).getSentence());
		assertEquals("pt", documents.get(3).getLang());
		assertEquals(645, documents.get(3).getSentences().size());
		assertEquals(7, documents.get(3).getSentences().get(6).getSeq());
		assertEquals(
				"relativo à gestão sustentável das frotas de pesca externas, e que revoga o Regulamento (CE) n.º 1006/2008 do Conselho",
				documents.get(3).getSentences().get(6).getSentence());
//		assertEquals("OJ:L:2017:347:TOC", document.getJournalId());
	}
}
