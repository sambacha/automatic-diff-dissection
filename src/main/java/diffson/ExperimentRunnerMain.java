package diffson;

import java.io.File;

import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * Experiment runners
 */
public class ExperimentRunnerMain {

	public static void main(String[] args) throws Exception {
		// String name = args[0];
		String inputpath = args[0];
		String output = args[1];

		File outFile = new File(output);
		String out = outFile.getAbsolutePath();
		outFile.mkdirs();
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);
		String input = new File(inputpath).getAbsolutePath();
		ConfigurationProperties.properties.setProperty("icse15difffolder", input);
		analyzer.run(ConfigurationProperties.getProperty("icse15difffolder"));
	}

}
