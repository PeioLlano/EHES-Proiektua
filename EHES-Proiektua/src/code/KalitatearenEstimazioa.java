package code;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Klase honetan lortutako sailkatzailearekin eta data finalaren kalitatearen estimazioa egingo da.
 * <p>
 * Date: Apr 06-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio
 *
 */
public class KalitatearenEstimazioa {

	/**
	 * Metodo honen bidez jasotako sailkatzailearekin eta datu-sortarekin kalitatearen estimazioa egingo da eta hirugarren atributuaren path-ean gordeko dira..
	 * 
	 * @param Data Kalitatea estimatzeko erabiliko den datu-sorta.
	 * @param cls Kalitatea estimatzeko bezala erabiliko den sailkatzailea.
	 * @param path2Save Emaitzak gordetzeko helbidea.
	 */
	public static void kalitateaEstimatu(Instances data, Classifier cls, String path2Save) throws Exception {
		
		//ECLIPSE-tik FILE-era pasatzeaz arduratuko den 'stremer'-a.
		FileOutputStream fos = new FileOutputStream(path2Save);
		PrintStream ps = new PrintStream(fos);
		
		//ZINTZOA
		
			//Iragarpenak egiteaz arduratuko den ebaluatzailea.
			Evaluation evZ = new Evaluation(data);
			
			//Modeloa ebaluatu.
			evZ.evaluateModel(cls, data);
		
		//HOLD-OUT
		
			Instances dataTest = LagMethods.holdOut("test", data, 70.0, 1);
			Instances dataTrain = LagMethods.holdOut("train", data, 70.0, 1);

			//Modeloa entrenatu.
			cls.buildClassifier(dataTrain);
			
			//Iragarpenak egiteaz arduratuko den ebaluatzailea.
			Evaluation evHO = new Evaluation(dataTrain);
			
			//Modeloa ebaluatu.
			evHO.evaluateModel(cls, dataTest);
			
			
		//k-CROSS VALIDATION
			
			//Iragarpenak egiteaz arduratuko den ebaluatzailea.
			Evaluation evCV = new Evaluation(data);
			
			//Modeloa ebaluatu.
			evCV.crossValidateModel(cls, data, 10, new Random(1));
			
		//Kalitatearen estimazioa fitxategian gorde.
			LagMethods.printHeader(ps);
			
			ps.println("#############################################################");
			ps.println("#                                                           #");
			ps.println("#                    KALITATEAREN ESTIMAZIOA                #");
			ps.println("#                                                           #");
			ps.println("#############################################################");
			
			//ZINTZOA
				LagMethods.printResults(ps, evZ, "ZINTZOA");
				
			//HOLD-OUT
				LagMethods.printResults(ps, evHO, "HOLD-OUT (70%)");
			
			//k-CROSS VALIDATION
				LagMethods.printResults(ps, evCV, "10-FOLD CROSS VALIDATION");
	
		ps.close();
	}
}
