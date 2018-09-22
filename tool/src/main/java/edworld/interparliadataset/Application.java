package edworld.interparliadataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws IOException {
		String langs = "bg+es+cs+da+de+et+el+en+fr+ga+hr+it+lv+lt+hu+mt+nl+pl+pt+ro+sk+sl+fi+sv";
		if (args.length == 0) {
			System.out.println();
			System.out.println(
					".............................................................................................");
			System.out.println("INTER-PARLIAMENTARY DATASET tool");
			System.out.println("(https://github.com/edmundoandrade/interparliadataset)");
			System.out.println();
			System.out.println("This tool is able to download and convert legislative data given:");
			System.out.println("- the CELEX number of one or more European Union's laws; for instance:");
			System.out.println("  --CELEX:32017R2403 --CELEX:31974B0144");
			System.out.println("- the LEX URN of one or more Brazil's laws; for instance:");
			System.out.println("  --urn:lex:br:federal:lei:1992-04-22;8413");
			System.out.println();
			System.out.println(
					"By default, European Union's laws will be downloaded in the available EUR-Lex languages:");
			System.out.println("  " + langs);
			System.out.println(
					"To restrict these languages, use: --languages <LANG1>+...+<LANGN>. Example: --languages en+pt");
			System.out.println("In case of Brazil's laws, there is only one target language: pt-BR");
			System.out.println();
			System.out.println("As result, the legislative data will be saved in two files:");
			System.out.println("- dataset/metadata.csv");
			System.out.println("- dataset/text.csv");
			System.out
					.println("New data will always be appended to the end of these files in case they already exist.");
			System.out.println(
					".............................................................................................");
			System.out.println();
			return;
		}
		File metadataFile = new File("dataset/metadata.csv");
		File textFile = new File("dataset/text.csv");
		metadataFile.getParentFile().mkdirs();
		textFile.getParentFile().mkdirs();
		boolean generateMetadataHeader = !metadataFile.exists();
		boolean generateTextHeader = !textFile.exists();
		try (PrintWriter metadataOut = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(metadataFile, true), StandardCharsets.UTF_8)));
				PrintWriter textOut = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(textFile, true), StandardCharsets.UTF_8)))) {
			List<String> idsCELEX = new ArrayList<>();
			List<String> idsURNLEXBR = new ArrayList<>();
			int index = 0;
			while (index < args.length) {
				if (args[index].startsWith("--languages")) {
					index++;
					langs = args[index].trim();
				} else if (args[index].startsWith("--CELEX:"))
					idsCELEX.add(args[index].substring(2).trim());
				else if (args[index].startsWith("--urn:lex:br:"))
					idsURNLEXBR.add(args[index].substring(2).trim());
				index++;
			}
			System.out.println("importing law texts in the languages: " + langs);
			System.out.println("appending metadata to " + metadataFile.getAbsolutePath());
			System.out.println("appending text to " + textFile.getAbsolutePath());
			if (generateMetadataHeader)
				Document.metadataHeaderToCsv(metadataOut);
			if (generateTextHeader)
				Document.textHeaderToCsv(textOut);
			LocalDateTime start = LocalDateTime.now();
			EuropeanUnionSource europeanUnionSource = new EuropeanUnionSource(langs.split("\\+"));
			Source brazilSource = new BrazilSource();
			for (String docId : idsCELEX) {
				List<Document> documents = europeanUnionSource.loadDocuments(docId);
				documents.stream().forEach(document -> document.metadataToCsv(metadataOut));
				Document.textToCsv(textOut, documents);
			}
			for (String docId : idsURNLEXBR) {
				List<Document> documents = brazilSource.loadDocuments(docId);
				documents.stream().forEach(document -> document.metadataToCsv(metadataOut));
				Document.textToCsv(textOut, documents);
			}
			System.out.println("Finished. Total duration: " + Duration.between(start, LocalDateTime.now()));
		}
	}
}
