package edworld.interparliadataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Assert;

public class BrazilSourceTest {
	private Source source = new BrazilSource() {
		@Override
		protected URL buildUrl(String url) throws MalformedURLException {
			String resourcePath = url.replaceAll("http://www.lexml.gov.br/urn/(.*)", "/$1.html");
			if (url.equals(resourcePath)) {
				resourcePath = url.replaceAll(
						"http://legislacao.planalto.gov.br/legisla/legislacao.nsf/websearch\\?openagent&(.*)",
						"/$1.html");
				if (url.equals(resourcePath))
					return new URL(url);
			}
			return getClass().getResource(resourcePath.replaceAll("[:;,=&]", "_"));
		}
	};

	@Test
	public void loadDocumentWithTextUnavailable() throws IOException {
		Document document = source.loadDocument("urn:lex:br:federal:decreto:1972-07-28;70885");
		Assert.isNull(document, "document should be null when legal text is not available");
	}

	@Test
	@Ignore
	public void loadODocumentWithTextAvailable() throws IOException {
		Document document = source.loadDocument("urn:lex:br:federal:lei:1992-04-22;8413");
		Assert.notNull(document, "document shouldn't be null when legal text is available");
		assertEquals(22, document.getTexts().size());
		document = source.loadDocument("urn:lex:br:federal:lei:1993-06-21;8666");
		Assert.notNull(document, "document shouldn't be null when legal text is available");
		assertEquals(1016, document.getTexts().size());
	}
}
