package code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;


public class PreProcessTest {

	public static Instances preProcess(Instances dataTrain, String dataTestPath) throws Exception {
		PreProcessTest ppt = new PreProcessTest();
		
		// Textu gordina izanik dataTest
		Instances dataTest = ppt.raw2arff(dataTestPath);
		dataTest = ppt.fixedDictionaryStringToWordVector(dataTest);
		
		return dataTest;
	}
	
	public Instances raw2arff(String dataTest) {
		return null;
	}
	
	public Instances fixedDictionaryStringToWordVector(Instances dataTest) throws Exception {
		
//################################################################ TEST BOW ################################################################
        
        FixedDictionaryStringToWordVector fixedDictionary = new FixedDictionaryStringToWordVector();
        
        String[] testOptions = new String[5];
        testOptions[0] = "-R";
        testOptions[1] = "first-last";
        testOptions[2] = "-dictionary";
        testOptions[3] = "Dictionary.txt";
        testOptions[4] = "-L";
        
        fixedDictionary.setOptions(testOptions);
        fixedDictionary.setDictionaryFile(new File(LagMethods.relative2absolute("src/files/Dictionary.txt")));
        fixedDictionary.setInputFormat(dataTest);
        Instances dataTestBoW = Filter.useFilter(dataTest,fixedDictionary);
        
        dataTestBoW.setClassIndex(0);

//      FileOutputStream osTest = new FileOutputStream(args[3]);
//		PrintStream psTest = new PrintStream(osTest);
//		psTest.print(dataTestBoW);
//		psTest.close();
		
		//################################################################ TEST BOW ################################################################
		
		return dataTestBoW;
	}
}
