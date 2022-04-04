package code;

import java.io.File;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.attribute.Reorder;

public class PreProcessDev {
	/**
	 * Klasea definituak ez duten SMS-ak dituen fitxategia jaso eta Intances motako datu sorta bueltatzen dut 'PreProcess'-aren metodo guztiak aplikatu ondoren.
	 * @param dataPath dev datu sortaren fitxategia dagoen path-a.
	 * @return PreProcess osoa aplikatu ondorengo Instances motako data sorta.
	 */
	public static Instances preProcess(String dataDevPath) throws Exception {
		PreProcessDev ppd = new PreProcessDev();
		
		// Textu gordina izanik dataDev lortu
		Instances dataDev = ppd.text2raw(dataDevPath);
		// dataDev eta dataTrain-en preprocess bukatu onderen lortutako hiztegia izanik,  fixedDictionaryStringToWordVector filtroa aplikatu
		dataDev = ppd.fixedDictionaryStringToWordVector(dataDev);
		// Aurreko filtroak klase atributua hasieran jartzen du, hurrengo honekin amaieran jarriko dugu.
		dataDev = ppd.reorderClass(dataDev);
		return dataDev;
	}
	
	
	/**
	 * Textu fitxategia jasota arff formatura pasatu gorde eta Java-ko Instances motako datu sorta moduan bueltatuko du. Honen kasuan, kontuan izango du klasea ezezaguna izango dela.
	 * @param dataPath Textu fitxategi garbia egongo den 'path'-a.
	 * @return Textuan zegoen fitxategia Instances motako datu sorta moduan.
	 */
	public Instances text2raw(String dataPath) throws Exception {
		String output = LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.train_raw.arff");
		Instances data = LagMethods.txt2Intances(dataPath,output);
		data.setClassIndex(0);
		return data;
	}
	
	/**
	 * Lehenengo arff fitxategia jasota, datu sorta horri atibutuekiko ez-gainbegiratutako 'Fixed Dictionaty String to Word Vector' textu mehatzaritza filtroa aplikatuko zaio eta moldatutako datu sorta bueltatuko da.
	 * @param Data 'raw'-tik 'arff'-ra pasatutako datu sorta, Intances motako objektua.
	 * @return Filtroa aplikatu ondorengo datu sorta.
	 */
	public static Instances fixedDictionaryStringToWordVector(Instances dataDev) throws Exception {
		        
		//Converts String attributes into a set of attributes representing word occurrence (depending on the tokenizer) information from the text contained in the strings.
        FixedDictionaryStringToWordVector fixedDictionary = new FixedDictionaryStringToWordVector();
        
        //Aukerak izango dituen zerrenda hasieratu.
        String[] devOptions = new String[5];
        
      //-R <index1,index2-index4,...>
		  //Specify list of string attributes to convert to words (as weka Range).
        devOptions[0] = "-R";
        devOptions[1] = "first-last";
        
//      -L
//  		Convert all tokens to lowercase before adding to the dictionary.
//    		Convierta todos los tokens a minúsculas antes de agregarlos al diccionario.
//  	Hiztegian sartu baino lehen, izki guztiak letra xehean jarri.
        devOptions[2] = "-L";
        
//      -dictionary <path to save to>
	//  	The file to save the dictionary to.
	//  	(default is not to save the dictionary)
        devOptions[3] = "-dictionary";
        devOptions[4] = "DictionaryFSS.txt";

        //Pasatutako aukera zerrenda, filtroaren aukera bezala esleitu.
        fixedDictionary.setOptions(devOptions);
        //Set the dictionary file to read from
        fixedDictionary.setDictionaryFile(new File(LagMethods.relative2absolute("src/files/DictionaryFSS.txt")));
		//Sets the format of the input instances.
        fixedDictionary.setInputFormat(dataDev);
		//Erabili filtroa.
        Instances dataDevBoW = Filter.useFilter(dataDev,fixedDictionary);
        
        //0 posizioko atributua klase bezala esleitu.
        dataDevBoW.setClassIndex(0);

		//Gorde jarritako fitxategian.

        //LagMethods.saver(LagMethods.relative2absolute("src/files/SMS_SpamCollection.dev.arff"), dataDevBoW);
				
		return dataDevBoW;
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
		//LagMethods.saver(LagMethods.relative2absolute("src/files/SMS_SpamCollection.dev_shorted.arff"), data);
		return data;
	}
}

