package diffson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fr.inria.astor.core.entities.CNTX_Property;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FeatureJSONTest {

	@Before
	public void setUp() throws Exception {

		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().addAppender(console);

	}

	public File getFile(String name) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(name).getFile());
		return file;
	}

	@Test
	public void testContext_M4_Closure9() {

		String diffId = "Closure_9";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);

		assertMarkedlAST(resultjson, CNTX_Property.M4_PARAMETER_RETURN_COMPABILITY, Boolean.TRUE);
		assertMarkedlAST(resultjson,
				CNTX_Property.M4_PARAMETER_RETURN_COMPABILITY + "_normalizeSourceName(java.lang.String)", Boolean.TRUE);

		assertMarkedlAST(resultjson, CNTX_Property.M3_SIMILAR_METHOD_WITH_PARAMETER_COMP, Boolean.TRUE);

		assertMarkedlAST(resultjson,
				CNTX_Property.M3_SIMILAR_METHOD_WITH_PARAMETER_COMP + "_normalizeSourceName(java.lang.String)",
				Boolean.TRUE);

		assertMarkedlAST(resultjson, CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, Boolean.FALSE);

		// assertMarkedlAST(resultjson,
		// "M2_SIMILAR_METHOD_WITH_SAME_RETURN_guessCJSModuleName(java.lang.String)",
		// s Boolean.TRUE);
	}

	@Test
	public void testContext_M1_Math_58() {

		String diffId = "Math_58";

		JsonObject resultjson = getJsonOfBugId(diffId);

		System.out.println(resultjson);

		assertMarkedlAST(resultjson, CNTX_Property.M1_OVERLOADED_METHOD, Boolean.TRUE);

		assertMarkedlAST(resultjson, CNTX_Property.M1_OVERLOADED_METHOD + "_fit(double[])", Boolean.TRUE);

	}

	@Test
	public void testContefxt_L1_Closure20() {

		String diffId = "Closure_20";
		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION, Boolean.TRUE);
	}

	@Test
	public void testContext_L2_Closure51() {

		String diffId = "Closure_51";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR, Boolean.TRUE);
	}

	@Test
	public void testContext_L3_Chart_9() {

		String diffId = "Chart_9";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED, Boolean.TRUE);
	}

	@Test
	public void testContext_L4_Closure_38() {

		String diffId = "Closure_38";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE4_EXISTS_LOCAL_UNUSED_VARIABLES, Boolean.TRUE);
	}

	@Test
	public void testContext_L5_Closure_38() {

		String diffId = "Closure_38";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE5_BOOLEAN_EXPRESSIONS_IN_FAULTY, Boolean.TRUE);
	}

	@Test
	public void testContext_L6_Closure_31() {

		String diffId = "Closure_31";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE6_HAS_NEGATION, Boolean.TRUE);
	}

	@Test
	public void testContext_L7_Closure_18() {

		String diffId = "Closure_18";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.LE7_SIMPLE_VAR_IN_LOGIC, Boolean.TRUE);
	}

	@Test
	public void testContext_S1_Chart_4() {

		String diffId = "Chart_4";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		// it's used in statement
		// assertMarkedlAST(resultjson, CNTX_Property.S1_LOCAL_VAR_NOT_USED,
		// Boolean.TRUE);
	}

	public static void assertMarkedlAST(JsonObject resultjson, CNTX_Property name, Boolean b) {
		assertMarkedlAST(resultjson, name.name(), b);
	}

	public static void assertMarkedlAST(JsonObject resultjson, String name, Boolean b) {

		System.out.println("**************** finding " + name);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJsonString = gson.toJson(resultjson);

		System.out.println(prettyJsonString);
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
				// System.out.println("--> AST element: \n" + jso.get("pattern_name"));
				// System.out.println("suspicious element:\n" + prettyJsonString);
				JsonObject asJsonObject = jso.get("context").getAsJsonObject().get("cntx").getAsJsonObject();
				JsonElement property = asJsonObject.get(name.toString());
				if (property != null) {
					JsonPrimitive value = property.getAsJsonPrimitive();

					System.out.println(name + " " + value.getAsString());
					found = found || Boolean.parseBoolean(value.getAsString());
				}
			}

		}

		assertEquals(b, found);

		// assertTrue("Node suspicious not found", found);
	}

	@Test
	@Ignore
	public void testContext_v1() {
		// TO refactor
		String diffId = "Math_24";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		assertMarkedlAST(resultjson, CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN, Boolean.TRUE);
	}

	@Test
	@Ignore
	public void testContext_v2_2() {
		// TO refactor
		String diffId = "Math_26";

		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN, Boolean.FALSE);
		assertMarkedlAST(resultjson, CNTX_Property.S3_TYPE_OF_FAULTY_STATEMENT + "_Return", Boolean.TRUE);

	}

	@Test
	@Ignore
	public void testContext_m1_1() {
		// To refactor
		String diffId = null;// "Math_58";
		JsonObject resultjson = getJsonOfBugId(diffId);
		System.out.println(resultjson);
		//
		assertMarkedlAST(resultjson, CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, Boolean.FALSE);
	}

	public JsonObject getJsonOfBugId(String diffId) {
		String input = "Defects4J/" + diffId;
		File file = new File("./datasets/" + input);
		JsonObject resultjson = SuspiciousASTFaultyTest.getContext(diffId, file.getAbsolutePath());
		return resultjson;
	}
}
