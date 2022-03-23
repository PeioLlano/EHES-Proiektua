package code;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		//1. Pausua --> Lortu: train.arff
			//Non gordetzen da train.arff? files/train.arff
			Instances dataTrain = PreProcessTrain.preProcess(LagMethods.relative2absolute("src/files/train.txt"));
			
			System.exit(0);
			
		//2. Pausua --> Test eta train bateragarri egitea, test.arff lortu
			//Non gordetzen da test.arff? files/test.arff
			Instances dataTest = PreProcessTest.preProcess(dataTrain, LagMethods.relative2absolute("src/files/test.txt"));
		
		//3. Pausua --> Parametro ekorketa
			//Non gordetzen da classifier.model? files/classifier.model
			MultilayerPerceptron model = ParametroEkorketa.parametroakEkortu(dataTrain);
		
		//4. Pausua --> Iragarpenak egitea
			//Non gordetzen dira iragarpenak? files/iragarpenak.txt
			IragarpenSortzailea.eginIragarpenak(dataTest, model, "Path");	
			
	}

}
