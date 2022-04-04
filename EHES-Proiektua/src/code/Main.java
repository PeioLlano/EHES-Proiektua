package code;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

/**
 * Proiektu osoaren fluxua gidatuko duen klasea.
 * 
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
		
		//1. Pausua --> Lortu: train.arff
			//Non gordetzen da train.arff? files/train.arff
			Instances dataTrain = PreProcessTrain.preProcess(LagMethods.relative2absolute("src/inputFiles/SMS_SpamCollection.train.txt"));
			
			System.exit(0);

		//2. Pausua --> Test eta train bateragarri egitea, test.arff lortu
			//Non gordetzen da test.arff? files/test.arff
			Instances dataTest = PreProcessTest.preProcess(LagMethods.relative2absolute("src/inputFiles/SMS_SpamCollection.test_blind.txt"));
		
			System.out.println("Goiburuak berdina dira? " + dataTest.equalHeaders(dataTrain));
			
			System.exit(0);
		//3. Pausua --> Parametro ekorketa
			//Non gordetzen da classifier.model? files/classifier.model
			MultilayerPerceptron model = ParametroEkorketa.parametroakEkortu(dataTrain);
		
		//4. Pausua --> Iragarpenak egitea
			//Non gordetzen dira iragarpenak? files/iragarpenak.txt
			IragarpenSortzailea.eginIragarpenak(dataTest, model, "Path");	
			
	}

}
