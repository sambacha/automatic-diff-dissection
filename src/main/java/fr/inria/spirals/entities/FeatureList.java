package fr.inria.spirals.entities;

import fr.inria.spirals.main.Constants;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fermadeiral
 */
public class FeatureList {

    private List<Feature> featureList;

    public FeatureList() {
        this.featureList = new ArrayList<>();
    }

    public void add(Feature feature) {
        this.featureList.add(feature);
    }

    public String toCSV() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < featureList.size(); i++) {
            Feature feature = featureList.get(i);
            for (String featureName : feature.getFeatureNames()) {
                output.append(featureName + Constants.CSV_SEPARATOR);
            }
        }
        output.append(Constants.LINE_BREAK);
        for (int i = 0; i < featureList.size(); i++) {
            Feature feature = featureList.get(i);
            for (String featureName : feature.getFeatureNames()) {
                int counter = feature.getFeatureCounter(featureName);
                output.append(counter + Constants.CSV_SEPARATOR);
            }
        }
        return output.toString();
    }

    @Override
    public String toString() {
        JSONObject mergedJSON = new JSONObject();
        for (int i = 0; i < featureList.size(); i++) {
            Feature feature = featureList.get(i);
            JSONObject jsonObject = new JSONObject(feature.toString());
            for (String key : JSONObject.getNames(jsonObject)) {
                mergedJSON.put(key, jsonObject.get(key));
            }
        }
        return mergedJSON.toString(4);
    }

}