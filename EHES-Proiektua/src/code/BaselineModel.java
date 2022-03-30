package code;

import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;

public class BaselineModel {

	public static NaiveBayes getBaselineModel(Instances data) throws Exception {

        NaiveBayes nb = new NaiveBayes();
        nb.buildClassifier(data);
        
        return nb;
	}
	
	public static void evaluateBaseline(Instances data) throws Exception {
        
        SerializationHelper.write("Hemen gorde baseline modeloa", getBaselineModel(data));
        
        KalitatearenEstimazioa.kalitateaEstimatu(data, getBaselineModel(data), "Path to save");
  	}
	
	public static ArrayList<Double> getBaselineFmeasure(Instances data) throws Exception {
		ArrayList<Double> emaitza = new ArrayList<>();
		
		int minoritarioa = Utils.minIndex(data.attributeStats(data.classIndex()).nominalCounts);
		NaiveBayes nb = getBaselineModel(data);
		//HOLD-OUT
		
		Instances dataTest = LagMethods.holdOut("test", data, 70.0, 1);
		Instances dataTrain = LagMethods.holdOut("train", data, 70.0, 1);

		//Modeloa entrenatu.
		nb.buildClassifier(dataTrain);
		
		//Iragarpenak egiteaz arduratuko den ebaluatzailea.
		Evaluation evHO = new Evaluation(dataTrain);
		
		//Modeloa ebaluatu.
		evHO.evaluateModel(nb, dataTest);
		LagMethods.printResults(System.out, evHO, "BASELINE");
        
		emaitza.add(evHO.fMeasure(minoritarioa));
		emaitza.add(evHO.pctUnclassified());

        return emaitza;
  	}

}
