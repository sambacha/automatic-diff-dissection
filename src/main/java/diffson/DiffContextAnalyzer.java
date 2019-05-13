package diffson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.junit.Ignore;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import add.entities.PatternInstance;
import add.entities.RepairActions;
import add.entities.RepairPatterns;
import add.features.detector.EditScriptBasedDetector;
import add.features.detector.repairactions.RepairActionDetector;
import add.features.detector.repairpatterns.MappingAnalysis;
import add.features.detector.repairpatterns.RepairPatternDetector;
import add.main.Config;
import add.main.TimeChrono;
import fr.inria.coming.codefeatures.Cntx;
import fr.inria.coming.codefeatures.CodeFeatureDetector;
import fr.inria.coming.codefeatures.CodeFeatures;
import fr.inria.coming.utils.MapList;
import gumtree.spoon.AstComparator;
import gumtree.spoon.builder.Json4SpoonGenerator;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.builder.jsonsupport.NodePainter;
import gumtree.spoon.builder.jsonsupport.OperationNodePainter;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.DiffImpl;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.MoveOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

/**
 * 
 * @author Matias Martinez
 *
 */
public class DiffContextAnalyzer {
	File out = null;

	public DiffContextAnalyzer() {
		super();
		PDDConfigurationProperties.properties.setProperty("maxdifftoanalyze", "5");

		out = new File("/tmp/");
		out.mkdirs();
	}

	public DiffContextAnalyzer(String outfile) {
		super();

		out = new File(outfile);
		out.mkdirs();
	}

	private Logger log = Logger.getLogger(this.getClass());
	int error = 0;
	int zero = 0;
	int withactions = 0;

	@SuppressWarnings("unchecked")
	public void run(String path) throws Exception {

		error = 0;
		zero = 0;
		withactions = 0;

		File dir = new File(path);

		beforeStart();

		int diffanalyzed = 0;
		for (File difffile : dir.listFiles()) {

			TimeChrono cr = new TimeChrono();
			cr.start();
			Map<String, Diff> diffOfcommit = new HashMap();

			if (difffile.isFile() || difffile.listFiles() == null)
				continue;

			diffanalyzed++;

			log.debug("-commit->" + difffile);
			System.out.println("\n****" + diffanalyzed + "/" + dir.listFiles().length + ": " + difffile.getName());

			if (!acceptFile(difffile)) {
				System.out.println("existing json for: " + difffile.getName());
				continue;
			}

			processDiff(difffile, diffOfcommit);
			double timediff = cr.getSeconds();
			log.info("Total diff of " + difffile.getName() + ": " + timediff);

			// here, at the end, we compute the Context
			atEndCommit(difffile, diffOfcommit);

			double timeProg = cr.getSeconds();
			log.info("Total property of " + difffile.getName() + ": " + (timeProg - timediff));

			// if (diffanalyzed ==
			// ConfigurationProperties.getPropertyInteger("maxdifftoanalyze")) {
			// System.out.println("max-break");
			// break;
			// }
			log.info("Total time of " + difffile.getName() + ": " + cr.stopAndGetSeconds());
		}
		log.info("Final Results: ");
		log.info("----");
		log.info("Withactions " + withactions);
		log.info("Zero " + zero);
		log.info("Error " + error);

		beforeEnd();
	}

	@SuppressWarnings("unchecked")
	public void processDiff(File difffile, Map<String, Diff> diffOfcommit) {
		for (File fileModif : difffile.listFiles()) {
			int i_hunk = 0;

			if (".DS_Store".equals(fileModif.getName()))
				continue;

			if (PDDConfigurationProperties.getPropertyBoolean("excludetests")
					&& (fileModif.getName().toLowerCase().indexOf("test")!=-1))
				continue;
			// Commented for the ye's dataset 3Fix
			// String pathname = fileModif.getAbsolutePath() + File.separator +
			//// difffile.getName() + "_" +
			// fileModif.getName();

			String pathname = fileModif.getAbsolutePath() + File.separator + difffile.getName() + "_"
					+ fileModif.getName();

			File previousVersion = new File(pathname + "_s.java");
			if (!previousVersion.exists()) {
				pathname = pathname + "_" + i_hunk;
				previousVersion = new File(pathname + "_s.java");
				if (!previousVersion.exists())
					// break;
					continue;
			}

			File postVersion = new File(pathname + "_t.java");
			i_hunk++;
			try {
				Diff diff = getdiffFuture(previousVersion, postVersion);

				String key = fileModif.getParentFile().getName() + "_" + fileModif.getName();
				diffOfcommit.put(key, diff);

				if (diff.getAllOperations().size() > 0) {

					withactions++;
					log.debug("-file->" + fileModif + " actions " + diff.getRootOperations().size());
					for (Operation operation : diff.getRootOperations()) {

						log.debug("-op->\n" + operation);

						// JsonObject op = getJSONFromOperator(operation);

						// operationsArray.add(op);
					}

				} else {
					zero++;
					log.debug("-file->" + fileModif + " zero actions ");
				}

			} catch (Throwable e) {
				log.error("error with " + previousVersion);
				e.printStackTrace();
				error++;
			}

		}
		// return filesArray;
	}

	@SuppressWarnings("unchecked")
	protected JsonObject getJSONFromOperator(Operation operation) {
		JsonObject op = new JsonObject();
		op.addProperty("operator", operation.getAction().getName());
		op.addProperty("src",
				(operation.getSrcNode() != null) ? operation.getSrcNode().getClass().getSimpleName() : "null");
		op.addProperty("dst",
				(operation.getDstNode() != null) ? operation.getDstNode().getParent().getClass().getSimpleName()
						: "null");

		op.addProperty("srcparent",
				(operation.getSrcNode() != null) ? operation.getSrcNode().getClass().getSimpleName() : "null");
		op.addProperty("dstparent",
				(operation.getDstNode() != null) ? operation.getDstNode().getParent().getClass().getSimpleName()
						: "null");
		return op;
	}

	public void beforeEnd() {
		// Do nothing
	}

	public void beforeStart() {
		// Do nothing
	}

	public Diff getdiff(File left, File right) throws Exception {

		AstComparator comparator = new AstComparator();
		return comparator.compare(left, right);

	}

	private void addStats(JsonObject root, String key1, Map sorted) {
		JsonArray frequencyArray = new JsonArray();
		root.add(key1, frequencyArray);
		for (Object key : sorted.keySet()) {
			Object v = sorted.get(key);
			JsonObject singlediff = new JsonObject();
			singlediff.addProperty("c", key.toString());
			singlediff.addProperty("f", v.toString());
			frequencyArray.add(singlediff);
		}
	}

	// Buggy Array exception
	@Ignore
	public String read(File file) {
		String s = "";
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				s += (line);

			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("One arg: folder path");
		}
		String path = args[0];
		DiffContextAnalyzer runner = new DiffContextAnalyzer();
		runner.run(path);
	}

	private Future<Diff> getfutureResult(ExecutorService executorService, File left, File right) {

		Future<Diff> future = executorService.submit(() -> {

			AstComparator comparator = new AstComparator();
			return comparator.compare(left, right);

		});
		return future;
	}

	public Diff getdiffFuture(File left, File right) throws Exception {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<Diff> future = getfutureResult(executorService, left, right);

		Diff resukltDiff = null;
		try {
			resukltDiff = future.get(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("job was interrupted");
		} catch (ExecutionException e) {
			log.error("caught exception: " + e.getCause());
		} catch (TimeoutException e) {
			log.error("timeout");
		}

		executorService.shutdown();
		return resukltDiff;

	}

	protected boolean acceptFile(File fileModif) {
		File f = new File(out.getAbsolutePath() + File.separator + fileModif.getName() + ".json");
		// If the json file does not exist, we process it
		return !f.exists();
	}

	@SuppressWarnings("unchecked")
	public JsonObject atEndCommit(File difffile, Map<String, Diff> diffOfcommit) {
		try {

			JsonObject statsjsonRoot = getContextFuture(difffile.getName(), diffOfcommit);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			boolean savePerFile = false;// testing
			if (savePerFile) {

				JsonArray filesa = statsjsonRoot.getAsJsonArray("affected_files");
				for (JsonElement jsonElement : filesa) {

					String name = jsonElement.getAsJsonObject().getAsJsonPrimitive("file").getAsString();

					FileWriter fw = new FileWriter(out.getAbsolutePath() + File.separator + name + ".json");

					String prettyJsonString = gson.toJson(jsonElement);
					fw.write(prettyJsonString);

					fw.flush();
					fw.close();

				}

			} else {
				FileWriter fw = new FileWriter(out.getAbsolutePath() + File.separator + difffile.getName() + ".json");

				// Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String prettyJsonString = gson.toJson(statsjsonRoot);
				fw.write(prettyJsonString);
				// System.out.println(prettyJsonString);
				fw.flush();
				fw.close();
			}
			return statsjsonRoot;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/// -=-=--=-=-=-=--=

	private Future<JsonObject> getContextInFeature(ExecutorService executorService, String id,
			Map<String, Diff> diffOfcommit) {

		Future<JsonObject> future = executorService.submit(() -> {
			JsonObject statsjsonRoot = calculateCntxJSON(id, diffOfcommit);
			return statsjsonRoot;
		});
		return future;
	}

	public JsonObject getContextFuture(String id, Map<String, Diff> operations) throws Exception {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<JsonObject> future = getContextInFeature(executorService, id, operations);

		JsonObject resukltDiff = null;
		try {
			resukltDiff = future.get(4, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("job was interrupted");
		} catch (ExecutionException e) {
			log.error("caught exception: " + e.getCause());
		} catch (TimeoutException e) {
			log.error("timeout context analyzed.");
		}

		executorService.shutdown();
		return resukltDiff;

	}

	/////// ---------=-=-=-=--=-=-=-

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JsonObject calculateCntxJSON(String id, Map<String, Diff> operations) {

		JsonObject statsjsonRoot = new JsonObject();
		statsjsonRoot.addProperty("diffid", id);
		JsonArray filesArray = new JsonArray();
		statsjsonRoot.add("affected_files", filesArray);

		for (String modifiedFile : operations.keySet()) {
			MapList<Operation, String> patternsPerOp = new MapList<>();
			MapList<Operation, String> repairactionPerOp = new MapList<>();
			List<PatternInstance> patternInstances = new ArrayList<>();

			Diff diff = operations.get(modifiedFile);
			List<Operation> operationsFromFile = diff.getRootOperations();

			log.info("Diff file " + modifiedFile + " " + operationsFromFile.size());

			// Patterns:

			if(diff.getRootOperations().size()<=10) {
			   JsonObject fileModified = new JsonObject();

			   fileModified.addProperty("file", modifiedFile);
			   fileModified.addProperty("nr_root_ast_changes", diff.getRootOperations().size());
			   filesArray.add(fileModified);

			   Config config = new Config();
			   EditScriptBasedDetector.preprocessEditScript(diff);
			   TimeChrono cr = new TimeChrono();
			   cr.start();
			   RepairPatternDetector detector = new RepairPatternDetector(config, diff);
			   RepairPatterns rp = detector.analyze();

			   log.info("---Total pattern of " + ": " + cr.stopAndGetSeconds());

			   for (List<PatternInstance> pi : rp.getPatternInstances().values()) {
				  patternInstances.addAll(pi);
			   }
			   cr.start();

			   JsonArray ast_arrays = calculateJSONAffectedStatementList(diff, operationsFromFile, patternsPerOp,
					repairactionPerOp, patternInstances);
			// fileModified.add("faulty_stmts_ast", ast_arrays);
			   fileModified.add("pattern_instances", ast_arrays);
			   log.info("---Total feature of " + ": " + cr.stopAndGetSeconds());

			   includeAstChangeInfoInJSon(diff, operationsFromFile, fileModified);
		   }
		}

		return statsjsonRoot;

	}

	public void includeAstChangeInfoInJSon(Diff diff, List<Operation> operationsFromFile, JsonObject fileModified) {
		JsonArray ast_changes_arrays = new JsonArray();
		// Here include optionality

		for (Operation op : operationsFromFile) {

			JsonObject astNode = new JsonObject();
			astNode.addProperty("change_type", op.getClass().getSimpleName());
			astNode.addProperty("entity_type",
					op.getSrcNode().getClass().getSimpleName().replaceAll("Ct", "").replaceAll("Impl", ""));

			// TODO: now we don't want to compute context of AST
			// JsonObject opContext = getContextInformation(diff, cresolver, op,
			// op.getSrcNode());
			// astNode.add("info", opContext);

			ast_changes_arrays.add(astNode);
		}
		fileModified.add("ast_changes", ast_changes_arrays);
	}

	public void repairactions(MapList<Operation, String> patternsPerOp, MapList<Operation, String> repairactionPerOp,
			Diff diff, JsonObject fileModified, Config config, RepairPatterns rp) {
		JsonObject patterns = new JsonObject();
		for (String featureName : rp.getFeatureNames()) {
			int counter = rp.getFeatureCounter(featureName);
			patterns.addProperty(featureName, counter);

			List<Operation> opsfeature = rp.getOperationsPerFeature().get(featureName);
			if (opsfeature == null || opsfeature.isEmpty())
				continue;

			for (Operation operation : opsfeature) {

				patternsPerOp.add(operation, featureName);

			}
		}

		fileModified.add("repairpatterns", patterns);

		/// Repair actions
		JsonObject repairactions = new JsonObject();
		try {
			RepairActionDetector pa = new RepairActionDetector(config, diff);
			RepairActions as = pa.analyze();

			for (String featureName : as.getFeatureNames()) {
				int counter = as.getFeatureCounter(featureName);
				repairactions.addProperty(featureName, counter);

				List<CtElement> el = as.getElementPerFeature().get(featureName);
				if (el != null && el.size() > 0) {
					for (Operation opi : diff.getAllOperations()) {
						if (el.contains(opi.getNode())) {
							repairactionPerOp.add(opi, featureName);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error computing ");
			e.printStackTrace();
		}

		fileModified.add("repairactions", repairactions);
		// repairActionslistJSon.add(repairActionFile);
		// End repair actions
	}

	/**
	 * CodeFeatureDetector cresolver = new CodeFeatureDetector();
	 * 
	 * JsonObject opContext = new JsonObject();
	 * 
	 * opContext.addProperty("bug", modifiedFile);
	 * 
	 * opContext.addProperty("key", modifiedFile); Cntx iContext =
	 * cresolver.retrieveCntx(operation.getSrcNode());
	 * iContext.setIdentifier(modifiedFile); opContext.add("cntx",
	 * iContext.toJSON());
	 * 
	 * setBuggyInformation(operation, cresolver, opContext, diff);
	 * 
	 * setPatchInformation(operation, cresolver, opContext, diff);
	 * calculateJSONAffectedMethod(diff, operation, opContext);
	 * calculateJSONAffectedElement(diff, operation, opContext);
	 * opsFeature.add(opContext);
	 */

	/**
	 * // let's find the destination in the Source Tree Move ma = (Move)
	 * operation.getAction(); ITree newParentDst = ma.getParent(); ITree
	 * mappedParentSrc = null; do { // ITree parentTree =
	 * operation.getAction().getNode().getParent(); mappedParentSrc =
	 * mappings.getSrc(newParentDst); newParentDst = newParentDst.getParent(); }
	 * while (mappedParentSrc == null && newParentDst != null);
	 * 
	 * @param operation
	 * @param cresolver
	 * @param opContext
	 * @param diff
	 */

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void seInformation(Operation operation, CodeFeatureDetector cresolver, JsonObject opContext, Diff diff, CtElement affectedelement) {

		Cntx bugContext = new Cntx<>();

		CtElement affectedCtElement = null;

		if (operation instanceof MoveOperation) {

			MoveOperation movop = (MoveOperation) operation;
			// Element to move in source
			CtElement affectedMoved = operation.getSrcNode();
			MappingStore mappings = diff.getMappingsComp();

			affectedCtElement = affectedMoved;

			bugContext.put(CodeFeatures.OPERATION, "MV");

			bugContext.put(CodeFeatures.AFFECTED, cresolver.retrieveInfoOfElement(affectedMoved));

			ITree affected = MappingAnalysis.getParentInSource(diff, movop.getAction());

			ITree targetTreeParentNode = getParent(affected);

			if (targetTreeParentNode != null) {
				CtElement oldParentLocationInsertStmt = (CtElement) targetTreeParentNode.getMetadata("spoon_object");

				bugContext.put(CodeFeatures.AFFECTED_PARENT,
						cresolver.retrieveInfoOfElement(oldParentLocationInsertStmt));
			}

		} else if (operation instanceof InsertOperation)

		{

			CtElement oldLocation = ((InsertOperation) operation).getParent();
			CtElement oldParentLocationInsertStmt = getStmtParent(oldLocation);

			affectedCtElement = oldLocation;
			bugContext.put(CodeFeatures.AFFECTED, null);
			bugContext.put(CodeFeatures.AFFECTED_PARENT, cresolver.retrieveInfoOfElement(oldParentLocationInsertStmt));
			bugContext.put(CodeFeatures.OPERATION, "INS");

		} else if (operation instanceof DeleteOperation) {

			DeleteOperation up = (DeleteOperation) operation;
			CtElement oldLocation = operation.getSrcNode();
			CtElement oldParentLocationInsertStmt = getStmtParent(oldLocation);
			bugContext.put(CodeFeatures.AFFECTED, cresolver.retrieveInfoOfElement(oldLocation));
			bugContext.put(CodeFeatures.AFFECTED_PARENT, cresolver.retrieveInfoOfElement(oldParentLocationInsertStmt));
			bugContext.put(CodeFeatures.OPERATION, "DEL");

			affectedCtElement = oldLocation;

		} else if (operation instanceof UpdateOperation) {

			UpdateOperation up = (UpdateOperation) operation;
			CtElement oldLocation = operation.getSrcNode();
			CtElement oldParentLocationInsertStmt = getStmtParent(oldLocation);
			bugContext.put(CodeFeatures.AFFECTED, cresolver.retrieveInfoOfElement(oldLocation));
			bugContext.put(CodeFeatures.AFFECTED_PARENT, cresolver.retrieveInfoOfElement(oldParentLocationInsertStmt));
			bugContext.put(CodeFeatures.OPERATION, "UPD");

			affectedCtElement = oldLocation;
		}

		//
		if (affectedCtElement != null) {
			Cntx iContext = cresolver.analyzeFeatures(affectedelement);
			opContext.add("cntx", iContext.toJSON());
		}

		//
//		if (bugContext != null)
//			opContext.add("bug", bugContext.toJSON());
//		else
//			System.out.println("Operation not known: " + operation.getClass().getSimpleName());

	}
//////
	// ---
	// ---
	////////

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void setPatchInformation(Operation operation, CodeFeatureDetector cresolver, JsonObject opContext,
			Diff diff) {

		Cntx bugContext = new Cntx<>();
		MappingStore mappings = diff.getMappingsComp();

		CtElement nodeToCalculateContext = null;

		if (operation instanceof MoveOperation) {

			// Element to move in source
			CtElement affectedMoved = operation.getSrcNode();
			bugContext.put(CodeFeatures.OPERATION, "MV");
			// Find the parent

			// let's find the destination in the Source Tree
			Move ma = (Move) operation.getAction();
			// This parent is from the dst
			ITree newParentSRC = ma.getParent();

			bugContext.put(CodeFeatures.AFFECTED, cresolver.retrieveInfoOfElement(affectedMoved));

			ITree parentInRight = MappingAnalysis.getParentInRight(diff, ma);

			CtElement parentMovedElementInDst = getStmtParent((CtElement) parentInRight.getMetadata("spoon_object"));
			nodeToCalculateContext = parentMovedElementInDst;
			bugContext.put(CodeFeatures.AFFECTED_PARENT, cresolver.retrieveInfoOfElement(parentMovedElementInDst));

		} else if (operation instanceof InsertOperation)

		{
			InsertOperation op = (InsertOperation) operation;
			CtElement affectedElement = op.getSrcNode();
			CtElement newParentLocationInsertStmt = getStmtParent(affectedElement);

			bugContext.put(CodeFeatures.AFFECTED, cresolver.retrieveInfoOfElement(affectedElement));
			bugContext.put(CodeFeatures.AFFECTED_PARENT, cresolver.retrieveInfoOfElement(newParentLocationInsertStmt));
			bugContext.put(CodeFeatures.OPERATION, "INS");
			nodeToCalculateContext = affectedElement;
		} else if (operation instanceof DeleteOperation) {

			DeleteOperation up = (DeleteOperation) operation;

			ITree newParentDst = up.getAction().getNode().getParent();
			ITree mappedParentDst = null;
			do {
				mappedParentDst = mappings.getDst(newParentDst);
				newParentDst = newParentDst.getParent();
			} while (mappedParentDst == null && newParentDst != null);

			if (mappedParentDst != null) {

				CtElement parentDstInDst = (CtElement) mappedParentDst.getMetadata("spoon_object");

				CtElement oldParentLocationInsertStmt = getStmtParent(parentDstInDst);
				nodeToCalculateContext = oldParentLocationInsertStmt;
				bugContext.put(CodeFeatures.AFFECTED, null);
				bugContext.put(CodeFeatures.AFFECTED_PARENT,
						cresolver.retrieveInfoOfElement(oldParentLocationInsertStmt));
				bugContext.put(CodeFeatures.OPERATION, "DEL");
			}

		} else if (operation instanceof UpdateOperation) {

			UpdateOperation up = (UpdateOperation) operation;
			CtElement oldLocation = operation.getDstNode();
			CtElement oldParentLocationInsertStmt = getStmtParent(oldLocation);

			bugContext.put(CodeFeatures.OPERATION, "UPD");
			bugContext.put(CodeFeatures.AFFECTED, cresolver.retrieveInfoOfElement(oldLocation));
			bugContext.put(CodeFeatures.AFFECTED_PARENT, cresolver.retrieveInfoOfElement(oldParentLocationInsertStmt));
			// Is it located in parent?
			nodeToCalculateContext = oldLocation;
		}

		if (nodeToCalculateContext != null) {

		}

		if (bugContext != null)
			opContext.add("patch", bugContext.toJSON());
		else
			System.out.println("Operation not known: " + operation.getClass().getSimpleName());

	}

	private CtElement getStmtParent(CtElement element) {
		if (element instanceof CtField)
			return element;

		CtElement parent = element.getParent(CtStatement.class);
		if (parent == null)
			parent = element.getParent(CtMethod.class);
		else {
			// Workarround case of X = new X();
			if (parent.getParent() instanceof CtStatement && !(parent.getParent() instanceof CtBlock))
				return getStmtParent(element.getParent());// parent.getParent();
			else {
				return parent;
			}
		}

		return element.getParent();
	}

	private void calculateJSONAffectedMethod(Diff diff, Operation operation, JsonObject opContext) {

		CtMethod methodOfOperation = operation.getNode().getParent(CtMethod.class);
		Json4SpoonGenerator jsongen = new Json4SpoonGenerator();

		Action affectedAction = operation.getAction();
		ITree affected = affectedAction.getNode();
		// jsongen.getJSONasJsonObject(

		ITree methodTreeNode = null;
		do {
			CtElement relatedCtElement = (CtElement) affected.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
			if (relatedCtElement instanceof CtExecutable) { // check if its th buggy method
				// if (methodOfOperation == relatedCtElement) {// same object
				methodTreeNode = affected;
			}
			affected = affected.getParent();
		} while (methodTreeNode == null && affected.getParent() != null);
		//
		if (methodTreeNode != null) {
			JsonObject jsonT = jsongen.getJSONwithOperations(((DiffImpl) diff).getContext(), methodTreeNode,
					diff.getAllOperations());

			opContext.add(CodeFeatures.AST_PARENT.toString(), jsonT);

		}

	}

	CodeFeatureDetector cresolver = new CodeFeatureDetector();

	/**
	 * Only AST for pattern
	 * 
	 * @param diff
	 * @param operations
	 * @param patternsPerOp
	 * @param repairactionPerOp
	 * @param patternInstances
	 * @return
	 */
	public JsonArray calculateJSONAffectedStatementList(Diff diff, List<Operation> operations,
			MapList<Operation, String> patternsPerOp, MapList<Operation, String> repairactionPerOp,
			List<PatternInstance> patternInstancesOriginal) {

		Json4SpoonGenerator jsongen = new Json4SpoonGenerator();

		JsonArray ast_affected = new JsonArray();

		List<PatternInstance> patternInstancesMerged = merge(patternInstancesOriginal);

		for (PatternInstance patternInstance : patternInstancesMerged) {
			Set<ITree> allTreeparents = new HashSet<>();
			Operation opi = patternInstance.getOp();

			List<CtElement> faulties = null;

			CtElement getAffectedCtElement = patternInstance.getFaultyLine();
			ITree faultyTree = patternInstance.getFaultyTree();
			if (faultyTree != null) {

				// Transform the Tree element in case it's a control flow

				faultyTree = MappingAnalysis.getFormatedTreeFromControlFlow(faultyTree,
						(CtElement) faultyTree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));

				allTreeparents.add(faultyTree);
			} else {

				if (getAffectedCtElement != null) {
					faulties = new ArrayList<>();
					faulties.add(getAffectedCtElement);
				} else {
					if (patternInstance.getFaulty() != null)
						faulties = patternInstance.getFaulty();
					else {

					}
				}

				for (CtElement faulty : faulties) {
					ITree nodeFaulty = (ITree) faulty.getMetadata("gtnode");

					if (nodeFaulty != null) {

						ITree transformedTree = MappingAnalysis.getFormatedTreeFromControlFlow(nodeFaulty,
								(CtElement) faulty.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT));

						allTreeparents.add(transformedTree);
					} else {
						System.out.println("Error nodefaulty null");
					}
				}
			}

			List<NodePainter> painters = new ArrayList();
			painters.add(new PatternPainter(patternsPerOp, "patterns"));
			painters.add(new PatternPainter(repairactionPerOp, "repairactions"));
		//	painters.add(new OperationNodePainter(diff.getAllOperations()));
			painters.add(new FaultyElementPatternPainter(patternInstancesOriginal));
			painters.add(new ReturnTypePainter(getAffectedCtElement));

			// System.out.println(patternInstance);
			JsonObject jsonInstance = new JsonObject();
			JsonArray affected = new JsonArray();
			for (ITree iTree : allTreeparents) {
				JsonObject jsonT = jsongen.getJSONwithCustorLabels(((DiffImpl) diff).getContext(), iTree, painters);
				affected.add(jsonT);
			}
			
			   jsonInstance.add("faulty_ast", affected);

			// Removed in this version
			// jsonInstance.addProperty("pattern_name", patternInstance.getPatternName());
			   ast_affected.add(jsonInstance);

			   JsonObject opContext = getContextInformation(diff, cresolver, opi, getAffectedCtElement);

			   jsonInstance.add("context", opContext);
		}

		return ast_affected;
	}

	public JsonObject getContextInformation(Diff diff, CodeFeatureDetector cresolver, Operation opi,
			CtElement getAffectedCtElement) {

		JsonObject opContext = new JsonObject();

		// Cntx iContext = cresolver.retrieveCntx(getAffectedCtElement);
		// opContext.add("cntx", iContext.toJSON());

		seInformation(opi, cresolver, opContext, diff, getAffectedCtElement);

	//	setPatchInformation(opi, cresolver, opContext, diff);
		return opContext;
	}

	private List<PatternInstance> merge(List<PatternInstance> patternInstancesOriginal) {
		List<PatternInstance> patternInstancesMerged = new ArrayList<>();
		Map<CtElement, PatternInstance> cacheFaultyLines = new HashMap<>();

		for (PatternInstance patternInstance : patternInstancesOriginal) {
			if (!cacheFaultyLines.containsKey(patternInstance.getFaultyLine())) {
				cacheFaultyLines.put(patternInstance.getFaultyLine(), patternInstance);
				patternInstancesMerged.add(patternInstance);
			}
		}
		return patternInstancesMerged;
	}

	public JsonObject calculateJSONAffectedStatement(Diff diff, Operation operation,
			MapList<Operation, String> patternsPerOp, MapList<Operation, String> repairactionPerOp) {

		Json4SpoonGenerator jsongen = new Json4SpoonGenerator();

		List<NodePainter> painters = new ArrayList();
		painters.add(new PatternPainter(patternsPerOp, "patterns"));
		painters.add(new PatternPainter(repairactionPerOp, "repairactions"));
		painters.add(new OperationNodePainter(diff.getAllOperations()));

		ITree targetTreeNode = null;
		Action affectedAction = operation.getAction();
		ITree affected = affectedAction.getNode();

		targetTreeNode = getParent(affected);

		if (targetTreeNode != null) {

			if (operation instanceof InsertOperation) {
				InsertOperation insert = (InsertOperation) operation;
				insert.getAction().getParent().insertChild(insert.getAction().getNode(),
						insert.getAction().getPosition());

			}

			JsonObject jsonT = jsongen.getJSONwithCustorLabels(((DiffImpl) diff).getContext(), targetTreeNode,
					painters);
			return jsonT;
		}
		return null;
	}

	private ITree getParent(ITree affected) {
		ITree parent = getParentStatement(affected);
		if (parent == null) {
			parent = getParentExecutable(affected);
			if (parent == null)
				parent = getParentField(affected);

		}
		return parent;
	}

	private ITree getParentExecutable(ITree affected) {
		ITree targetTreeNode = null;
		CtElement relatedCtElement = null;
		do {
			relatedCtElement = (CtElement) affected.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

			if (relatedCtElement instanceof CtExecutable) {
				targetTreeNode = affected;
			}
			affected = affected.getParent();
		} while ((targetTreeNode == null && affected.getParent() != null));

		return targetTreeNode;
	}

	private ITree getParentField(ITree affected) {
		ITree targetTreeNode = null;
		CtElement relatedCtElement = null;
		do {
			relatedCtElement = (CtElement) affected.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

			if (relatedCtElement instanceof CtField) {
				targetTreeNode = affected;

			}
			affected = affected.getParent();
		} while ((targetTreeNode == null && affected.getParent() != null));

		return targetTreeNode;
	}

	private ITree getParentStatement(ITree affected) {
		ITree targetTreeNode = null;
		CtElement targetCtElement = null;
		CtElement relatedCtElement = null;
		do {
			relatedCtElement = (CtElement) affected.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

			if (relatedCtElement instanceof CtStatement && !(relatedCtElement instanceof CtClass)) {
				targetTreeNode = affected;
				targetCtElement = relatedCtElement;
			}
			affected = affected.getParent();
		} while ((targetTreeNode == null && affected.getParent() != null) || (relatedCtElement != null
				&& relatedCtElement.getParent() instanceof CtStatement && !(relatedCtElement instanceof CtBlock)));

		// System.out.println("target statement: " + targetCtElement);
		return targetTreeNode;
	}

	static List emptyList = new ArrayList();

	private void calculateJSONAffectedElement(Diff diff, Operation operation, JsonObject opContext) {

		operation.getNode();
		Json4SpoonGenerator jsongen = new Json4SpoonGenerator();

		JsonObject jsonT = jsongen.getJSONwithOperations(((DiffImpl) diff).getContext(),
				operation.getAction().getNode(), emptyList);
		opContext.add(CodeFeatures.AST.toString(), jsonT);

	}

}
