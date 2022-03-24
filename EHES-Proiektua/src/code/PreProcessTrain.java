package code;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class PreProcessTrain {
	//hola
	public static Instances preProcess(String dataPath) throws Exception {
		PreProcessTrain ppt = new PreProcessTrain();
		
		// Textu gordina izanik DataTrain lortu
		Instances dataTrain = ppt.raw2arff(dataPath);
		// DataTrain izanik, StringToWordVector filtroa aplikatu
		dataTrain = ppt.bow(dataTrain);
		// Aurreko filtroarekin lortutako atributu baliagarriak kentzeko nahirekin AttributeSelection aplikatuko dugu.
		dataTrain = ppt.fss(dataTrain); //Hemendik klase atributuarekin amaieran atera
		// AttributeSelection eta gero StringToWordVector aplikatu eta gero bueltatu diguten hiztegia moldatuko dugu soilik oraingo atributuak (hitzak) egon daitezen.
		ppt.hiztegiaEguneratu(dataTrain, LagMethods.relative2absolute("src/files/Dictionary.txt"), LagMethods.relative2absolute("src/files/DictionaryFSS.txt")); // --> Metodo hau soilik hiztegia eguneratzeko 
		return dataTrain;
	}

	public Instances raw2arff(String dataPath) throws Exception {
		String output = LagMethods.relative2absolute("src/files/SMS_SpamCollection.train_raw.arff");
		Instances data = LagMethods.txt2Intances(dataPath,output);
		
		return data;
	}
	
	public Instances bow(Instances data) throws Exception {
		
		// Converts string attributes into a set of numeric attributes representing word occurrence information from the text contained in the strings.
		StringToWordVector str2vector = new StringToWordVector();
		
        //Aukerak izango dituen zerrenda hasieratu.
		String[] options = new String[11];
		
		//Ia guztia defaut balioak dira baina ziurtatzeko da bide batean.
		
		//-R <index1,index2-index4,...>
		  //Specify list of string attributes to convert to words (as weka Range).
        options[0] = "-R";
        options[1] = "first-last";
        
        //-W <number of words to keep>
	        //Specify approximate number of word fields to create.
	        //Surplus words will be discarded..
	        //(default: 1000)
        options[2] = "-W";
        options[3] = "2000";
        
//      -prune-rate <rate as a percentage of dataset>
//      	Specify the rate (e.g., every 10% of the input dataset) at which to periodically prune the dictionary.
//      	-W prunes after creating a full dictionary. You may not have enough memory for this approach.
//      	(default: no periodic pruning)
        options[4] = "-prune-rate";
        options[5] = "-1.0";
        
//      -N
//      	Whether to 0=not normalize/1=normalize all data/2=normalize test data only
//          to average length of training documents (default 0=don't normalize)
        options[6] = "-N";
        options[7] = "0";
        
//      -L
//      	Convert all tokens to lowercase before adding to the dictionary.
//        	Convierta todos los tokens a minúsculas antes de agregarlos al diccionario.
//      Hiztegian sartu baino lehen, izki guztiak letra xehean jarri.
        options[8] = "-L";
        
//      -dictionary <path to save to>
//      	The file to save the dictionary to.
//      	(default is not to save the dictionary)
        options[9] = "-dictionary";
        options[10] = LagMethods.relative2absolute("src/files/Dictionary.txt");
        
        //Pasatutako aukera zerrenda, filtroaren aukera bezala esleitu.
        str2vector.setOptions(options);
		//Sets the format of the input instances.
        str2vector.setInputFormat(data);
		//Erabili filtroa.
        Instances dataTrainBoW = Filter.useFilter(data,str2vector);
        //0 posizioko atributua klase bezala esleitu.
        dataTrainBoW.setClassIndex(0);
		
        LagMethods.saver(LagMethods.relative2absolute("src/files/SMS_SpamCollection.train.arff"), dataTrainBoW);
		
		return dataTrainBoW;
	}
	
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
        LagMethods.saver(LagMethods.relative2absolute("src/files/SMS_SpamCollection.train_FSS.arff"), selectedData);
        
        return (selectedData);
	}
	
	public void hiztegiaEguneratu(Instances data, String inputHizt, String outputHizt) throws Exception, FileNotFoundException {
    	
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
	
	
	

