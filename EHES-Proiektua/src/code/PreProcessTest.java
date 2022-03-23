package code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.attribute.Reorder;


public class PreProcessTest {

	public static Instances preProcess(Instances dataTrain, String dataTestPath) throws Exception {
		PreProcessTest ppt = new PreProcessTest();
		
		// Textu gordina izanik dataTest
		Instances dataTest = ppt.raw2arff(dataTestPath);
		dataTest = ppt.fixedDictionaryStringToWordVector(dataTest);
		dataTest = ppt.reorderClass(dataTest);
		return dataTest;
	}
	
	public Instances raw2arff(String dataPath) throws Exception {
		String output = LagMethods.relative2absolute("src/files/SMS_SpamCollection.test_blind_raw.arff");
		
		StringBuilder report = new StringBuilder();
	    report.append("@relation SMS_SpamCollection\n");
    	report.append("\n");
	    report.append("@attribute klasea {spam, ham}\n");
	    report.append("@attribute testua string\n");
    	report.append("\n");
	    
	    BufferedReader br = new BufferedReader(new FileReader(dataPath));
	     String sCurrentLine;
	     
		report.append("@data\n");
	    while ((sCurrentLine = br.readLine()) != null) {
	    	String klasea = "?";
	    	String textua = sCurrentLine;
	    	ArrayList<Integer> list = new ArrayList<>();
	    	int index = textua.indexOf("\"");
	    	while (index >= 0) {
	    		list.add(index);
	    		index = textua.indexOf("\"", index + 1);
	    	}

	    	Integer i = 0; 
	    	for (Integer integer : list) {
	    		textua = textua.substring(0, integer+i) + "\\" + textua.substring(integer+i);
	    		i++;
			}
		    report.append(klasea + ", \"" + textua + "\"\n");
	    }
	    
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
		writer.write(report.toString());
		writer.close();
	    br.close();
	    
	    return  LagMethods.path2instances(output);
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
        testOptions[4] = "DictionaryFSS.txt";

        
        fixedDictionary.setOptions(testOptions);
        fixedDictionary.setDictionaryFile(new File(LagMethods.relative2absolute("src/files/DictionaryFSS.txt")));
        fixedDictionary.setInputFormat(dataTest);
        Instances dataTestBoW = Filter.useFilter(dataTest,fixedDictionary);
        
        dataTestBoW.setClassIndex(0);

        LagMethods.saver(LagMethods.relative2absolute("src/files/SMS_SpamCollection.test_blind.arff"), dataTestBoW);
//      FileOutputStream osTest = new FileOutputStream(args[3]);
//		PrintStream psTest = new PrintStream(osTest);
//		psTest.print(dataTestBoW);
//		psTest.close();
		
		//################################################################ TEST BOW ################################################################
		
		return dataTestBoW;
	}
	
	public Instances reorderClass(Instances data) throws Exception {
		Reorder reorder = new Reorder();
		reorder.setAttributeIndices("2-last,1"); //2-atributu kop, 1.  2-atributu kop bitarteko atributuak goian jarriko dira eta 1 atributua (klasea dena) amaieran.
		reorder.setInputFormat(data);
		data = Filter.useFilter(data,reorder);
		LagMethods.saver(LagMethods.relative2absolute("src/files/SMS_SpamCollection.test_blind_shorted.arff"), data);
		return data;
	}
}
