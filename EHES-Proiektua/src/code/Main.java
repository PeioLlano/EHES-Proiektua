package code;

import java.io.File;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * Proiektu osoaren fluxua gidatuko duen klasea.
 * <p>
 * Date: Mar 30-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio
 *
 */
public class Main {
	
	/**
	 * Klase beste guztietan aplikatutako prozesuak bateratzen dituen 'main' metodo nagusia.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		LagMethods.printHeader(System.out);
		ArrayList<String> finalOutputs = new ArrayList<>();
		finalOutputs.add("SMS_SpamCollection.train.arff");finalOutputs.add("SMS_SpamCollection.test.arff");finalOutputs.add("SMS_SpamCollection.dev.arff");finalOutputs.add("SMS_SpamCollection.dataBuild.arff");finalOutputs.add("DictionaryFSS.txt");
		File outputDir = new File("src/outputFiles");
		
		//1. Pausua --> Lortu: train.arff
			//Non gordetzen da train.arff? filesOutput/train.arff
			System.out.println("--------------------------------------------------------- PRE-PROCESS TRAIN ---------------------------------------------------------\n");
			Instances dataTrain = PreProcessTrain.preProcess(LagMethods.relative2absolute("src/inputFiles/SMS_SpamCollection.train.txt"));
			

		//2. Pausua --> Test eta train bateragarri egitea, test.arff lortu
			//Non gordetzen da test.arff? filesOutput/test.arff
			System.out.println("\n--------------------------------------------------------- PRE-PROCESS TEST ---------------------------------------------------------\n");
			Instances dataTest = PreProcessTest.preProcess(LagMethods.relative2absolute("src/inputFiles/SMS_SpamCollection.test_blind.txt"));
		
			System.out.println("Goiburuak berdina dira? " + dataTest.equalHeaders(dataTrain));
			
		//3. Pausua --> Dev eta train bateragarri egitea, dev.arff lortu
			//Non gordetzen da dev.arff? filesOutput/dev.arff
			System.out.println("\n--------------------------------------------------------- PRE-PROCESS DEV ---------------------------------------------------------\n");
			Instances dataDev = PreProcessDev.preProcess(LagMethods.relative2absolute("src/inputFiles/SMS_SpamCollection.dev.txt"));
		
			System.out.println("Goiburuak berdina dira? " + dataDev.equalHeaders(dataTrain));
			
		//4. Pausua --> Parametro ekorketa
			//Non gordetzen da classifier.model? filesOutput/classifier.model
			//Non gordetzen da dataTrain+dataDev? filesOutput/dataBuild.arff
			System.out.println("\n--------------------------------------------------------- PARAMETRO EKORKETA ---------------------------------------------------------\n");
			Classifier model = ParametroEkorketa.parametroakEkortu(dataTrain, dataDev);
			
		//5. Pausua --> Kalitatearen estimazioa
			System.out.println("\n--------------------------------------------------------- KALITATEAREN ESTIMAZIOA ---------------------------------------------------------\n");
			model = (Classifier) SerializationHelper.read("src/models/sailkatzaile.model");
			Classifier baselineModel = (Classifier) SerializationHelper.read("src/models/baseline.model");
			//Non gordetzen da kalitatearen estimazioa? bezeroEntrega/KalitatearenEstimazioa.txt
			KalitatearenEstimazioa.kalitateaEstimatu(dataTrain, model, "src/bezeroEntrega/KalitatearenEstimazioa.txt");
			//Non gordetzen da baselineren kalitatearen estimazioa? bezeroEntrega/KalitatearenEstimazioaBaseline.txt
			KalitatearenEstimazioa.kalitateaEstimatu(dataTrain, baselineModel, "src/bezeroEntrega/KalitatearenEstimazioaBaseline.txt");
			
		//6. Pausua --> Iragarpenak egitea
			System.out.println("\n--------------------------------------------------------- IRAGARPENAK SORTZEN ---------------------------------------------------------\n");
			//Non gordetzen dira iragarpenak? bezeroEntrega/iragarpenak.txt
			IragarpenSortzailea.eginIragarpenak(dataTest, model, "src/bezeroEntrega/Iragarpenak.txt");
			//Non gordetzen dira iragarpenak? bezeroEntrega/iragarpenakBaseline.txt
			IragarpenSortzailea.eginIragarpenak(dataTest, baselineModel, "src/bezeroEntrega/IragarpenakBaseline.txt");	
			
		System.out.println("\n--------------------------------------------------------- GEHIGARRIAK ---------------------------------------------------------\n");
		LagMethods.deleteFiles(outputDir.listFiles(), finalOutputs);
	}

}
