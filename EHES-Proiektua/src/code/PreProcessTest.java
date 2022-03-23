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
        
      //-R <index1,index2-index4,...>
		  //Specify list of string attributes to convert to words (as weka Range).
        testOptions[0] = "-R";
        testOptions[1] = "first-last";
        
//      -L
//  		Convert all tokens to lowercase before adding to the dictionary.
//    		Convierta todos los tokens a minúsculas antes de agregarlos al diccionario.
//  	Hiztegian sartu baino lehen, izki guztiak letra xehean jarri.
        testOptions[2] = "-L";
        
//      -dictionary <path to save to>
	//  	The file to save the dictionary to.
	//  	(default is not to save the dictionary)
        testOptions[3] = "-dictionary";
        testOptions[4] = "Dictionary.txt";

        
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
