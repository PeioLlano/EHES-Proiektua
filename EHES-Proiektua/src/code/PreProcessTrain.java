package code;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;


/**
 * Klase honek train datu sortaren aurreprozesamendua aplikatuko dut, 'txt' motako fitxategia izatetik 'arff' motako fitxategi bat izatera.
 * <p>
 * Date: Mar 30-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio

 */
public class PreProcessTrain {

	/**
	 * Spam eta ham motako SMS-ak dituen fitxategia jaso eta Intances motako datu sorta bueltatzen dut 'PreProcess'-aren metodo guztiak aplikatu ondoren.
	 * @param dataPath Fitxategia dagoen path-a.
	 * @return PreProcess osoa aplikatu ondorengo Instances motako data sorta.
	 */
	public static Instances preProcess(String dataPath) throws Exception {
		PreProcessTrain ppt = new PreProcessTrain();
		
		Instances dataTrain = null;
		Double fMesOptimoa = 0.0;
		Instances dataSortaOpt = null;
		String garbitzeStringOpt = null;
		File hiztOpt = null;
		String[] string2vectorOptionsOpt = null;

	//AURREPROZESAMENDUA
		
		String[] garbiketa = {".,/?:-#&Â¼”â*|·~¬¨çºªÃœ"};

		String[][] string2vectorOptions = ppt.getOptionsStringToVector();
		
		Integer i = 1;
		for (String gabitzeString : garbiketa) {
			
			String garbitutaPath = ppt.garbituTXTdataPath(dataPath, gabitzeString);
			
			for (int j=0; j<string2vectorOptions.length;j++) {
				System.out.println("\n--------------" + i + ". ITERAZIOA --------------");
				
				// Textu gordina izanik DataTrain lortu
				dataTrain = ppt.text2raw(garbitutaPath);
				// DataTrain izanik, StringToWordVector filtroa aplikatu
				System.out.println("Num atributes: " + dataTrain.numAttributes());

				dataTrain = ppt.stringToVector(dataTrain, string2vectorOptions[j]);
				System.out.println("Num atributes: " + dataTrain.numAttributes());

				LagMethods.saver(LagMethods.relative2absolute("src/outputFiles/"+i+".arff"), dataTrain);

				// Aurreko filtroarekin lortutako atributu baliagarriak kentzeko nahirekin AttributeSelection aplikatuko dugu.
				dataTrain = ppt.fss(dataTrain); //Hemendik klase atributuarekin amaieran atera
				
				ArrayList<Double> arrLag = BaselineModel.getBaselineMeasures(dataTrain);
				Double fMes = arrLag.get(0);
				Double uncPct = arrLag.get(1);

				System.out.println("\n--------------" + i + ". EMAITZAK --------------");
				System.out.println("\nNum atributes: " + dataTrain.numAttributes());
				System.out.println("Num instances: " + dataTrain.numInstances());
				System.out.println("f-Measure: " + fMes);
				System.out.println("Unclassified percentage: " + uncPct);
				System.out.println("Garbitu-path: " + garbitutaPath);

				if(fMesOptimoa < fMes && uncPct < 50.0) {
					fMesOptimoa = fMes;
					dataSortaOpt = dataTrain;
					hiztOpt = new File(LagMethods.relative2absolute("src/outputFiles/Dictionary.txt"));
					garbitzeStringOpt = gabitzeString;
					string2vectorOptionsOpt = string2vectorOptions[j];
					
				}
				i++;
			}
		}
		
		LagMethods.saver(LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.train.arff"), dataSortaOpt);
		// AttributeSelection eta gero StringToWordVector aplikatu eta gero bueltatu diguten hiztegia moldatuko dugu soilik oraingo atributuak (hitzak) egon daitezen.
		ppt.hiztegiaEguneratu(dataSortaOpt, hiztOpt, LagMethods.relative2absolute("src/outputFiles/DictionaryFSS.txt")); // --> Metodo hau soilik hiztegia eguneratzeko 
		
		System.out.println("\n-------------- OPTIMOA --------------");
		System.out.print("Garbiketa: " + garbitzeStringOpt + "   |  S2V aukerak: " + Arrays.toString(string2vectorOptionsOpt));
		System.out.println("\nNum atributes: " + dataSortaOpt.numAttributes());
		System.out.println("Num instances: " + dataSortaOpt.numInstances());
		System.out.println("f-Measure: " + fMesOptimoa);
		
		return dataSortaOpt;
	}

	
	/**
	 * Hasierako textu fitxategia eta karaktere zehatz batzuk jasota fitxategiak zituen karaktere horiek ezabatu ondoren fitxategi garbia gorde eta horren 'path'-a bueltatuko da.
	 * @param path Hasietako textu fitxategia egongo den 'path'-a.
	 * @param ezabatzekoak String bat, karaktereen zerrenda bezala, ezabatu nahi diren karaktereak izango ditu.
	 * @return Garbitutako fitxategiaren 'path'-a.
	 */
	private String garbituTXTdataPath(String path, String ezabatzekoak) throws Exception {
	    char[] chList = ezabatzekoak.toCharArray();
	    
		String output = path.split(".txt")[0] + "_garbi.txt";
		//StringBuilder in Java is a class used to create a mutable, or in other words, a modifiable succession of characters.
		StringBuilder report = new StringBuilder();
		
		//Java BufferedReader is a public Java class that reads text, using buffering to enable large reads 
    	//at a time for efficiency, storing what is not needed immediately in memory for later use.
	    BufferedReader br = new BufferedReader(new FileReader(path));
	    //Uneko ilara
	    String sCurrentLine;
	    //Uneko ilara null ez den bitartean...
	    while ((sCurrentLine = br.readLine()) != null) {
	    	for (char c : chList) {
		    	ArrayList<Integer> list = new ArrayList<>();
	    		int index = sCurrentLine.indexOf(c);
		    	while (index >= 0) {
		    		list.add(index);
		    		index = sCurrentLine.indexOf(c, index + 1);
		    	}

		    	//Behin "-ren posizioa jakinda ' ' bat jarriko diogu zegoenaren ordez
		    	for (Integer integer : list) {
		    		sCurrentLine = sCurrentLine.substring(0, integer) + " " + sCurrentLine.substring(integer+1);
				}
			}
	    	report.append(sCurrentLine + "\n");
	    }
	    
	    //Gorde sortutakoa fitzategi batean.
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
		writer.write(report.toString());
		writer.close();
		br.close();
		
		return output;
	}

	
	/**
	 * Textu fitxategi garbia jasota arff formatura pasatu gorde eta Java-ko Instances motako datu sorta moduan bueltatuko du.
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
	 * StringToWordVector filtroaren aukeren zerrendak sortu eta aukera moten zerrenda buletatuko du.
	 * @return StringToWordVector filtroaren aukera moten zerrenda.
	 */
	public String[][] getOptionsStringToVector() {
		
		
		String[] optionsBOW = new String[11];
		
		//-R <index1,index2-index4,...>
		  //Specify list of string attributes to convert to words (as weka Range).
		optionsBOW[0] = "-R";
		optionsBOW[1] = "first-last";
        
        //-W <number of words to keep>
	        //Specify approximate number of word fields to create.
	        //Surplus words will be discarded..
	        //(default: 1000)
		optionsBOW[2] = "-W";
		optionsBOW[3] = "2000";
        
//	    -prune-rate <rate as a percentage of dataset>
//	     Specify the rate (e.g., every 10% of the input dataset) at which to periodically prune the dictionary.
//	      	-W prunes after creating a full dictionary. You may not have enough memory for this approach.
//	      	(default: no periodic pruning)
		optionsBOW[4] = "-prune-rate";
		optionsBOW[5] = "-1.0";
        
//	      -N
//	      	Whether to 0=not normalize/1=normalize all data/2=normalize test data only
//	          to average length of training documents (default 0=don't normalize)
		optionsBOW[6] = "-N";
		optionsBOW[7] = "0";
        
//	      -L
//	      	Convert all tokens to lowercase before adding to the dictionary.
//	        	Convierta todos los tokens a minúsculas antes de agregarlos al diccionario.
//	      Hiztegian sartu baino lehen, izki guztiak letra xehean jarri.
		optionsBOW[8] = "-L";
        
//	      -dictionary <path to save to>
//	      	The file to save the dictionary to.
//	      	(default is not to save the dictionary)
		optionsBOW[9] = "-dictionary";
		optionsBOW[10] = LagMethods.relative2absolute("src/outputFiles/Dictionary.txt");
			
		String[] optionsTFIDF = new String[13];
		
		optionsTFIDF[0] = "-R";
        optionsTFIDF[1] = "first-last";
        optionsTFIDF[2] = "-W";
        optionsTFIDF[3] = "2000";
        optionsTFIDF[4] = "-prune-rate";
        optionsTFIDF[5] = "-1.0";
        
        //TF
        //Sets whether if the word frequencies in a document should be transformed into:
        //fij*log(num of Docs/num of Docs with word i)
        //where fij is the frequency of word i in document(instance) j.
        optionsTFIDF[6] = "-T";
        
        //TFIDF
	    //Sets whether if the word frequencies should be transformed into log(1+fij) where fij is the
	    //frequency of word i in document(instance) j.
        optionsTFIDF[7] = "-I";
        
        optionsTFIDF[8] = "-N";
        optionsTFIDF[9] = "0";
        optionsTFIDF[10] = "-L";
        optionsTFIDF[11] = "-dictionary";
        optionsTFIDF[12] = LagMethods.relative2absolute("src/outputFiles/Dictionary.txt");
        
		System.out.println("TF-IDF: " + Arrays.toString(optionsTFIDF));
		System.out.println("BOW: " + Arrays.toString(optionsBOW));
		
		String[][] emaitza = {optionsBOW, optionsTFIDF};
		
		System.out.print("EMAITZA: ");
		for (String[] row : emaitza) System.out.print(Arrays.toString(row));
		System.out.println(" ");
		
		return emaitza;
	}
	
	
	/**
	 * Lehenengo arff fitxategia eta filtroaren aukerak jasota datu sorta horri atibutuekiko ez-gainbegiratutako 'String to Word Vector' textu mehatzaritza filtroa aplikatuko zaio eta moldatutako datu sorta bueltatuko da.
	 * @param Data 'raw'-tik 'arff'-ra pasatutako datu sorta, Intances motako objektua.
	 * @param Options StringToWordVector filtroaren aukerak.
	 * @return Filtroa aplikatu ondorengo datu sorta.
	 */
	public Instances stringToVector(Instances data, String[] options) throws Exception {
		
		// Converts string attributes into a set of numeric attributes representing word occurrence information from the text contained in the strings.
		StringToWordVector str2vector = new StringToWordVector();
        //Pasatutako aukera zerrenda, filtroaren aukera bezala esleitu.
        str2vector.setOptions(options);
		//Sets the format of the input instances.
        str2vector.setInputFormat(data);
        //Erabili filtroa.
        Instances dataTrainBoW = Filter.useFilter(data,str2vector);
        //0 posizioko atributua klase bezala esleitu.
        dataTrainBoW.setClassIndex(0);
		
        //LagMethods.saver(LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.train.arff"), dataTrainBoW);
		
		return dataTrainBoW;
	}
	
	/**
	 * StringToWordVector aplikatu ondorengo datu sorta jasota, atributuekiko gainbegiratutako AttributeSelection filtroa aplikatuko zaio datu sortari eta aplikatu ondorengo datu sorta bueltatuko da.
	 * @param data StringToWordVector aplikatu ondorengo datu sorta.
	 * @return Filtroa aplikatu ondorengo datu sorta.
	 */
	public Instances fss(Instances data) throws Exception {
		
		// A supervised attribute filter that can be used to select attributes. It is very flexible and allows various search and evaluation methods to be combined.
		AttributeSelection at = new AttributeSelection();
		
		//Evaluates the worth of an attribute by measuring the information gain with respect to the class.
			//InfoGain(Class,Attribute) = H(Class) - H(Class | Attribute).
        InfoGainAttributeEval infoG = new InfoGainAttributeEval();
        
        //Ranks attributes by their individual evaluations. Use in conjunction with attribute evaluators
        Ranker rank = new Ranker();
	        //threshold -- Set threshold by which attributes can be discarded. Default value results in no attributes being discarded. Use either this option or numToSelect to reduce the attribute set.
	        //umbral: establece el umbral por el cual se pueden descartar los atributos. El valor predeterminado da como resultado que no se descarten atributos. Utilice esta opciÃ³n o numToSelect para reducir el conjunto de atributos.
        	rank.setOptions(new String[] { "-T", "0.00000001" });
        
    	//Sets the format of the input instances.
        at.setInputFormat(data);
        // set the attribute/subset evaluator
        at.setEvaluator(infoG);
        // set the search method
        at.setSearch(rank);
		//Erabili filtroa.
        Instances selectedData = Filter.useFilter(data, at);
        
        //Data sortaren azken posizioko atributua klase bezala esleitu.
        selectedData.setClassIndex(selectedData.numAttributes()-1);
		//Gorde jarritako fitxategian.
        //LagMethods.saver(LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.train_FSS.arff"), selectedData);
        
        return (selectedData);
	}
	
	/**
	 * @param data
	 * @param inputHizt
	 * @param outputHizt
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public void hiztegiaEguneratu(Instances data, File inputHizt, String outputHizt) throws Exception, FileNotFoundException {
    	
		//StringBuilder in Java is a class used to create a mutable, or in other words, a modifiable succession of characters.
		StringBuilder report = new StringBuilder();
		
    	//Hiztegiko goiburua sartu.
		report.append("@@@numDocs="+data.numInstances()+"@@@\n");
		
		// Hiztegiko hitzak eta hauen maiztasuna gordetzeko HashMap-a
		HashMap<String, Integer> hizt = new HashMap<String, Integer>();

		// HashMap-a data-sortaren atributuekin (hitzekin) hasieratu
        for(int i=0;i<data.numAttributes()-1;i++) {
            Attribute atr = data.attribute(i);
            hizt.put(atr.name(),1);
        }

        //Java BufferedReader is a public Java class that reads text, using buffering to enable large reads 
    	//at a time for efficiency, storing what is not needed immediately in memory for later use.
	    BufferedReader br = new BufferedReader(new FileReader(inputHizt));
	    //Uneko ilara
	    String sCurrentLine = br.readLine();
	    //Lehenengoa ilara baztertuko dugu, ez dugulako nahi: @@@numDocs="+data.numInstances()+"@@@ formatua duena da.
        sCurrentLine = br.readLine();
	    //Uneko ilara null ez den bitartean...
		while (sCurrentLine != null) {
			//Erabiliko ditugun parametroak hasieratu eta izendatu.
			String atr = "";
            Integer freq;
            
            //(Hurrengoa textuak ',' baldin badauka arazorik ez emateko da.)
	    	//Uneko ilara ',' batengatik banatzean bi zati baldin badaude...
			if(sCurrentLine.split(",").length==2) {
		    	//Lehenengo zatia atributua (hitza) izango da.
	            atr = sCurrentLine.split(",")[0];
		    	//Bigarren zatia maiztasuna izango da.
	            freq = Integer.parseInt(sCurrentLine.split(",")[1]);
			}
			else {
				int luzera = sCurrentLine.split(",").length;
				atr = atr + sCurrentLine.split(",")[0];
				for(int i=1;i<luzera-2;i++) {
			    	//Azkena kenduta zati guztien konkatenazioa atributua (hitza) izango da.
					atr = atr + "," + sCurrentLine.split(",")[i];
				}
		    	//Azken zatia maiztasuna izango da.
	            freq = Integer.parseInt(sCurrentLine.split(",")[luzera-1]);
			}
			//Data sortaren atributua eta hiztegi zaharraren hitza bat badatoz
            if(hizt.containsKey(atr)){
            	//Maiztasuna eguneratu
            	hizt.put(atr,freq);;
            }
            //Hurrengo ilara hartu
            sCurrentLine = br.readLine();
        }
		
		// HashMap-an dagoen maiztasunarekin, data-sortaren atributuekin (hitzekin) hiztegia eraiki
		for(int i=0; i<data.numAttributes()-1;i++){
            String atr = data.attribute(i).name();
            if(hizt.containsKey(atr)){
            	report.append(atr+","+hizt.get(atr)+"\n");
            }
        }
	    
	    //Gorde sortutakoa fitzategi batean.
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputHizt)));
		writer.write(report.toString());
		writer.close();
		br.close();
	}
}
	
	
	

