package edworld.interparliadataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.util.Assert;

public class BrazilSourceTest {
	private Source source = new BrazilSource() {
		@Override
		protected String translateUrl(String url) {
			String resourcePath = url.replaceAll("http://www.lexml.gov.br/urn/(.*)", "/$1.html");
			if (url.equals(resourcePath)) {
				resourcePath = url.replaceAll(
						"http://legislacao.planalto.gov.br/legisla/legislacao.nsf/websearch\\?openagent&(.*)",
						"/$1.html");
				if (url.equals(resourcePath))
					return url;
			}
			return getClass().getResource(resourcePath.replaceAll("[:;,=&]", "_")).toString();
		}
	};

	@Test
	public void loadDocumentWithTextUnavailable() throws IOException {
		List<Document> documents = source.loadDocuments("urn:lex:br:federal:decreto:1972-07-28;70885");
		assertEquals(1, documents.size());
		Assert.notNull(documents.get(0), "document shouldn't be null even when legal text is not available");
		Assertions.assertThat(documents.get(0).getSentences()).isEmpty();
	}

	@Test
	public void loadODocument1992() throws IOException {
		List<Document> documents = source.loadDocuments("urn:lex:br:federal:lei:1992-04-22;8413");
		assertEquals(1, documents.size());
		Assert.notNull(documents.get(0), "document shouldn't be null when legal text is available");
		assertEquals("pt-BR", documents.get(0).getLang());
		assertEquals(9, documents.get(0).getSentences().size());
	}

	@Test
	public void loadODocument1993() throws IOException {
		List<Document> documents = source.loadDocuments("urn:lex:br:federal:lei:1993-06-21;8666");
		assertEquals(1, documents.size());
		Assert.notNull(documents.get(0), "document shouldn't be null when legal text is available");
		assertEquals("pt-BR", documents.get(0).getLang());
		assertEquals(1008, documents.get(0).getSentences().size());
	}
}
