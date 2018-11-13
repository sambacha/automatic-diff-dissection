package diffson;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 */
public class Runner {

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
		String out = "/Users/matias/develop/CodeRep-data/processed_ICSE2015_unidiff";
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);
		String input = "/Users/matias/develop/sketch-repair/outputdiff4/";
		ConfigurationProperties.properties.setProperty("icse15difffolder", input);
		analyzer.run(ConfigurationProperties.getProperty("icse15difffolder"));

	}

	@Test
	public void testD4J() throws Exception {
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "200");
		String out = "/Users/matias/develop/sketch-repair/git-sketch4repair/diff_analysis/Defects4J";
		// +"//"/Users/matias/develop/CodeRep-data/processed_d4J/";
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);
		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/";
		ConfigurationProperties.properties.setProperty("icse15difffolder", input);
		analyzer.run(ConfigurationProperties.getProperty("icse15difffolder"));
	}

	@Test
	public void testCODEREP() throws Exception {
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("max_synthesis_step", "100000");
		for (int i = 1; i <= 1; i++) {
			String out = "/Users/matias/develop/CodeRep-data/process_Dataset" + i + "_unidiff";
			DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);
			String input = "/Users/matias/develop/CodeRep-data/result_Dataset" + i + "_unidiff/";
			ConfigurationProperties.properties.setProperty("icse15difffolder", input);
			analyzer.run(ConfigurationProperties.getProperty("icse15difffolder"));
		}
	}

}
