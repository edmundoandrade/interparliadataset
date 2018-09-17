package edworld.interparliadataset;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;

public class BrazilSource extends Source {
	private static final String LANGUAGE_PTBR = "pt-BR";
	private static final Pattern URL_PLANALTO = Pattern
			.compile("href=\"(http://legislacao\\.planalto\\.gov\\.br[^\"]*)\"");

	public BrazilSource() {
		ignoreCertificateValidation();
	}

	@Override
	public List<Document> loadDocuments(String docId) throws IOException {
		String urlLexML = "http://www.lexml.gov.br/urn/" + docId;
		Document document = new Document(docId, LANGUAGE_PTBR);
		String pageContent = pageContent(translateUrl(urlLexML));
		Optional<String> url = uniqueOccurrences(pageContent, URL_PLANALTO).stream().findFirst();
		if (!url.isPresent())
			throw new IllegalArgumentException("URL to the legal text not found for the document: " + docId);
		String urlPlanalto = unescapeHTML(url.get());
		try (WebClient webClient = new WebClient()) {
			webClient.addRequestHeader("Referer", urlLexML);
			webClient.addRequestHeader("Accept-Language", LANGUAGE_PTBR);
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			new WebConnectionWrapper(webClient) {
				public WebResponse getResponse(final WebRequest request) throws IOException {
					if (request.getUrl().toString().startsWith("http://www.planalto.gov.br/"))
						request.setUrl(new URL(request.getUrl().toString().replace("http:", "https:")));
					WebResponse response = super.getResponse(request);
					if (document.getSentences().isEmpty() && request.getUrl().toString().matches(".*\\.html?")
							&& response.getStatusCode() == HttpStatus.SC_OK) {
						document.setHtmlUrl(request.getUrl().toString());
						loadTexts(response.getContentAsString(), document);
					}
					return response;
				}
			};
			Page page = webClient.getPage(translateUrl(urlPlanalto));
			if (document.getSentences().isEmpty()) {
				pageContent = page.getWebResponse().getContentAsString();
				if (pageContent.contains("em processo de inclusão retrospectiva")) {
					System.err.println("[" + urlLexML + "]");
					System.err.println("Legal text is not available yet, according to its page: " + urlPlanalto);
				} else if (page.getWebResponse().getStatusCode() == HttpStatus.SC_OK) {
					document.setHtmlUrl(urlPlanalto);
					loadTexts(pageContent, document);
				}
			}
		}
		return Arrays.asList(document);
	}

	private void loadTexts(String pageContent, Document document) {
		int sequence = 1;
		Matcher matcher = PARAGRAPH.matcher(pageContent);
		while (matcher.find()) {
			String text = removeMarkup(matcher.group(1));
			if (combinedWithArticleNumber(text)) {
				int sep = text.indexOf('°') + 1;
				document.getSentences().add(new DocumentSentence(sequence, text.substring(0, sep)));
				text = text.substring(sep).trim();
				sequence++;
			}
			document.getSentences().add(new DocumentSentence(sequence, text));
			sequence++;
		}
	}

	private boolean combinedWithArticleNumber(String text) {
		return text.matches("Art.\\s+\\d+°\\s+.*");
	}
}
