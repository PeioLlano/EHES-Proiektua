package code;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

public class KalitatearenEstimazioa {

	public static void kalitateaEstimatu(Instances data, NaiveBayes nb) throws Exception {
		
		//ECLIPSE-tik FILE-era pasatzeaz arduratuko den 'stremer'-a.
		FileOutputStream fos = new FileOutputStream("-------------Non doan jarri behar da-------------");
		PrintStream ps = new PrintStream(fos);
		
		//HOLD-OUT
		
			//Datu sorta randomizatu.
			Randomize r = new Randomize();
			r.setSeed(1);
			r.setInputFormat(data);
			Instances dataRan = Filter.useFilter(data, r);
			
			//Split percentage definitu duen paramatroa hasieratu.
			int percent = 70;
			
			//Datu sortatik 'Train' bezala erabiliko dugun data sorta atera.
			RemovePercentage rp = new RemovePercentage();
			rp.setInputFormat(dataRan);
			rp.setPercentage(percent);
			rp.setInvertSelection(true);
			Instances dataTrain = Filter.useFilter(dataRan, rp);
			
			//Datu sortatik 'Train' bezala erabiliko dugun data sorta atera.
			rp.setInputFormat(dataRan);
			rp.setInvertSelection(false);
			Instances dataTest = Filter.useFilter(dataRan, rp);
			
			//Modeloa entrenatu.
			nb.buildClassifier(dataTrain);
			
			//Iragarpenak egiteaz arduratuko den ebaluatzailea.
			Evaluation evHO = new Evaluation(dataTrain);
			
			//Modeloa ebaluatu.
			evHO.evaluateModel(nb, dataTest);
			
			
		
		//k-CROSS VALIDATION
			
			//Iragarpenak egiteaz arduratuko den ebaluatzailea.
			Evaluation evCV = new Evaluation(data);
			
			//Modeloa ebaluatu.
			evCV.crossValidateModel(nb, data, 10, new Random(1));
		
			
		//Kalitatearen estimazioa fitxategian gorde.
			
			//HOLD-OUT
				ps.println(evHO.toSummaryString("\nHOLD-OUT SUMMARY", false));
				ps.println(evHO.toMatrixString("HOLD-OUT CONFUSSION MATIX"));
			
			//k-CROSS VALIDATION
				ps.println(evCV.toSummaryString("\n10-CROSS VALIDATION SUMMARY", false));
				ps.println(evCV.toMatrixString("10-CROSS VALIDATION CONFUSSION MATIX"));
		
	
		ps.close();
	}
}
