package diffson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import add.entities.PatternInstance;
import add.entities.RepairPatterns;
import add.features.detector.EditScriptBasedDetector;
import add.features.detector.repairpatterns.RepairPatternDetector;
import add.main.Config;
import fr.inria.astor.core.entities.CNTX_Property;
import fr.inria.astor.core.setup.ConfigurationProperties;
import gumtree.spoon.diff.Diff;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SuspiciousASTFaultyTest {

	/**
	 * Assert if there is at least one node marked as suspicious.
	 * 
	 * @param resultjson
	 * @param patternName
	 * @param label
	 * @param type
	 */
	public static void assertMarkedlAST(JsonObject resultjson, String patternName, String label, String type) {

		System.out.println("****************");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJsonString = gson.toJson(resultjson);

		// System.out.println(prettyJsonString);
		boolean found = false;
		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() > 0);

			// System.out.println("--> AST element: \n" + elAST);
			for (JsonElement suspiciousTree : ar) {

				JsonObject jso = suspiciousTree.getAsJsonObject();
				System.out.println("--> AST element: \n" + jso.get("pattern_name"));

				prettyJsonString = gson.toJson(jso.get("faulty_ast"));
				System.out.println("suspicious element:\n" + prettyJsonString);

				assertTrue("Equals to []", !jso.get("faulty_ast").toString().equals("[]"));
				// assertTrue(printSusp(jso.get("faulty_ast"), label, type));
				if (printSusp(jso.get("faulty_ast"), patternName, label, type)) {
					found = true;
				}

			}

		}
		assertTrue("Node suspicious not found", found);
	}

	public static List<JsonElement> getMarkedlAST(JsonObject resultjson, String fileName, String patternName) {

		System.out.println("****************");
		List<JsonElement> elements = new ArrayList<>();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJsonString = gson.toJson(resultjson);

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;

			for (JsonElement suspiciousTree : ar) {

				JsonObject jso = suspiciousTree.getAsJsonObject();
				System.out.println("--> AST element: \n" + jso.get("pattern_name"));

				JsonElement faultyElement = jso.get("faulty_ast");
				prettyJsonString = gson.toJson(faultyElement);
				System.out.println("suspicious element:\n" + prettyJsonString);

				assertTrue("Equals to []", !faultyElement.toString().equals("[]"));
				// assertTrue(printSusp(jso.get("faulty_ast"), label, type));
				if (calculateSusp(elements, faultyElement, patternName, null, null)) {
					// elements.add(faultyElement);
				}

			}

		}
		return elements;
	}

	public static void assertSuspiciousASTNode(JsonObject resultjson) {
		assertMarkedlAST(resultjson, null, null, null);
	}

	public static JsonObject getContext(String diffId, String input) {
		File fileInput = new File(input);
		System.out.println(input);

		ConfigurationProperties.setProperty("max_synthesis_step", "100000");
		ConfigurationProperties.properties.setProperty("max_synthesis_step", "100000");
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		@SuppressWarnings("unused")
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileInput);

		Map<String, Diff> diffOfcommit = analyzer.getDiffOfcommit();
		for (Diff diff : diffOfcommit.values()) {
			System.out.println("Diff " + diff);
		}

		MapCounter<String> counter = new MapCounter<>();
		MapCounter<String> counterParent = new MapCounter<>();

		analyzer.processDiff(counter, counterParent, fileInput);

		JsonObject resultjson = analyzer.calculateCntxJSON(diffId, diffOfcommit);
		return resultjson;
	}

	public static JsonElement getSusp(JsonElement ob) {
		if (ob instanceof JsonObject) {
			JsonObject jon = ob.getAsJsonObject();
			for (String s : jon.keySet()) {
				if (s.equals("susp")) {
					return jon;
				} else {

					JsonElement e = jon.get(s);
					JsonElement t1 = getSusp(e);
					if (t1 != null) {
						return t1;
					}
				}

			}
		} else {
			if (ob instanceof JsonArray) {
				JsonArray arr = ob.getAsJsonArray();
				for (JsonElement jsonElement : arr) {
					JsonElement t = getSusp(jsonElement);
					if (t != null) {
						return t;
					}
				}
			}
		}
		return null;
	}

	public static boolean hasPattern(JsonObject jon, String p) {

		// JsonPrimitive p = new JsonPrimitive("susp_" + patternName);
		// boolean has = false;
		if (!jon.has("susp"))
			return false;

		for (JsonElement el : jon.get("susp").getAsJsonArray()) {
			if (el.getAsString().toString().startsWith(p))
				return true;
		}
		return false;
		// return jon.get("susp").getAsJsonArray().contains(p);
	}

	public static boolean printSusp(JsonElement ob, String patternName, String label, String type) {
		boolean t = false;
		if (ob instanceof JsonObject) {
			JsonObject jon = ob.getAsJsonObject();
			for (String s : jon.keySet()) {
				if (s.equals("susp")) {
					// System.out.println("susp--> " + ob);
					if (label == null && type == null) {

						t = true;
						break;

					} else {

						if (label != null && type != null && jon.get("label").getAsString().toString().equals(label)
								&& jon.get("type").getAsString().toString().equals(type)
								&& (patternName == null || hasPattern(jon, ("susp_" + patternName))))
							t = true;
					}
				} else {

					JsonElement e = jon.get(s);
					boolean t1 = printSusp(e, patternName, label, type);
					if (t1) {
						return true;
					}
				}

			}
		} else {
			if (ob instanceof JsonArray) {
				JsonArray arr = ob.getAsJsonArray();
				for (JsonElement jsonElement : arr) {
					if (printSusp(jsonElement, patternName, label, type)) {
						t = true;
					}
				}
			}
		}
		return t;
	}

	public static boolean calculateSusp(List<JsonElement> elements, JsonElement ob, String patternName, String label,
			String type) {
		boolean t = false;
		if (ob instanceof JsonObject) {
			JsonObject jon = ob.getAsJsonObject();
			for (String s : jon.keySet()) {
				if (s.equals("susp")) {
					// System.out.println("susp--> " + ob);
					if (label == null && type == null && hasPattern(jon, ("susp_" + patternName))) {

						t = true;
						elements.add(jon);
						// break;

					} else {

						if (label != null && type != null && jon.get("label").getAsString().toString().equals(label)
								&& jon.get("type").getAsString().toString().equals(type)
								&& (patternName == null || hasPattern(jon, ("susp_" + patternName)))) {
							t = true;
							elements.add(jon);
						}
					}
				} else {

					JsonElement e = jon.get(s);
					boolean t1 = calculateSusp(elements, e, patternName, label, type);
					if (t1) {
						return true;
					}
				}

			}
		} else {
			if (ob instanceof JsonArray) {
				JsonArray arr = ob.getAsJsonArray();
				for (JsonElement jsonElement : arr) {
					if (calculateSusp(elements, jsonElement, patternName, label, type)) {
						t = true;
					}
				}
			}
		}
		return t;
	}

	public static void showJSONFaultyAST(JsonObject resultjson) {

		System.out.println("****************");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJsonString = gson.toJson(resultjson);

		JsonArray affected = (JsonArray) resultjson.get("affected_files");

		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() > 0);

			// System.out.println("--> AST element: \n" + elAST);
			int size = ar.size();
			int i = 0;
			for (JsonElement suspiciousTree : ar) {

				System.out.println("***");

				JsonObject jso = suspiciousTree.getAsJsonObject();
				System.out.println("--> AST element: \n" + jso.get("pattern_name"));

				prettyJsonString = gson.toJson(jso.get("faulty_ast"));
				System.out.println("--faulty_ast:\n" + (++i) + "/" + size + ": " + prettyJsonString);

			}

		}

	}

	public List<RepairPatterns> analyze(String input) {
		File fileInput = new File(input);
		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		@SuppressWarnings("unused")
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileInput);

		Map<String, Diff> diffOfcommit = analyzer.getDiffOfcommit();

		List<RepairPatterns> patterns = new ArrayList<>();
		for (Diff diff : diffOfcommit.values()) {
			System.out.println("Diff " + diff);

			RepairPatterns r = getPatterns(diff);
			patterns.add(r);

		}
		return patterns;
	}

	public boolean checkVar(PatternInstance pi1, String v1, String v2) {
		return pi1.getFaulty().stream().filter(e -> e.toString().contains(v1)).findFirst().isPresent()
				|| pi1.getFaulty().stream().filter(e -> e.toString().contains(v2)).findFirst().isPresent();
	}

	public String getCompletePath(String project, String diffId) {
		String input = project + File.separator + diffId;
		File file = new File("./datasets/" + input);
		return file.getAbsolutePath();
	}

	public String getCompletePathD4J(String diffId) {
		return getCompletePath("Defects4J/", diffId);
	}

	public JsonObject getJSonCodeRep(String diffId) {
		return getJsonData("codeRepDS1/", diffId);
	}

	public JsonObject getJsonData(String ds, String diffId) {

		// File file = new File(classLoader.getResource(input).getFile());
		File file = new File("./datasets/" + ds + File.separator + diffId);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, file.getAbsolutePath());
		return resultjson;
	}

	public JsonObject getJsonDataD4j(String diffId) {
		return getJsonData("Defects4J/", diffId);
	}

	public List<JsonElement> getPatternInstance(JsonObject resultjson) {
		List<JsonElement> contexts = new ArrayList<>();
		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;

			for (JsonElement suspiciousTree : ar) {

				JsonObject jso = suspiciousTree.getAsJsonObject();
				contexts.add(jso.get("context"));

			}
		}
		System.out.println("Number of pattern instances " + contexts.size());
		return contexts;
	}

	public RepairPatterns getPatterns(Diff diff) {
		Config config = new Config();
		EditScriptBasedDetector.preprocessEditScript(diff);
		RepairPatternDetector detector = new RepairPatternDetector(config, diff);
		RepairPatterns rp = detector.analyze();
		return rp;
	}

	private boolean hasColored(JsonElement pob, String value) {

		if (pob instanceof JsonObject) {
			JsonObject ob = (JsonObject) pob;
			if (ob.keySet().contains("op")) {

				JsonPrimitive e = (JsonPrimitive) ob.get("op");
				if (e.getAsString().equals(value))
					return true;
			}

			for (String v : ob.keySet()) {
				JsonElement je = ob.get(v);

				if (hasColored(je, value)) {
					return true;
				}

			}
		} else if (pob instanceof JsonArray) {
			JsonArray ja = (JsonArray) pob;
			for (JsonElement jsonElement : ja) {
				if (hasColored(jsonElement, value)) {
					return true;
				}
			}

		}

		return false;

	}

	@Test
	public void test_EMPTY_ICSE15_966027() throws Exception {
		String diffId = "966027";
		File file = new File("./datasets/icse2015" + File.separator + diffId);

		JsonObject resultjson = getContext(diffId, file.getAbsolutePath());

		JsonArray allch = (JsonArray) resultjson.get("info");
		System.out.println("Print " + allch);
		// assertTrue(allch.size() == 2);

		// JsonObject json0 = (JsonObject) allch.get(0);

	}

	@Test
	public void testD4closure124() throws Exception {
		String diffId = "Closure_124";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JChar14() throws Exception {
		String diffId = "Chart_14";

		JsonObject resultjson = getJsonDataD4j(diffId);
		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

	}

	@Test
	public void testD4Jchart10() throws Exception {
		String diffId = "Chart_10";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4Jchart12() throws Exception {
		String diffId = "Chart_12";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4Jchart18() throws Exception {
		String diffId = "Chart_18";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testD4Jchart21() throws Exception {
		String diffId = "Chart_21";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testD4JChart3() throws Exception {
		String diffId = "Chart_3";

		JsonObject resultjson = getJsonDataD4j(diffId);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			System.out.println("--> AST element: \n" + elAST);

		}

	}

	@Test
	public void testD4JChart4() throws Exception {
		String diffId = "Chart_4";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testD4Jclosure111() throws Exception {
		String diffId = "Closure_111";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testD4Jclosure2() throws Exception {
		String diffId = "Closure_2";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testD4Jclosure83() throws Exception {
		String diffId = "Closure_83";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JLang1() throws Exception {
		String diffId = "Lang_1";

		JsonObject resultjson = getJsonDataD4j(diffId);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			System.out.println("--> AST element: \n" + elAST);

		}

	}

	@Test
	public void testD4Jlang13() throws Exception {
		String diffId = "Lang_13";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4Jlang17() throws Exception {
		String diffId = "Lang_17";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
		// See multiple susp
	}

	@Test
	public void testD4JLang3_WrapId() throws Exception {
		String diffId = "Lang_3";

		JsonObject resultjson = getJsonDataD4j(diffId);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

	}

	@Test
	public void testD4Jlang31() throws Exception {
		String diffId = "Lang_31";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testD4Jlang33() throws Exception {
		String diffId = "Lang_33";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testD4JLang7_CBR() throws Exception {
		String diffId = "Lang_7";

		JsonObject resultjson = getJsonDataD4j(diffId);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

	}

	@Test
	public void testD4JMath_55() throws Exception {
		String diffId = "Math_55";

		JsonObject resultjson = getJsonDataD4j(diffId);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			System.out.println("--> AST element: \n" + elAST);

		}

	}

	@Test
	public void testD4JMath_88() throws Exception {
		String diffId = "Math_88";
		ConfigurationProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "200");
		JsonObject resultjson = getJsonDataD4j(diffId);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected");
		for (JsonElement jsonElement : affected) {
			JsonArray affAr = (JsonArray) jsonElement;
			for (JsonElement aff : affAr) {

				JsonObject jo = (JsonObject) aff;
				JsonElement elAST = jo.get("ast");
				System.out.println("--> AST element: \n" + elAST);
			}
		}

	}

	@Test
	public void testD4Jmath105() throws Exception {
		String diffId = "Math_105";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	@Ignore
	public void testD4Jmath27() throws Exception {
		String diffId = "Math_27";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JMath4() throws Exception {
		String diffId = "Math_4";

		JsonObject resultjson = getJsonDataD4j(diffId);
		System.out.println(resultjson);
	}

	@Test
	public void testD4Jmath46() throws Exception {
		String diffId = "Math_46";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4Jmath60() throws Exception {
		String diffId = "Math_60";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JMath75() throws Exception {
		String diffId = "Math_75";

		JsonObject resultjson = getJsonDataD4j(diffId);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected");
		for (JsonElement jsonElement : affected) {
			JsonArray affAr = (JsonArray) jsonElement;
			for (JsonElement aff : affAr) {

				JsonObject jo = (JsonObject) aff;
				JsonElement elAST = jo.get("ast");
				// System.out.println("--> AST element: \n" + elAST);
			}
		}

	}

	@Test
	@Ignore
	public void testD4Jmockito14() throws Exception {
		String diffId = "time_8";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JTime_11() throws Exception {
		String diffId = "Time_11";
		ConfigurationProperties.properties.setProperty("MAX_AST_CHANGES_PER_FILE", "200");
		JsonObject resultjson = getJsonDataD4j(diffId);

		System.out.println(resultjson);
		// assertTrue(resultjson.get("patterns"))

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("faulty_stmts_ast");
			System.out.println("--> AST element: \n" + elAST);

		}

	}

	@Test
	public void testD4Jtime18() throws Exception {
		String diffId = "time_18";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JTime5() throws Exception {
		String diffId = "Time_5";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testD4Jtime8() throws Exception {
		String diffId = "time_8";

		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4Math_7() throws Exception {
		String diffId = "Math_7";
		JsonObject resultjson = getJsonDataD4j(diffId);

		assertSuspiciousASTNode(resultjson);
	}

	@Test
	@Ignore // NOT pattern instance
	public void testICSE15_1264_NPE() throws Exception {
		String diffId = "1264";

		JsonObject resultjson = getJSonCodeRep(diffId);

		System.out.println(resultjson);

		List<JsonElement> elements = getPatternInstance(resultjson);

		assertTrue(elements.size() > 0);

		JsonObject json0 = (JsonObject) elements.get(0);

		JsonPrimitive ops = (JsonPrimitive) json0.get("bug")// .getAsJsonObject().get(CNTX_Property.AFFECTED.toString())
				.getAsJsonObject().get(CNTX_Property.OPERATION.toString());

		assertEquals("UPD", ops.getAsString());

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("java.lang.String REPLICATION_UNABLE_TO_STOP_MASTER = \\\"XRE07\\\";", buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("java.lang.String REPLICATION_NOT_IN_MASTER_MODE = \\\"XRE07\\\";", buggyCode1.getAsString());

	}

	@Test
	public void testICSE15_1290_update() throws Exception {
		String diffId = "1290";

		JsonObject resultjson = getJSonCodeRep(diffId);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);
		System.out.println("");

	}

	@Test
	public void testICSE15_1555_Move_new() throws Exception {
		String diffId = "1555";

		JsonObject resultjson = getJSonCodeRep(diffId);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 2);

		JsonObject json0 = (JsonObject) allch.get(0);

		// INSERT

		Object buggyCode = json0.get("bug").getAsJsonObject().get(CNTX_Property.AFFECTED.toString());
		assertEquals("null", buggyCode.toString());

		JsonPrimitive typeBuggyParent = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get("TYPE");
		assertEquals("CtLocalVariableImpl", typeBuggyParent.getAsString());

		/// Move
		JsonObject json1 = (JsonObject) allch.get(1);
		System.out.println(json1);

		JsonPrimitive buggyCode1 = (JsonPrimitive) json1.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				// "(userStartTimeout != null) ? java.lang.Long.parseLong(userStartTimeout) :
				// org.apache.derbyTesting.functionTests.tests.replicationTests.ReplicationRun.DEFAULT_SERVER_START_TIMEOUT",
				"java.lang.Long.parseLong(userStartTimeout)", buggyCode1.getAsString());

		JsonArray asJsonArray = json1.get("bug").getAsJsonObject().get(CNTX_Property.AFFECTED_PARENT.toString())
				.getAsJsonArray();

		JsonPrimitive buggyCodeParent = (JsonPrimitive) asJsonArray.get(0).getAsJsonObject()
				.get(CNTX_Property.CODE.toString());
		// System.out.println(buggyCodeParent);
		assertEquals(
				"long startTimeout = (userStartTimeout != null) ? java.lang.Long.parseLong(userStartTimeout) : org.apache.derbyTesting.functionTests.tests.replicationTests.ReplicationRun.DEFAULT_SERVER_START_TIMEOUT"

				, buggyCodeParent.getAsString());

		///

		JsonPrimitive patchCode = (JsonPrimitive) json1.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("java.lang.Long.parseLong(userStartTimeout)", patchCode.getAsString());

		JsonArray asJsonArraypatch = json1.get("patch").getAsJsonObject().get(CNTX_Property.AFFECTED_PARENT.toString())
				.getAsJsonArray();

		assertTrue(asJsonArraypatch.size() == 1);
		JsonPrimitive patchCodeParent = (JsonPrimitive) asJsonArraypatch.get(0).getAsJsonObject()
				.get(CNTX_Property.CODE.toString());

		System.out.println(patchCodeParent.getAsString());
		assertEquals(
				"long startTimeout = (userStartTimeout != null) ? (java.lang.Long.parseLong(userStartTimeout)) * 1000 : org.apache.derbyTesting.functionTests.tests.replicationTests.ReplicationRun.DEFAULT_SERVER_START_TIMEOUT",
				patchCodeParent.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "INS"));

	}

	@Test
	public void testICSE15_2_update() throws Exception {
		String diffId = "2";

		JsonObject resultjson = getJSonCodeRep(diffId);

		List<JsonElement> elements = getPatternInstance(resultjson);

		assertTrue(elements.size() > 0);

		JsonObject json0 = (JsonObject) elements.get(0);
		System.out.println(json0);

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("pageSize", buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("averages = new float[pageSize]", buggyCode1.getAsString());

		/////
		System.out.println(json0);
		JsonPrimitive patchCode = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("initialPageCount", patchCode.getAsString());

		JsonPrimitive patchCodeP = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("averages = new float[initialPageCount]", patchCodeP.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "UPD"));

	}

	@Test
	public void testICSE15_2875_NPE() throws Exception {
		String diffId = "2875";

		JsonObject resultjson = getJSonCodeRep(diffId);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 2);
		assertTrue(hasColored(((JsonObject) allch.get(0)).get(CNTX_Property.AST_PARENT.toString()), "UPD"));
		assertTrue(hasColored(((JsonObject) allch.get(1)).get(CNTX_Property.AST_PARENT.toString()), "UPD"));
		assertFalse(hasColored(((JsonObject) allch.get(0)).get(CNTX_Property.AST_PARENT.toString()), "DEL"));
		assertFalse(hasColored(((JsonObject) allch.get(1)).get(CNTX_Property.AST_PARENT.toString()), "DEL"));
		assertFalse(hasColored(((JsonObject) allch.get(0)).get(CNTX_Property.AST_PARENT.toString()), "INS"));
		assertFalse(hasColored(((JsonObject) allch.get(1)).get(CNTX_Property.AST_PARENT.toString()), "INS"));
	}

	@Test
//	@Ignore // TO UPDATE
	public void testICSE15_4117_MOVE() throws Exception {
		String diffId = "4117";

		JsonObject resultjson = getJSonCodeRep(diffId);
		System.out.println(resultjson);

		List<JsonElement> elements = getPatternInstance(resultjson);

		assertTrue(elements.size() == 1);

		JsonObject jsonMove = (JsonObject) elements.get(0);
		System.out.println(jsonMove);

		JsonPrimitive buggyCode = (JsonPrimitive) jsonMove.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("(bytes.position())", buggyCode.getAsString());

		JsonArray buggyCodeParents = (JsonArray) jsonMove.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonArray();

		assertEquals(1, buggyCodeParents.size());

		JsonPrimitive bl1 = buggyCodeParents.get(0).getAsJsonObject().get(CNTX_Property.CODE.toString())
				.getAsJsonPrimitive();
		System.out.println(bl1);
		assertEquals("return java.lang.String.valueOf(bytes.getLong(((bytes.position()) + (bytes.arrayOffset()))))",
				bl1.getAsString());

		// PATCH

		JsonPrimitive patchCode = (JsonPrimitive) jsonMove.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("(bytes.position())", patchCode.getAsString());

		JsonArray asJsonArraypatch = jsonMove.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonArray();

		System.out.println("\n" + asJsonArraypatch);
		assertTrue(asJsonArraypatch.size() == 1);
		JsonPrimitive patchCodeParent = (JsonPrimitive) asJsonArraypatch.get(0).getAsJsonObject()
				.get(CNTX_Property.CODE.toString());

		System.out.println(patchCodeParent);
		assertEquals("return java.lang.String.valueOf(bytes.getLong(bytes.position()))", patchCodeParent.getAsString());

		// assertTrue(hasColored((JsonObject)
		// json0.get(CNTX_Property.AST_PARENT.toString()), "INS"));

	}

	@Test
	public void testICSE15_5_insert() throws Exception {
		String diffId = "5";

		JsonObject resultjson = getJSonCodeRep(diffId);

		JsonArray allch = (JsonArray) resultjson.get("info");
		System.out.println(allch);
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);

		// JsonPrimitive buggyCode = (JsonPrimitive)
		// json0.get("bug").getAsJsonObject().get(CNTX_Property.AFFECTED.toString()).getAsJsonObject()
		// .get(CNTX_Property.CODE.toString());

		JsonNull buggyCode = (JsonNull) json0.get("bug").getAsJsonObject().get(CNTX_Property.AFFECTED.toString());

		// assertEquals("new org.apache.lucene.index.RandomIndexWriter(random(), dir)",
		// buggyCode.getAsString());
		assertEquals("null", buggyCode.toString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				"org.apache.lucene.index.RandomIndexWriter iw = new org.apache.lucene.index.RandomIndexWriter(random(), dir)",
				buggyCode1.getAsString());

		// Patch

		JsonPrimitive patchCode = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("cfg", patchCode.getAsString());

		JsonPrimitive patchCodeParent = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				"org.apache.lucene.index.RandomIndexWriter iw = new org.apache.lucene.index.RandomIndexWriter(random(), dir, cfg)",
				patchCodeParent.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "INS"));
	}

	@Test
	public void testICSE15_61_update() throws Exception {
		String diffId = "61";

		JsonObject resultjson = getJSonCodeRep(diffId);

		JsonArray allch = (JsonArray) resultjson.get("info");
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("org.apache.lucene.index.IndexFileNames.segmentFileName(id, \\\"\\\", Writer.DATA_EXTENSION)",
				buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		System.out.println(buggyCode1);
		assertEquals(
				"datIn = dir.openInput(org.apache.lucene.index.IndexFileNames.segmentFileName(id, \\\"\\\", Writer.DATA_EXTENSION), context)",
				buggyCode1.getAsString());

		/////
		System.out.println(json0);
		JsonPrimitive patchCode = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				"org.apache.lucene.index.IndexFileNames.segmentFileName(id, Bytes.DV_SEGMENT_SUFFIX, Writer.DATA_EXTENSION)",
				patchCode.getAsString());

		JsonPrimitive patchCodeP = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals(
				"datIn = dir.openInput(org.apache.lucene.index.IndexFileNames.segmentFileName(id, Bytes.DV_SEGMENT_SUFFIX, Writer.DATA_EXTENSION), context)",
				patchCodeP.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "UPD"));

	}

	@Test
	public void testICSE15_65_replace() throws Exception {
		String diffId = "65";

		JsonObject resultjson = getJSonCodeRep(diffId);

		System.out.println(resultjson);
	}

	@Test
	public void testICSE15_694_Delete() throws Exception {
		String diffId = "694";

		JsonObject resultjson = getJSonCodeRep(diffId);
		System.out.println(resultjson);
		JsonArray allch = (JsonArray) resultjson.get("info");
		System.out.println(allch);
		assertTrue(allch.size() == 1);

		JsonObject json0 = (JsonObject) allch.get(0);

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("IndexWriter.MaxFieldLength.LIMITED", buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());
		System.out.println(buggyCode1);
		assertEquals(
				"org.apache.lucene.index.IndexWriter writer = new org.apache.lucene.index.IndexWriter(runData.getDirectory(), config.get(\\\"autocommit\\\", org.apache.lucene.benchmark.byTask.tasks.OpenIndexTask.DEFAULT_AUTO_COMMIT), runData.getAnalyzer(), false, IndexWriter.MaxFieldLength.LIMITED)",
				buggyCode1.getAsString());

		///
		JsonNull patchCode = (JsonNull) json0.get("patch").getAsJsonObject().get(CNTX_Property.AFFECTED.toString());
		System.out.println(patchCode);
		assertEquals("null", patchCode.toString());

		JsonPrimitive patchCode1 = (JsonPrimitive) json0.get("patch").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());
		System.out.println(patchCode1);
		assertEquals(
				"org.apache.lucene.index.IndexWriter writer = new org.apache.lucene.index.IndexWriter(runData.getDirectory(), config.get(\\\"autocommit\\\", org.apache.lucene.benchmark.byTask.tasks.OpenIndexTask.DEFAULT_AUTO_COMMIT), runData.getAnalyzer(), false)",
				patchCode1.getAsString());

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "DEL"));

	}

	@Test
	public void testICSE15_70_delete() throws Exception {
		String diffId = "70";

		JsonObject resultjson = getJSonCodeRep(diffId);

		JsonArray allch = (JsonArray) resultjson.get("info");
		System.out.println(allch);
		assertTrue(allch.size() == 3);

		JsonObject json0 = (JsonObject) allch.get(0);

		JsonPrimitive buggyCode = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		assertEquals("!(isSunJVM())", buggyCode.getAsString());

		JsonPrimitive buggyCode1 = (JsonPrimitive) json0.get("bug").getAsJsonObject()
				.get(CNTX_Property.AFFECTED_PARENT.toString()).getAsJsonObject().get(CNTX_Property.CODE.toString());

		System.out.println(buggyCode1);
		assertTrue(buggyCode1.getAsString().startsWith("if (!(isSunJVM())) "));

		assertTrue(hasColored((JsonObject) json0.get(CNTX_Property.AST_PARENT.toString()), "DEL"));

		JsonObject json1 = (JsonObject) allch.get(1);

		assertTrue(hasColored((JsonObject) json1.get(CNTX_Property.AST_PARENT.toString()), "INS"));

		JsonObject json2 = (JsonObject) allch.get(2);

		assertTrue(hasColored((JsonObject) json2.get(CNTX_Property.AST_PARENT.toString()), "MOV"));
	}

	@Test
	@Ignore
	public void testICSE15_986499() throws Exception {
		String diffId = "986499";

		DiffContextAnalyzer analyzer = new DiffContextAnalyzer();

		File fileDiff = new File(ConfigurationProperties.getProperty("icse15difffolder") + "/" + diffId);
		JsonArray arrayout = analyzer.processDiff(new MapCounter<>(), new MapCounter<>(), fileDiff);

		assertTrue(arrayout.size() > 0);

		analyzer.atEndCommit(fileDiff);

	}

	@Test
	public void testD4JVChar20() {
		String diffId = "Chart_20";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);

		assertTrue(checkVar(pi1, "stroke", "paint"));

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("super(paint, stroke, paint, stroke, alpha)"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		PatternInstance pi2 = insts.get(0);

		assertTrue(checkVar(pi2, "stroke", "paint"));

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongVarRef", "stroke", "VariableRead");
	}

	@Test
	public void testD4JVChart1() {
		String diffId = "Chart_1";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		Assert.assertTrue(repairPatterns.getFeatureCounter("binOperatorModif") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("binOperatorModif");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("dataset == null"));

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("dataset != null"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "binOperatorModif", "NE", "BinaryOperator");

	}

	@Test
	public void testD4JVchart10() throws Exception {
		String diffId = "Chart_10";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("ImageMapUtilities.htmlEscape(toolTipText)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("toolTipText")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("return (\" title=\\\"\" + toolTipText) + \"\\\" alt=\\\"\\\"\"", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JVChart11() {
		String diffId = "Chart_11";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("p1")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString()
				.equals("java.awt.geom.PathIterator iterator2 = p1.getPathIterator(null)"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongVarRef", "p1", "VariableRead");
	}

	@Test
	public void testD4JVchart12() throws Exception {
		String diffId = "Chart_12";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("setDataset(dataset)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("this.dataset")).findFirst()

				// assertTrue(pi1.getFaulty().stream().filter(e ->
				// e.toString().contains("this.dataset = dataset")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("this.dataset = dataset", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JVChart13() throws Exception {
		String diffId = "Chart_13";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		// assertTrue(pi1.getNodeAffectedOp().toString()
		// .contains("Math.max(0.0, ((sumYY) - (((sumXY) * (sumXY)) / (sumXX))))"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("((constraint.getWidth()) - (w[2]))"))
				.findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals(
				"org.jfree.chart.block.RectangleConstraint c4 = new org.jfree.chart.block.RectangleConstraint(0.0, new org.jfree.data.Range(0.0, ((constraint.getWidth()) - (w[2]))), LengthConstraintType.RANGE, h[2], null, LengthConstraintType.FIXED)",
				pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JVChart15() throws Exception {
		String diffId = "Chart_15";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIf") > 0);
		Assert.assertTrue(repairPatterns.getFeatureCounter("missNullCheckP") > 0);
		Assert.assertTrue(repairPatterns.getFeatureCounter("missNullCheckN") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("(this.dataset) != null"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains(
				"state.setTotal(org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(plot.getDataset())"))
				.findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals(
				"state.setTotal(org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(plot.getDataset()))",
				pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIf", "org.jfree.chart.plot.PiePlotState#setTotal()",
				"Invocation");

		// The second pattern:
		insts = repairPatterns.getPatternInstances().get("missNullCheckP");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("(this.dataset) == null"));
		// assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("double result = 0.0")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("double result = 0.0", pi1.getFaultyLine().toString());
		// assertEquals(1, pi1.getFaulty().size());

		// The third pattern:
		insts = repairPatterns.getPatternInstances().get("missNullCheckN");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("(this.dataset) != null"));
		// assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains(
				"state.setTotal(org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(plot.getDataset()))"))
				.findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals(
				"state.setTotal(org.jfree.data.general.DatasetUtilities.calculatePieDatasetTotal(plot.getDataset()))",
				pi1.getFaultyLine().toString());
		assertEquals(1, pi1.getFaulty().size());

		// Check that there are two suspicious, and one with two patterns

		// To check Patterns instances
		JsonArray affected = (JsonArray) resultjson.get("affected_files");

		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 2);
			boolean hasBoth = false;
			for (JsonElement e : ar) {
				JsonElement el = SuspiciousASTFaultyTest.getSusp(e);
				if (SuspiciousASTFaultyTest.hasPattern((JsonObject) el, "susp_missNullCheckN")
						&& SuspiciousASTFaultyTest.hasPattern((JsonObject) el, "susp_wrapsIf"))
					hasBoth = true;
			}
			assertTrue(hasBoth);
		}

	}

	@Test
	public void testD4JVChart18() {
		String diffId = "Chart_18";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() == 2);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIf") > 0);

		List<PatternInstance> listWrap = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(listWrap);
		assertTrue(listWrap.size() > 0);

		PatternInstance pi1 = listWrap.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("index >= 0"));
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("rowData.removeValue(columnKey)"))
				.findFirst().isPresent());

		List<PatternInstance> insts = patterns.get(1).getPatternInstances().get("unwrapIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("rebuildIndex()"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("if (index < (this.keys.size()))"))
				.findFirst().isPresent());
		assertTrue(pi1.getFaultyLine().toString().contains("if (index < (this.keys.size())) {"));
		assertNotNull(pi1.getFaultyTree());
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "unwrapIfElse", "if", "If");

	}

	@Test
	public void testD4JVchart21() throws Exception {
		String diffId = "Chart_21";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsElse") > 0);

		// Pattern 1
		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("double minval = java.lang.Double.NaN"))
				.findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("double minval = java.lang.Double.NaN", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsElse", "minval", "LocalVariable");

		// Pattern addassignment

		insts = repairPatterns.getPatternInstances().get("addassignment");
		System.out.println(insts);
		assertTrue(insts.size() == 4);

		pi1 = insts.get(2);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("this.maximumRangeValue = java.lang.Double.NaN")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("this.maximumRangeValue = java.lang.Double.NaN", pi1.getFaultyLine().toString());

		System.out.println("END 1\n" + resultjson.toString());
		// TODO:
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "addassignment", "\u003d"/* "maximumRangeValue" */,
				"Assignment");

	}

	@Test
	public void testD4JVchart22() throws Exception {
		String diffId = "Chart_22";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("row")).findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("row >= 0", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongVarRef", "row", "VariableRead");

		// Pattern 2
		insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		pi1 = insts.get(0);
		System.out.println(pi1);
	}

	@Test
	public void testD4JVchart26() throws Exception {
		String diffId = "Chart_26";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIf") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("owner != null"));
		assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("EntityCollection entities = owner.getEntityCollection()"))
				.findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("org.jfree.chart.entity.EntityCollection entities = owner.getEntityCollection()",
				pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIf", "entities", "LocalVariable");
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "missNullCheckN", "entities", "LocalVariable");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 1);
			boolean hasBoth = false;
			for (JsonElement e : ar) {
				JsonElement el = SuspiciousASTFaultyTest.getSusp(e);
				if (SuspiciousASTFaultyTest.hasPattern((JsonObject) el, "susp_missNullCheckN")
						&& SuspiciousASTFaultyTest.hasPattern((JsonObject) el, "susp_wrapsIf"))
					hasBoth = true;
			}
			assertTrue(hasBoth);
		}

	}

	@Test
	public void testD4JVChart3() {
		String diffId = "Chart_3";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		Assert.assertTrue(repairPatterns.getFeatureCounter("addassignment") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("addassignment");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("java.lang.Double.NaN"));

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("copy.data = new java.util.ArrayList()"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "addassignment", "\u003d", "Assignment");

	}

	@Test
	public void testD4JVchart4() throws Exception {
		String diffId = "Chart_4";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIf") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		assertEquals(3, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("java.util.Collection c = r.getAnnotations()")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertEquals("java.util.Collection c = r.getAnnotations()", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIf", "c", "LocalVariable");
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "missNullCheckN", "c", "LocalVariable");

	}

	@Test
	public void testD4JVChart5() throws Exception {
		String diffId = "Chart_5";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicReduce");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);

		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());

		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().equals("(!(this.allowDuplicateXValues))"))
				.findFirst().isPresent());
		// assertTrue(pi1.getFaulty().stream().filter(e ->
		// e.toString().equals("this.allowDuplicateXValues")).findFirst()
		// .isPresent());
		assertNotNull(pi1.getFaultyTree());
		// assertNotNull(pi1.getFaultyTree());
		assertEquals("(index >= 0) && (!(this.allowDuplicateXValues))", pi1.getFaultyLine().toString());
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// TODO:
		// JSonTest.showAST(resultjson, "expLogicExpand", "AND"/* "maximumRangeValue"
		// */, "BinaryOperator");

	}

	@Test
	@Ignore
	public void testD4JVClosure102() {
		String diffId = "Closure_102";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("codeMove");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.startsWith("if (com.google.javascript.jscomp.Normalize.MAKE_LOCAL_NAMES_UNIQUE) "));
		// assertEquals(2, pi1.getFaulty().size());
		// assertTrue(pi1.getFaulty().stream().filter(e ->
		// e.toString().contains("false")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"new com.google.javascript.jscomp.Normalize.PropogateConstantAnnotations(compiler, assertOnChange).process(externs, root)"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.assertMarkedlAST(resultjson, "constChange", "false", "Literal");
	}

	@Test
	public void testD4JVClosure103() throws Exception {
		String diffId = "Closure_103";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);
		Assert.assertTrue(repairPatterns.getFeatureCounter("expLogicExpand") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicExpand");

		System.out.println("Values " + repairPatterns.getPatternInstances().values());

		assertNull(insts);
		System.out.println(insts);

	}

	@Test
	public void testD4JVClosure11() {
		String diffId = "Closure_11";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);
	}

	@Test
	public void testD4JVClosure111() {
		String diffId = "Closure_111";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIfElse") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("topType.isAllType()"));
		assertTrue(pi1.getFaulty().size() == 1);
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("topType")).findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIfElse", "topType", "VariableRead");
	}

	@Test
	public void testD4JVClosure17() throws Exception {
		String diffId = "Closure_17";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);
		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

	}

	////

	@Test
	public void testD4JVClosure2() {

		String diffId = "Closure_2";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() == 1);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIfElse") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("implicitProto == null"));
		assertTrue(pi1.getFaulty().size() == 1);
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("currentPropertyNames = implicitProto.getOwnPropertyNames()"))
				.findFirst().isPresent());
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIfElse", "\u003d", "Assignment");

		Assert.assertTrue(repairPatterns.getFeatureCounter("missNullCheckP") > 0);

		///
		insts = repairPatterns.getPatternInstances().get("missNullCheckP");

		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);

		assertTrue(pi1.getFaultyLine().toString().equals("currentPropertyNames = implicitProto.getOwnPropertyNames()"));

	}

	@Test
	public void testD4JVClosure20() throws Exception {
		String diffId = "Closure_20";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicExpand");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);
		assertTrue(pi1.getNodeAffectedOp().toString().contains(
				"((value != null) && ((value.getNext()) == null)) && (com.google.javascript.jscomp.NodeUtil.isImmutableValue(value))"));
		assertNotNull(pi1.getFaultyTree());
		assertTrue(
				pi1.getFaulty().stream().filter(e -> e.toString().contains("value != null")).findFirst().isPresent());

		// assertTrue(pi1.getFaultyLine().toString().equals("value != null"));
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
	}

	@Test
	public void testD4JVClosure213() {
		String diffId = "Closure_123";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("com.google.javascript.jscomp.CodeGenerator.Context.OTHER"))
				.findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"com.google.javascript.jscomp.CodeGenerator.Context rhsContext = com.google.javascript.jscomp.CodeGenerator.Context.OTHER"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongVarRef", "OTHER", "FieldRead");
	}

	@Test
	public void testD4JVClosure25() throws Exception {
		String diffId = "Closure_25";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrongMethodRef") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongMethodRef");
		System.out.println(insts);
		assertEquals(1, insts.size());

		PatternInstance pi1 = insts.stream().filter(e -> e.getPatternName().equals("wrongMethodRef")).findFirst().get();
		// assertTrue(pi1.getNodeAffectedOp().toString().contains("setElitismRate(elitismRate)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().equals("traverse(constructor, scope)")).findFirst()
				.isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("scope = traverse(constructor, scope)", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongMethodRef",
				"com.google.javascript.jscomp.TypeInference#traverse(com.google.javascript.rhino.Node,com.google.javascript.jscomp.type.FlowScope)",
				"Invocation");

		// To check Patterns instances
		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			// assertTrue(ar.size() == 2);

		}
		System.out.println("End 1\n " + resultjson);
		// SECOND pattern move

		// AVOID moves
		if (false) {
			List<PatternInstance> instsMove = repairPatterns.getPatternInstances().get("codeMove");
			System.out.println("Pattern move: ");
			System.out.println(instsMove);
			assertEquals(1, instsMove.size());

			pi1 = instsMove.stream().filter(e -> e.getPatternName().equals("codeMove")).findFirst().get();
			assertTrue(pi1.getNodeAffectedOp().toString().contains("Node constructor = n.getFirstChild()"));
			// assertEquals(1, pi1.getFaulty().size());
			// assertTrue(pi1.getFaulty().stream().filter(e ->
			// e.toString().equals("constructor.getJSType()")).findFirst()
			// .isPresent());

			assertNotNull(pi1.getFaultyTree());
			assertEquals("com.google.javascript.rhino.jstype.JSType constructorType = constructor.getJSType()",
					pi1.getFaultyLine().toString());

			// resultjson = JSonTest.getContext(diffId, input);
			// System.out.println("End 2\n " + resultjson);
			// JSonTest.showAST(resultjson);
			SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "codeMove", "constructorType", "LocalVariable");

		}
	}

	@Test
	public void testD4JVClosure30() {
		String diffId = "Closure_30";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);
	}

	@Test
	public void testD4JVClosure40() {
		String diffId = "Closure_40";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("constChange");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("false")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString()
				.equals("com.google.javascript.jscomp.NameAnalyzer.JsName name = getName(ns.name, false)"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "constChange", "false", "Literal");
	}

	@Test
	public void testD4JVClosure58() throws Exception {
		String diffId = "Closure_58";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		assertTrue(patterns.size() > 0);
		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.contains("if (com.google.javascript.jscomp.NodeUtil.isName(lhs)) {"));

		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().startsWith("addToSetIfLocal(lhs, kill)"))
				.findFirst().isPresent());

		assertTrue(pi1.getFaultyLine().toString().equals("addToSetIfLocal(lhs, kill)"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIfElse",
				"com.google.javascript.jscomp.LiveVariablesAnalysis#addToSetIfLocal(com.google.javascript.rhino.Node,java.util.BitSet)",
				"Invocation");

	}

	@Test
	public void testD4JVClosure75() {
		String diffId = "Closure_75";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("com.google.javascript.rhino.jstype.TernaryValue.TRUE")).findFirst()
				.isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(
				pi1.getFaultyLine().toString().equals("return com.google.javascript.rhino.jstype.TernaryValue.TRUE"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongVarRef", "TRUE", "FieldRead");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 1);

		}

	}

	@Test
	public void testD4JVClosure88() throws Exception {
		String diffId = "Closure_88";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);
		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);
	}

	@Test
	public void testD4JVClosure9() {
		String diffId = "Closure_9";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "unwrapMethod",
				"com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
				"Invocation");
	}

	@Test
	public void testD4JVClosure90() {
		String diffId = "Closure_90";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");
	}

	@Test
	public void testD4JVClosure96() throws Exception {
		String diffId = "Closure_96";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicExpand");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);

		// assertTrue(pi1.getNodeAffectedOp().toString().contains("r != null"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("(arguments.hasNext())")).findFirst()
				.isPresent());
		assertNotNull(pi1.getFaultyTree());
		// assertNotNull(pi1.getFaultyTree());
		assertEquals("(arguments.hasNext()) && (parameters.hasNext())", pi1.getFaultyLine().toString());
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// TODO:
		// JSonTest.showAST(resultjson, "expLogicExpand", "AND"/* "maximumRangeValue"
		// */, "BinaryOperator");

	}

	@Test
	public void testD4JVLang17() {
		String diffId = "Lang_17";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("unwrapIfElse") > 0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("unwrapMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.contains("pos += java.lang.Character.charCount(java.lang.Character.codePointAt(input, pos))"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("if (pos < (len - 2))")).findFirst()
				.isPresent());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("pos++")).findFirst().isPresent());

		assertTrue(pi1.getFaultyLine().toString().contains("if (pos < (len - 2))"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "unwrapIfElse", "if", "If");

		// Second pattern
		insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("input.length()"));
		// assertEquals(2, pi1.getFaulty().size());
		// assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("if
		// (pos < (len - 2))")).findFirst()
		// .isPresent());
		// assertTrue(pi1.getFaulty().stream().filter(e ->
		// e.toString().contains("pos++")).findFirst().isPresent());
		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString()
				.equals("int len = java.lang.Character.codePointCount(input, 0, input.length())"));

		resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "unwrapMethod",
				"java.lang.Character#codePointCount(java.lang.CharSequence,int,int)", "Invocation");

	}

	@Test
	public void testD4JVLang21() {
		String diffId = "Lang_21";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("java.util.Calendar.HOUR")).findFirst()
				.isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"return ((((((((cal1.get(java.util.Calendar.MILLISECOND)) == (cal2.get(java.util.Calendar.MILLISECOND))) && ((cal1.get(java.util.Calendar.SECOND)) == (cal2.get(java.util.Calendar.SECOND)))) && ((cal1.get(java.util.Calendar.MINUTE)) == (cal2.get(java.util.Calendar.MINUTE)))) && ((cal1.get(java.util.Calendar.HOUR)) == (cal2.get(java.util.Calendar.HOUR)))) && ((cal1.get(java.util.Calendar.DAY_OF_YEAR)) == (cal2.get(java.util.Calendar.DAY_OF_YEAR)))) && ((cal1.get(java.util.Calendar.YEAR)) == (cal2.get(java.util.Calendar.YEAR)))) && ((cal1.get(java.util.Calendar.ERA)) == (cal2.get(java.util.Calendar.ERA)))) && ((cal1.getClass()) == (cal2.getClass()))"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.showAST(resultjson, "wrongVarRef", "TRUE", "FieldRead");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 1);

		}

	}

	@Test
	public void testD4JVLang32() {
		String diffId = "Lang_32";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");
	}

	@Test
	public void testD4JVLang33() {

		String diffId = "Lang_33";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsIfElse") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("(array[i]) == null"));
		assertTrue(pi1.getFaulty().size() == 1);
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("array[i].getClass()")).findFirst()
				.isPresent());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIfElse", "java.lang.Object#getClass()",
				"Invocation");
	}

	@Test
	public void testD4JVMath_26() throws Exception {
		String diffId = "Math_26";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.stream().filter(
				e -> e.getNodeAffectedOp().toString().equals("(org.apache.commons.math3.util.FastMath.abs(p2))"))
				.findFirst().get();
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("p2")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("(p2 > overflow) || (q2 > overflow)", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);

		// To check duplicates
		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 2);

		}
	}

	@Test
	public void testD4JVMath_28() throws Exception {
		String diffId = "Math_28";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains(
		// "((value != null) && ((value.getNext()) == null)) &&
		// (com.google.javascript.jscomp.NodeUtil.isImmutableValue(value))"));

		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().startsWith("for (java.lang.Integer row : minRatioPositions) {")).findFirst()
				.isPresent());

		// assertTrue(pi1.getFaultyLine().toString().equals("value != null"));
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIf", "", "ForEach");
	}

	@Test
	public void testD4JVmath105() throws Exception {
		String diffId = "Math_105";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.contains("Math.max(0.0, ((sumYY) - (((sumXY) * (sumXY)) / (sumXX))))"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("((sumYY) - (((sumXY) * (sumXY)) / (sumXX)))")).findFirst()
				.isPresent());

		assertTrue(pi1.getFaultyTree() != null);
		assertEquals("return (sumYY) - (((sumXY) * (sumXY)) / (sumXX))", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testD4JVMath35() throws Exception {
		String diffId = "Math_35";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("setElitismRate(elitismRate)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(
				pi1.getFaulty().stream().filter(e -> e.toString().equals("this.elitismRate")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("this.elitismRate = elitismRate", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsMethod", "elitismRate", "FieldWrite");
	}

	@Test
	public void testD4JVMath46() {
		String diffId = "Math_46";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		Assert.assertTrue(repairPatterns.getFeatureCounter("unwrapIfElse") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("NaN"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("isZero")).findFirst().isPresent());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("INF")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"return isZero ? org.apache.commons.math.complex.Complex.NaN : org.apache.commons.math.complex.Complex.INF"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "unwrapIfElse", "INF", "FieldRead");

	}

	@Test
	public void testD4JVMath5() {
		String diffId = "Math_5";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream()
				.filter(e -> e.toString().contains("org.apache.commons.math3.complex.Complex.NaN")).findFirst()
				.isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("return org.apache.commons.math3.complex.Complex.NaN"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongVarRef", "NaN", "FieldRead");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 1);

		}

	}

	@Test
	public void testD4JVMath54() throws Exception {
		String diffId = "Math_54";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		System.out.println(pi1);
		// assertTrue(pi1.getNodeAffectedOp().toString().contains(
		// "((value != null) && ((value.getNext()) == null)) &&
		// (com.google.javascript.jscomp.NodeUtil.isImmutableValue(value))"));

		assertTrue(
				pi1.getFaulty().stream().filter(e -> e.toString().startsWith("y = negate()")).findFirst().isPresent());

		// assertTrue(pi1.getFaultyLine().toString().equals("value != null"));
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrapsIf", "\u003d", "Assignment");
	}

	@Test
	public void testD4JVMath76() {
		String diffId = "Math_76";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("p")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"final org.apache.commons.math.linear.RealMatrix e = eigenDecomposition.getV().getSubMatrix(0, (p - 1), 0, (p - 1))"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		// JSonTest.showAST(resultjson, "wrongVarRef", "NaN", "FieldRead");

		JsonArray affected = (JsonArray) resultjson.get("affected_files");
		for (JsonElement jsonElement : affected) {

			JsonObject jo = (JsonObject) jsonElement;
			// JsonElement elAST = jo.get("faulty_stmts_ast");
			JsonElement elAST = jo.get("pattern_instances");

			assertNotNull(elAST);
			assertTrue(elAST instanceof JsonArray);
			JsonArray ar = (JsonArray) elAST;
			assertTrue(ar.size() == 2);

		}

	}

	@Test
	public void testD4JVMath86() throws Exception {
		String diffId = "Math_86";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsIf");
		System.out.println(insts);
		assertTrue(insts.size() > 0);
	}

	@Test
	public void testD4JVMath98() {
		String diffId = "Math_98";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongVarRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// JSonTest.showAST(resultjson, "unwrapMethod",
		// "com.google.javascript.jscomp.ProcessCommonJSModules#normalizeSourceName(java.lang.String)",
		// "Invocation");

		PatternInstance pi1 = insts.get(0);
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("v.length")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("final double[] out = new double[v.length]"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongVarRef", "length", "FieldRead");
	}

	@Test
	public void testD4JVTime20() {
		String diffId = "Time_20";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("unwrapIfElse");
		System.out.println(insts);
		assertTrue(insts.size() > 0);
	}

	@Test
	public void testD4JVtime8() throws Exception {
		String diffId = "Time_8";

		String input = getCompletePathD4J(diffId);

		List<RepairPatterns> patterns = analyze(input);
		assertTrue(patterns.size() > 0);

		RepairPatterns repairPatterns = patterns.get(0);

		Assert.assertTrue(repairPatterns.getFeatureCounter("wrapsMethod") > 0);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrapsMethod");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().contains("Math.abs(minutesOffset)"));
		assertEquals(1, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("inutesOffset")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertEquals("minutesOffset = hoursInMinutes - minutesOffset", pi1.getFaultyLine().toString());

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);
		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);
	}

	@Test
	public void testICSE_1064371_METHOD() {
		String diffId = "1064371";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testICSE_1086957_METHOD() {
		// False positive
		String diffId = "1086957";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testICSE1002329_METHOD() {
		String diffId = "1002329";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testICSE591061() {
		String diffId = "591061";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testICSE888066_METHOD() {
		String diffId = "888066";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);

	}

	@Test
	public void testTest1205753() {
		String diffId = "1205753";

		String input = getCompletePath("icse2015", diffId);
		// "/Users/matias/develop/code/git-gt-spoon-diff/coming/src/main/resources/testInsert2/"
		// + diffId;

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "addassignment", "if", "If");

	}

	@Test
	public void testTest1205753_insert_end() {
		String diffId = "1205753";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		System.out.println("Patterns: ");
		for (RepairPatterns repairPatterns : patterns) {
			System.out.println("-->" + repairPatterns);
		}
		System.out.println("-----");

		System.out.println("JSon");
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println(resultjson);
		showJSONFaultyAST(resultjson);
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "addassignment", "if", "If");

	}

	/**
	 * For 1079460, the repair action is identified as "susp_wrongMethodRef" and
	 * "susp_wrapsMethod", but in fact it probably should be identified as "variable
	 * replacement by method call" repair action. I think, the patch like 1079460 is
	 * common in practice, so it may represent a common and probably (important)
	 * drawback in the tool.
	 */
	@Test
	public void testICSE15_1079460() {
		String diffId = "1079460";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongMethodRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// newCC.setCurrentDependent(triggerActionSPSD.getPreparedStatement());

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.equals("newCC.setCurrentDependent(triggerActionSPSD.getPreparedStatement())"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("newCC.setCurrentDependent(td)"))
				.findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("newCC.setCurrentDependent(td)"));

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());
		SuspiciousASTFaultyTest.assertMarkedlAST(resultjson, "wrongVarRef", "NaN", "FieldRead");
//
//		JsonArray affected = (JsonArray) resultjson.get("affected_files");
//		for (JsonElement jsonElement : affected) {
//
//			JsonObject jo = (JsonObject) jsonElement;
//			// JsonElement elAST = jo.get("faulty_stmts_ast");
//			JsonElement elAST = jo.get("pattern_instances");
//
//			assertNotNull(elAST);
//			assertTrue(elAST instanceof JsonArray);
//			JsonArray ar = (JsonArray) elAST;
//			assertTrue(ar.size() == 1);
//
//		}

	}

	// 494136

	@Test
	public void testICSE15_494136_Labeling() {
		String diffId = "494136";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("expLogicExpand");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		// newCC.setCurrentDependent(triggerActionSPSD.getPreparedStatement());

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString().equals("(lastDoc != 0) && (doc <= lastDoc)"));
		// assertEquals(2, pi1.getFaulty().size());
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("doc")).findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("doc < lastDoc"));

		List<JsonElement> elements = getMarkedlAST(resultjson, null, "expLogicExpand");
		assertEquals(2, elements.size());

		List<PatternInstance> instsCOnst = repairPatterns.getPatternInstances().get("constChange");
		System.out.println(instsCOnst);
		assertTrue(instsCOnst.size() > 0);

		pi1 = instsCOnst.get(0);
		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("<")).findFirst().isPresent());
		assertTrue(pi1.getFaultyLine().toString().equals(
				"new java.lang.IllegalStateException(((((\"docs out of order (\" + doc) + \" < \") + lastDoc) + \" )\"))"));
		assertNotNull(pi1.getFaultyTree());

		elements = getMarkedlAST(resultjson, null, "constChange");
		assertEquals(1, elements.size());

	}

	@Test
	public void testICSE15_1078693_labeling() {
		String diffId = "1078693";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		List<PatternInstance> insts = repairPatterns.getPatternInstances().get("wrongMethodRef");
		System.out.println(insts);
		assertTrue(insts.size() > 0);

		PatternInstance pi1 = insts.get(0);
		assertTrue(pi1.getNodeAffectedOp().toString()
				.equals("newCC.setCurrentDependent(triggerActionSPSD.getPreparedStatement())"));

		assertTrue(pi1.getFaulty().stream().filter(e -> e.toString().contains("newCC.setCurrentDependent(td)"))
				.findFirst().isPresent());

		assertNotNull(pi1.getFaultyTree());
		assertTrue(pi1.getFaultyLine().toString().equals("newCC.setCurrentDependent(td)"));

		List elements = getMarkedlAST(resultjson, null, "wrongMethodRef");
		assertEquals(1, elements.size());

	}

	@Test
	public void testICSE15_1051440_WrongDiff() {
		String diffId = "1051440";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

	}

	@Test
	public void testICSE15_979518_Wrong_labeledNode_wrongMethodRef() {
		String diffId = "979518";

		String input = getCompletePath("icse2015", diffId);
		// We force to analyze the test, there we have the pattern instance
		diffson.ConfigurationProperties.properties.setProperty("excludetests", "false");
		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);

		List<JsonElement> suspiciousElements = getMarkedlAST(resultjson, null, "wrongMethodRef");
		assertEquals(1, suspiciousElements.size());
		System.out.println("-->" + suspiciousElements.get(0));
		JsonObject obj = (JsonObject) suspiciousElements.get(0);
		assertEquals("ConstructorCall", obj.get("type").getAsString());
	}

	@Test
	public void testICSE15_1089542() {
		String diffId = "1089542";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);

	}

	// 1102345
	// Wrong method ref points to childer:

	@Test
	public void testICSE15_1102345() {
		String diffId = "1102345";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);

	}

	// 381553

	@Test
	public void testICSE15_381553() {
		String diffId = "381553";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);

	}

	// 381553
	// FPositive
	@Test
	public void testICSE15_1476326() {
		String diffId = "1476326";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);

	}

	@Test
	public void testICSE15_289672() {
		String diffId = "289672";

		String input = getCompletePath("icse2015", diffId);

		List<RepairPatterns> patterns = analyze(input);

		RepairPatterns repairPatterns = patterns.get(0);
		System.out.println(repairPatterns);

		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, input);

		System.out.println("END 1\n" + resultjson.toString());

		SuspiciousASTFaultyTest.assertSuspiciousASTNode(resultjson);

	}
	// 289672
}
