package code;
import java.nio.file.FileSystems;

import code.PreProcessTrain;

public class Main {
	
	//relative2absolute
	String absolutePath = FileSystems.getDefault().getPath("src/files/froga.txt").normalize().toAbsolutePath().toString();
	
	public static void main(String[] args) throws Exception {
		
		//1. Pausua --> Lortu: train.arff
			//Non gordetzen da train.arff? files/train.arff
			PreProcessTrain.preProcess();
			
		//2. Pausua --> Test eta train bateragarri egitea, test.arff lortu
			//Non gordetzen da test.arff? files/test.arff
		
		//...
			
	}
}
