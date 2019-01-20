package diffson;

import java.util.List;
import java.util.stream.Collectors;

import com.github.gumtreediff.tree.ITree;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import add.entities.PatternInstance;
import add.entities.PropertyPair;
import fr.inria.astor.util.MapList;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.builder.jsonsupport.NodePainter;
import spoon.reflect.declaration.CtElement;

public class FaultyElementPatternPainter implements NodePainter {

	// MapList<CtElement, String> nodesAffectedByPattern = new MapList<>();
	MapList<String, String> nodesAffectedByPattern = new MapList<>();
	String label = "susp";

	public FaultyElementPatternPainter(List<PatternInstance> instances) {
		// Collect all nodes and get the operator
		Boolean includeMetadata = ConfigurationProperties.getPropertyBoolean("include_pattern_metadata");

		for (PatternInstance patternInstance : instances) {
			for (CtElement susp : patternInstance.getFaulty()) {
				nodesAffectedByPattern.add(getKey(susp), ("susp_" + patternInstance.getPatternName()
				//
						+ ((includeMetadata && !patternInstance.getMetadata().isEmpty()) ? ("_" + patternInstance
								.getMetadata().stream().map(PropertyPair::getValue).collect(Collectors.joining("_")))
								: "")));

			}
		}

	}

	private String getKey(CtElement susp) {
		try {
			return susp.toString() + "_" + susp.getPath();
		} catch (Exception e) {
			System.err.println("Problem Getting the key");
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void paint(ITree tree, JsonObject jsontree) {

		CtElement ctelement = (CtElement) tree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);

		boolean found = paint(jsontree, ctelement);

		if (!found) {
			CtElement ctelementdsr = (CtElement) tree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT_DEST);
			if (ctelementdsr != null)
				paint(jsontree, ctelementdsr);
		}
	}

	private boolean paint(JsonObject jsontree, CtElement ctelement) {
		boolean found = false;
		if (nodesAffectedByPattern.containsKey(getKey(ctelement))
		// workaround: siee if the same object is present
		// && nodesAffectedByPattern.keySet().stream().filter(e -> e ==
		// ctelement).findFirst().isPresent()
		) {

			JsonArray arr = new JsonArray();
			List<String> ps = nodesAffectedByPattern.get(getKey(ctelement));
			for (String p : ps) {
				JsonPrimitive prim = new JsonPrimitive(p);
				arr.add(prim);
			}
			jsontree.add(this.label, arr);
			found = true;
		}
		return found;
	}

}
