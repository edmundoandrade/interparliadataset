package edworld.interparliadataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class EuropeanUnionTest {
	private EuropeanUnion eur = new EuropeanUnion();

	@Test
	public void contextLoads() throws IOException {
		Document document = eur.loadDocument("32017R2403", "EN-PT-ES-FR".split("-"));
		assertEquals("OJ:L:2017:347:TOC", document.getJournalId());
		assertEquals("BG-ES-CS-DA-DE-ET-EL-EN-FR-GA-HR-IT-LV-LT-HU-MT-NL-PL-PT-RO-SK-SL-FI-SV", document.getTxtLang());
		assertEquals(645, document.getBlocks().size());
	}
}
