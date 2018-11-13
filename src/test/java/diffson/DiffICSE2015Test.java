package diffson;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.google.gson.JsonArray;

import fr.inria.astor.core.setup.ConfigurationProperties;

//@Ignore
public class DiffICSE2015Test {

	@Test
	public void testFailingTimeoutCase_1555_Move() throws Exception {
		String diffId = "1555";
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("max_synthesis_step", "100000");

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailing_MaxNodes_966027() throws Exception {
		String diffId = "966027";
		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("max_synthesis_step", "100000");

		String out = "/Users/matias/develop/CodeRep-data/processed_ICSE2015" + "_unidiff";
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);

		File fileDiff = new File("/Users/matias/develop/sketch-repair/outputdiff4/" + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	ClassLoader classLoader = getClass().getClassLoader();

	public String getCompletePath(String dataset, String diffId) {
		String input = dataset + File.separator + diffId;
		File file = new File(classLoader.getResource(input).getFile());
		input = file.getAbsolutePath();
		return input;
	}

	@Test
	public void testFailingTimeoutCase_3168() throws Exception {
		String diffId = "3168";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_1792() throws Exception {
		String diffId = "1792";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_95() throws Exception {
		String diffId = "95";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_909() throws Exception {
		String diffId = "909";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_2150() throws Exception {
		String diffId = "2150";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_2954() throws Exception {
		String diffId = "2954";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_1806() throws Exception {
		String diffId = "1806";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_4185() throws Exception {
		String diffId = "4185";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testFailingTimeoutCase_584756() throws Exception {
		String diffId = "584756";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingTimeoutCase_1421510() throws Exception {

		String diffId = "1421510";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingTimeoutCase_613948() throws Exception {

		String diffId = "613948";

		runAndAssertSingleDiff(diffId);
	}

//Diff file 4185_TestTypePromotion 3
	@Test
	public void testFailingTimeoutCase_1305909() throws Exception {

		String diffId = "1305909";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingTimeoutCase_985877() throws Exception {

		String diffId = "985877";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingTimeoutCase_932564() throws Exception {

		String diffId = "932564";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testFailingCase_1103681() throws Exception {
		// To see

		String diffId = "1103681";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testNoChangesCaseCase_1329010() throws Exception {

		String diffId = "1329010";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testChangesCaseCase_1185675() throws Exception {

		String diffId = "1185675";

		runAndAssertSingleDiff(diffId);
	}

	@Test
	public void testNoChangesCase_1381711() throws Exception {

		String diffId = "1381711";

		runAndAssertSingleDiff(diffId);
	}

	public void runAndAssertSingleDiff(String case1421510) {
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + case1421510);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);
		System.out.println(arrayout);
	}

	@Test
	public void testFailingTChart_21() throws Exception {
		String diffId = "Chart_21";

		String out = "/Users/matias/develop/CodeRep-data/processed_d4J/";
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer(out);
		String input = "/Users/matias/develop/sketch-repair/git-sketch4repair/datasets/Defects4J/";
		ConfigurationProperties.properties.setProperty("icse15difffolder", input);

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}
}
