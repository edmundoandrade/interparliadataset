package edworld.interparliadataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

public class BrazilSourceTest {
	private BrazilSource source = new BrazilSource() {
		@Override
		protected String pageContent(String url) throws IOException {
			return pageContent(getMockURL(url));
		}
	};

	@Test
	@Ignore
	public void loadOneDocumentWithTextFormatPending() throws IOException {
		Document document = source.loadDocument("urn:lex:br:federal:decreto:1972-07-28;70885");
		assertEquals("", document.getJournalId());
		assertEquals(-1, document.getTexts().size());
	}

	@Test
	@Ignore
	public void loadOneDocumentWithTextFormatAvailable() throws IOException {
		Document document = source.loadDocument("urn:lex:br:federal:lei:1992-04-22;8413");
		assertEquals("", document.getJournalId());
		assertEquals(-1, document.getTexts().size());
	}

	private URL getMockURL(String url) throws IOException {
		String resourcePath = url.replaceAll("http://www.lexml.gov.br/urn/(.*)", "/$1.html");
		if (url.equals(resourcePath)) {
			resourcePath = url.replaceAll(
					"http://legislacao.planalto.gov.br/legisla/legislacao.nsf/websearch\\?openagent&(.*)", "/$1.html");
			if (url.equals(resourcePath))
				return new URL(url);
		}
		return getClass().getResource(resourcePath.replaceAll("[:;,=&]", "_"));
	}
}
