package edworld.interparliadataset;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class BrazilSource extends Source {
	private static Pattern URL_PLANALTO = Pattern.compile("href=\"(http://legislacao\\.planalto\\.gov\\.br[^\"]*)\"");

	@Override
	public Document loadDocument(String id) throws IOException {
		Document document = new Document(id);
		String pageContent = pageContent("http://www.lexml.gov.br/urn/" + id);
		Optional<String> url = uniqueOccurrences(pageContent, URL_PLANALTO).stream().findFirst();
		if (!url.isPresent())
			throw new IllegalArgumentException("URL to the legal text not found for the document: " + id);
		String textUrl = unescapeHTML(url.get());
		pageContent = pageContent(textUrl);
		System.out.println(pageContent);
		if (pageContent.contains("em processo de inclusão retrospectiva"))
			throw new IllegalArgumentException(
					"URL to the legal text informed availability of text format is pending: " + textUrl);
		return document;
	}
}
