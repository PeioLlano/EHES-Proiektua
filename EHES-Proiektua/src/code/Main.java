package code;

import java.nio.file.FileSystems;

import weka.core.Instances;

public class Main {
	
	//relative2absolute
	String absolutePath = FileSystems.getDefault().getPath("src/files/froga.txt").normalize().toAbsolutePath().toString();
	
	public static void main(String[] args) throws Exception {
		
		//1. Pausua --> Lortu: train.arff
			//Non gordetzen da train.arff? files/train.arff
			Instances dataTrain = PreProcessTrain.preProcess("path del archivo de train");
			
		//2. Pausua --> Test eta train bateragarri egitea, test.arff lortu
			//Non gordetzen da test.arff? files/test.arff
			Instances dataTest = PreProcessTest.preProcess(dataTrain, "path del archivo de test");
		
		//...
			
	}
}
