package code;

import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;

/**
 * Proiektuan zehar kalkulu azkarra atera ahal izateko Baseline-a kudeatuko duen klasea.
 * 
 * Date: Mar 30-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio
 *
 */
public class BaselineModel {

	/**
	 * Datu sorta bat jasota, horren baseline sailkatzailea bueltatuko du.
	 * @param data Noren baseline sailkatzailea lortu nahi den datu sorta.
	 * @return Naive Bayes baseline sailkatzailea.
	 */
	public static NaiveBayes getBaselineModel(Instances data) throws Exception {

        NaiveBayes nb = new NaiveBayes();
        nb.buildClassifier(data);
        
        return nb;
	}
	
	/**
	 * Datu sorta bat jasota, Hold-Out, Zintzoa eta 10-cross validation aplikatu eta gero, horren emaitzak inprimatuko dituen metodoa.
	 * @param data Noren baseline emaitzak lortu nahi diren datu sorta.
	 */
	public static void evaluateBaseline(Instances data) throws Exception {
        
        SerializationHelper.write("Hemen gorde baseline modeloa", getBaselineModel(data));
        
        KalitatearenEstimazioa.kalitateaEstimatu(data, getBaselineModel(data), "Path to save");
  	}
	
	/**
	 * Datu sorta bat jasota, Hold-Out aplikatu eta gero, horren emaitza konkretu batzuk bueltatuko dituen metodoa.
	 * @param data Noren baseline emaitzak lortu nahi diren datu sorta.
	 * @return Emaitzen ArrayList bat bueltatuko da. O posizioan fMeasure eta 1 posizioan pctUnclassified.
	 */
	public static ArrayList<Double> getBaselineMeasures(Instances data) throws Exception {
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
