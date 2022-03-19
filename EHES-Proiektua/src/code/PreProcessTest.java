package code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

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
		FixedDictionaryStringToWordVector filtr = new FixedDictionaryStringToWordVector();
		//outputWordCounts -- Output word counts rather than boolean 0 or 1(indicating presence or absence of a word)
		filtr.setOutputWordCounts(true);
		//lowerCaseTokens -- If set then all the word tokens are converted to lower case before being added to the dictionary.
		filtr.setLowerCaseTokens(true);
		//Datu sortaren formatua esleitu
		filtr.setInputFormat(dataTrain);
		//Set the dictionary file to save the dictionary to.
		filtr.setDictionaryFile(new File(LagMethods.relative2absolute("/src/files/dictionary.txt")));
		//Filtroa erabili datarekin
		Instances dataTestBoW = Filter.useFilter(dataTest, filtr);
		
		return dataTestBoW;
	}
	
//	public Instances goiburuBateratu(Instances dataTrain, Instances dataTest) throws Exception {
//		
//		//ECLIPSE-tik FILE-era pasatzeaz arduratuko den 'stremer'-a.
//		FileOutputStream os = new FileOutputStream("------Donde queramos dejarlo-----");
//		PrintStream ps = new PrintStream(os);
//		
//		//Arazketa mezuak.
//		System.out.println("Hasierako train atributu kopurua: " + dataTrain.numAttributes());
//		System.out.println("Hasierako test atributu kopurua: " + dataTest.numAttributes());
//	
//		//Ezabatu behar izango diren atributuak sartzeko zerrenda hasieratu.
//		ArrayList<Integer> indizeak = new ArrayList<Integer>();
//		
//		//Test-erako data sortaren atributu bakoitzeko.
//		for(int i = 0; i<dataTest.numAttributes(); i++) {
//			//Atributuaren izena lortu.
//			String name = dataTest.attribute(i).name();
//			//Atributuaren izena entrenamendu data sortaren atributuen artean ez badago...
//			if(dataTrain.attribute(name) != null) {
//				//Ezabatu behar izango diren atributuen zerrendan sartu.
//				indizeak.add(i);
//			}
//		}
//		
//		//Formatua dela eta egin beharreko aldaketak
//		int[] ind = indizeak.stream().mapToInt(Integer::intValue).toArray();
//		
//		//Atributuak ezabatzeko filtroa sortu eta hasieratu:
//			// setAttributeIndicesArray --> Zein indize ezabatu
//			// setInvertSelection --> Zer remove zatiarekin geratzen garen, true (indizeen zerrendan ez dauden atributuekin) edo false (indizeen zerrendan dauden atributuekin)
//			// setInputFormat --> Test-erako data sortaren formatua hartu
//		Remove removeFilter = new Remove();
//		removeFilter.setAttributeIndicesArray(ind);
//		removeFilter.setInvertSelection(true);
//		removeFilter.setInputFormat(dataTest);
//		
//		//Filtroa aplikatu
//		Instances newData = Filter.useFilter(dataTest, removeFilter);
//		
//		//Arazketa mezuak.
//		System.out.println("Amaierako train atributu kopurua: " + dataTrain.numAttributes());
//		System.out.println("Amaierako test atributu kopurua: " + newData.numAttributes());
//		
//		//Beharreko atributuak dituen test-erako datu sorta gorde.
//		ps.print(newData);
//		ps.close();
//		
//		return newData;
//	}
}
