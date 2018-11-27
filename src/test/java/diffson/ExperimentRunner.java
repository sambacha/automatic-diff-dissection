package diffson;

import java.io.File;
import java.util.Date;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * Experiment runners
 */
public class ExperimentRunner {

	@Test
	public void testICSE2015() throws Exception {
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();
		analyzer.run(ConfigurationProperties.getProperty("icse15difffolder"));
	}

	@Test
	public void testICSE15() throws Exception {
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "20");
		File outFile = new File("./out/ICSE2015_" + (new Date()));
		String out = outFile.getAbsolutePath();
		outFile.mkdirs();
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);
		String input = new File("./datasets/icse2015").getAbsolutePath();
		ConfigurationProperties.properties.setProperty("icse15difffolder", input);
		analyzer.run(ConfigurationProperties.getProperty("icse15difffolder"));

	}

	@Test
	public void testD4J() throws Exception {
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "200");
		File outFile = new File("./out/Defects4J");
		String out = outFile.getAbsolutePath();
		outFile.mkdirs();
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);
		String input = new File("./datasets/Defects4J").getAbsolutePath();
		ConfigurationProperties.properties.setProperty("icse15difffolder", input);
		analyzer.run(ConfigurationProperties.getProperty("icse15difffolder"));
	}

	@Test
	public void testCODEREP() throws Exception {
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("max_synthesis_step", "100000");
		for (int i = 1; i <= 1; i++) {
			File outFile = new File("./out/codeRepDS" + i);
			String out = outFile.getAbsolutePath();
			outFile.mkdirs();
			DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);
			String input = new File("./datasets/codeRepDS" + i).getAbsolutePath();
			ConfigurationProperties.properties.setProperty("icse15difffolder", input);
			analyzer.run(ConfigurationProperties.getProperty("icse15difffolder"));
		}
	}

}
