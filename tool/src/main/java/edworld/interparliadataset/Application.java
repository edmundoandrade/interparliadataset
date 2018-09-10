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
		String langs = "BG-ES-CS-DA-DE-ET-EL-EN-FR-GA-HR-IT-LV-LT-HU-MT-NL-PL-PT-RO-SK-SL-FI-SV";
		EuropeanUnion europeanUnion = new EuropeanUnion();
		if (args.length == 0) {
			System.out.println();
			System.out.println("Inter-Parliamentary Dataset (https://github.com/edmundoandrade/interparliadataset)");
			System.out.println(
					"To import one or more European Union's laws, use: --CELEX <number>. Example: --CELEX 32017R2403 --CELEX 31974B0144");
			System.out.println("By default, the blocks of text will be imported according to the available languages: "
					+ langs + ".");
			System.out.println(
					"To restrict the languages, use: --languages <LANG1>-<LANG2>-...-<LANGN>. Example: --languages EN-PT");
			System.out.println();
			return;
		}
		File metadata = new File("dataset/metadata.csv");
		File blocks = new File("dataset/textblocks.csv");
		metadata.getParentFile().mkdirs();
		blocks.getParentFile().mkdirs();
		boolean generateMetadataHeader = !metadata.exists();
		boolean generateBlocksHeader = !blocks.exists();
		try (PrintWriter metadataOut = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(metadata, true), StandardCharsets.UTF_8)));
				PrintWriter blocksOut = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(blocks, true), StandardCharsets.UTF_8)))) {
			List<String> idsCELEX = new ArrayList<>();
			int index = 0;
			while (index < args.length) {
				if (args[index].equalsIgnoreCase("--languages")) {
					index++;
					langs = args[index].trim();
				} else if (args[index].equalsIgnoreCase("--CELEX")) {
					index++;
					idsCELEX.add(args[index].trim());
				}
				index++;
			}
			System.out.println("importing law texts in the languages: " + langs);
			System.out.println("appending metadata to " + metadata.getAbsolutePath());
			System.out.println("appending aligned blocks of text to " + blocks.getAbsolutePath());
			String[] languages = langs.split("-");
			if (generateMetadataHeader)
				Document.metadataHeaderToCsv(metadataOut);
			if (generateBlocksHeader)
				Document.blocksHeaderToCsv(languages, blocksOut);
			LocalDateTime start = LocalDateTime.now();
			for (String idCELEX : idsCELEX)
				europeanUnion.loadDocument(idCELEX, languages).metadataToCsv(metadataOut).blocksToCsv(languages,
						blocksOut);
			System.out.println("Finished. Total duration: " + Duration.between(start, LocalDateTime.now()));
		}
	}
}
