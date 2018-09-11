package edworld.interparliadataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class BrazilSourceTest {
	@Test
	@Ignore
	public void loadOneDocumentWithTextFormatPending() throws IOException {
		BrazilSource source = new BrazilSource();
		Document document = source.loadDocument("urn:lex:br:federal:decreto:1972-07-28;70885");
		assertEquals("", document.getJournalId());
		assertEquals(-1, document.getTexts().size());
	}

	@Test
	@Ignore
	public void loadOneDocumentWithTextFormatAvailable() throws IOException {
		BrazilSource source = new BrazilSource();
		Document document = source.loadDocument("urn:lex:br:federal:lei:1992-04-22;8413");
		assertEquals("", document.getJournalId());
		assertEquals(-1, document.getTexts().size());
	}
}
