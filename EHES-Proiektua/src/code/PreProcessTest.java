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

/**
 * Klase honek test datu sortaren aurreprozesamendua aplikatuko dut, 'txt' motako fitxategia izatetik 'arff' motako fitxategi bat izatera.
 * <p>
 * Date: Mar 30-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio
 *
 */
public class PreProcessTest {

	/**
	 * Klasea definituak ez duten SMS-ak dituen fitxategia jaso eta izan behar dituen atributuen hiztegia izanik, Intances motako datu sorta bueltatzen du 'PreProcess'-aren metodo guztiak aplikatu ondoren.
	 * @param dataPath Test datu sortaren fitxategia dagoen path-a.
	 * @return PreProcess osoa aplikatu ondorengo Instances motako data sorta.
	 */
	public static Instances preProcess(String dataTestPath) throws Exception {
		PreProcessTest ppt = new PreProcessTest();
		
		// Textu gordina izanik DataTest lortu
		Instances dataTest = ppt.text2raw(dataTestPath);
		// DataTest eta dataTrain-en preprocess bukatu onderen lortutako hiztegia izanik,  fixedDictionaryStringToWordVector filtroa aplikatu
		dataTest = ppt.fixedDictionaryStringToWordVector(dataTest);
		// Aurreko filtroak klase atributua hasieran jartzen du, hurrengo honekin amaieran jarriko dugu.
		dataTest = ppt.reorderClass(dataTest);
		
		LagMethods.saver(LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.test.arff"), dataTest);

		return dataTest;
	}
	
	
	/**
	 * Textu fitxategia jasota arff formatura pasatu gorde eta Java-ko Instances motako datu sorta moduan bueltatuko du. Honen kasuan, kontuan izango du klasea ezezaguna izango dela.
	 * @param dataPath Textu fitxategi garbia egongo den 'path'-a.
	 * @return Textuan zegoen fitxategia Instances motako datu sorta moduan.
	 */
	public Instances text2raw(String dataPath) throws Exception {
		String output = LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.test_blind_raw.arff");
		
    	//StringBuilder in Java is a class used to create a mutable, or in other words, a modifiable succession of characters.
		StringBuilder report = new StringBuilder();
    	//Goiburuak sortuko dugun ARFF-ak zentzua izan dezan.
			//ARFF-aren 'izenburua'
		    report.append("@relation SMS_SpamCollection\n");
	    	report.append("\n");
	    	
	    	//ARFF-aren atibutuen moten definizioa
		    report.append("@attribute klasea {spam, ham}\n");
		    report.append("@attribute testua string\n");
	    	report.append("\n");
	    
    	//Java BufferedReader is a public Java class that reads text, using buffering to enable large reads 
    	//at a time for efficiency, storing what is not needed immediately in memory for later use.    
	    BufferedReader br = new BufferedReader(new FileReader(dataPath));
	    //Uneko ilara
	    String sCurrentLine;
	    //Data sorta hasten dela definitu
		report.append("@data\n");
		//Uneko ilara null ez den bitartean...
	    while ((sCurrentLine = br.readLine()) != null) {
	    	//Uneko ilara banatu tabulazioa ikusten duen unean.
	    	//Klasea beti '?' izango da.
	    	String klasea = "?";
	    	//Ilara osoa textua izango da.
	    	String textua = sCurrentLine;
	    	//Textua "-rik dagoen bilatu (arazoak ematen dituzte string-a bukatu dela pentsatzen baitu)
	    	ArrayList<Integer> list = new ArrayList<>();
	    	int index = textua.indexOf("\"");
	    	while (index >= 0) {
	    		list.add(index);
	    		index = textua.indexOf("\"", index + 1);
	    	}

	    	//Behin "-ren posizioa jakinda '\' bat jarriko diogu aurrean textu irakurketan ez erratzeko
	    	Integer i = 0; 
	    	for (Integer integer : list) {
	    		textua = textua.substring(0, integer+i) + "\\" + textua.substring(integer+i);
	    		i++;
			}
	    	//ARFF formatua jarraituz ilara gorde, hau da: klasea, textua
		    report.append(klasea + ", \"" + textua + "\"\n");
	    }
	    
	    //Gorde sortutakoa fitzategi batean.
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
		writer.write(report.toString());
		writer.close();
	    br.close();
	    
	    return  LagMethods.path2instances(output);
	}
	
	/**
	 * Lehenengo arff fitxategia jasota, datu sorta horri atibutuekiko ez-gainbegiratutako 'Fixed Dictionaty String to Word Vector' textu mehatzaritza filtroa aplikatuko zaio eta moldatutako datu sorta bueltatuko da.
	 * @param Data 'raw'-tik 'arff'-ra pasatutako datu sorta, Intances motako objektua.
	 * @return Filtroa aplikatu ondorengo datu sorta.
	 */
	public static Instances fixedDictionaryStringToWordVector(Instances dataTest) throws Exception {
		        
		//Converts String attributes into a set of attributes representing word occurrence (depending on the tokenizer) information from the text contained in the strings.
        FixedDictionaryStringToWordVector fixedDictionary = new FixedDictionaryStringToWordVector();
        
        //Aukerak izango dituen zerrenda hasieratu.
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

        //Pasatutako aukera zerrenda, filtroaren aukera bezala esleitu.
        fixedDictionary.setOptions(testOptions);
        //Set the dictionary file to read from
        fixedDictionary.setDictionaryFile(new File(LagMethods.relative2absolute("src/outputFiles/DictionaryFSS.txt")));
		//Sets the format of the input instances.
        fixedDictionary.setInputFormat(dataTest);
		//Erabili filtroa.
        Instances dataTestBoW = Filter.useFilter(dataTest,fixedDictionary);
        
        //0 posizioko atributua klase bezala esleitu.
        dataTestBoW.setClassIndex(0);

		//Gorde jarritako fitxategian.

        //LagMethods.saver(LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.test_blind.arff"), dataTestBoW);
				
		return dataTestBoW;
	}
	
	
	/**
	 * Klasea hasieran duen Instances motako datu sorta jaso eta klasea amaieran duen datu sorta bueltatuko du.
	 * @param data Klasea hasieran duen Instances motako datu sorta.
	 * @return Klasea amaieran duen Instances motako datu sorta.
	 */
	public static Instances reorderClass(Instances data) throws Exception {
		//A filter that generates output with a new order of the attributes.
		Reorder reorder = new Reorder();
		//2-atributu kop, 1.  2-atributu kop bitarteko atributuak goian jarriko dira eta 1 atributua (klasea dena) amaieran.
		reorder.setAttributeIndices("2-last,1"); 
		//Sets the format of the input instances.
		reorder.setInputFormat(data);
		//Erabili filtroa.
		data = Filter.useFilter(data,reorder);
		//Gorde jarritako fitxategian.
		//LagMethods.saver(LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.test_blind_shorted.arff"), data);
		return data;
	}
}
