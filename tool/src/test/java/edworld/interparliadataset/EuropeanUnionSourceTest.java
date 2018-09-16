package edworld.interparliadataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class EuropeanUnionSourceTest {
	private EuropeanUnionSource source = new EuropeanUnionSource("EN+PT+ES+FR".split("\\+")) {
		@Override
		protected String translateUrl(String url) {
			return getClass().getResource(
					url.replaceAll("https://eur-lex.europa.eu/legal-content/([A-Z]+)/([A-Z]+)/\\?uri=CELEX:(.*)",
							"/CELEX-$3-$1-$2.html"))
					.toString();
		}
	};

	@Test
	public void loadOneDocumentInSpecifiedLanguages() throws IOException {
		Document document = source.loadDocument("CELEX:32017R2403");
		assertEquals("OJ:L:2017:347:TOC", document.getJournalId());
		assertEquals("BG+ES+CS+DA+DE+ET+EL+EN+FR+GA+HR+IT+LV+LT+HU+MT+NL+PL+PT+RO+SK+SL+FI+SV", document.getTxtLang());
		assertEquals(645, document.getTexts().size());
	}
}
