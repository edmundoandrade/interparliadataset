package edworld.interparliadataset;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ApplicationTest {
	@Test
	public void mainWithOnlyLanguages() throws IOException {
		File metadataFile = new File("dataset/metadata.csv");
		File textFile = new File("dataset/text.csv");
		metadataFile.delete();
		textFile.delete();
		Application.main(new String[] { "--languages", "EN" });
		assertTrue(metadataFile.exists());
		assertTrue(textFile.exists());
	}
}
