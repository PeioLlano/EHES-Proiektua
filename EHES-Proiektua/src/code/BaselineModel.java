package code;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class BaselineModel {

	public static Classifier getBaselineModel(Instances data) throws Exception {

        NaiveBayes nb = new NaiveBayes();
        nb.buildClassifier(data);
        
        return nb;
	}
	
	public static void evaluateBaseline(Instances data) throws Exception {
        
        SerializationHelper.write("Hemen gorde baseline modeloa", getBaselineModel(data));
        
        KalitatearenEstimazioa.kalitateaEstimatu(data, getBaselineModel(data), "Path to save");
  	}
	
}
