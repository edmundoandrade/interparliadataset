package edworld.interparliadataset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.text.StringEscapeUtils;

public abstract class Source {
	protected static final Pattern PARAGRAPH = Pattern.compile("(?is)<p[^>]*>\\s*(.*?)\\s*</p>");

	public abstract List<Document> loadDocuments(String id) throws IOException;

	public static String pageContent(String url) throws IOException {
		try (InputStream stream = new URL(url).openStream(); Scanner scanner = new Scanner(stream, "UTF-8")) {
			return scanner.useDelimiter("\\A").next();
		}
	}

	public static String uniqueOccurrences(String pageContent, Pattern pattern, String separator) {
		StringJoiner joiner = new StringJoiner(separator);
		uniqueOccurrences(pageContent, pattern).stream().forEach(item -> joiner.add(item));
		return joiner.toString();
	}

	public static Set<String> uniqueOccurrences(String pageContent, Pattern pattern) {
		Set<String> occurrences = new LinkedHashSet<>();
		Matcher matcher = pattern.matcher(pageContent);
		while (matcher.find())
			occurrences.add(matcher.group(1));
		return occurrences;
	}

	public static String removeMarkup(String text) {
		String result = unescapeHTML(text.replace("&nbsp;", " ").replace("<sup>o</sup>", "º")
				.replace("<sup><u>o</u></sup>", "º").replaceAll("<u><sup><span[^>]*>o</span></sup></u>", "º")
				.replace("<span class=\"super\">o</span>", "º").replaceAll("<br[ /]*>|</p>", "&LINEBREAK;")
				.replaceAll("(?is)\\s*<[^>]*>\\s*", " ").replaceAll("(?s)\\s+", " "));
		return result.replace("&LINEBREAK;", "\n").trim();
	}

	public static String unescapeHTML(String text) {
		return StringEscapeUtils.unescapeHtml4(text);
	}

	protected String translateUrl(String url) {
		return url;
	}

	protected void ignoreCertificateValidation() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (GeneralSecurityException e) {
			throw new IllegalArgumentException(e);
		}
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
}