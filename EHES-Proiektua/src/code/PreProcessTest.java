package code;

import weka.core.Instances;


public class PreProcessTest {

	public static Instances preProcess(Instances dataTrain, String dataTestPath) throws Exception {
		PreProcessTest ppt = new PreProcessTest();
		
		// Textu gordina izanik dataTest
		Instances dataTest = ppt.raw2arff(dataTestPath);
		dataTest = ppt.fixedDictionaryStringToWordVector(dataTrain, dataTest);
		
		return dataTest;
	}
	
	public Instances raw2arff(String dataTest) {
		return null;
	}
	
	public Instances fixedDictionaryStringToWordVector(Instances dataTrain, Instances dataTest) throws Exception {
		
		
		return null;
	}
}
