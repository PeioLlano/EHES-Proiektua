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
		
		Instances dataTrain = ppt.raw2arff(dataPath);
		dataTrain = ppt.bow(dataTrain);
		dataTrain = ppt.fss(dataTrain); //Hemendik atributuarekin amaieran atera
		ppt.hiztegiaEguneratu(dataTrain, LagMethods.relative2absolute("src/files/Dictionary.txt"), LagMethods.relative2absolute("src/files/DictionaryFSS.txt")); // --> Metodo hau soilik hiztegia eguneratzeko 
		return dataTrain;
	}

	public Instances raw2arff(String dataTest) throws Exception {
		String input = LagMethods.relative2absolute("src/files/SMS_SpamCollection.train.txt");
		String output = LagMethods.relative2absolute("src/files/SMS_SpamCollection.train_raw.arff");
		Instances data = LagMethods.txt2Intances(input,output);
		
		return data;
	}
	
	public Instances bow(Instances data) throws Exception {
		
		//################################################################ TRAIN BOW ################################################################
		
		StringToWordVector str2vector = new StringToWordVector();
		
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
        
        str2vector.setOptions(options);
        str2vector.setInputFormat(data);
        Instances dataTrainBoW = Filter.useFilter(data,str2vector);
        dataTrainBoW.setClassIndex(0);
		
        LagMethods.saver(LagMethods.relative2absolute("src/files/SMS_SpamCollection.train.arff"), dataTrainBoW);
//      LagMethods.saver("path", dataTrainBoW);
//      FileOutputStream os = new FileOutputStream(args[2]);
//		PrintStream ps = new PrintStream(os);
//      ps.print(dataTrainBoW);
//		ps.close();
		
		//################################################################ TRAIN BOW ################################################################
		
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
        
        at.setInputFormat(data);
        at.setEvaluator(infoG);
        at.setSearch(rank);
        Instances selectedData = Filter.useFilter(data, at);
        selectedData.setClassIndex(selectedData.numAttributes()-1);
        LagMethods.saver(LagMethods.relative2absolute("src/files/SMS_SpamCollection.train_FSS.arff"), selectedData);
        
        return (selectedData);
	}
	
	public void hiztegiaEguneratu(Instances data, String inputHizt, String outputHizt) throws Exception, FileNotFoundException {
		
		StringBuilder report = new StringBuilder();
		
		report.append("@@@numDocs="+data.numInstances()+"@@@\n");
		HashMap<String, Integer> hizt = new HashMap<String, Integer>();

        for(int i=0;i<data.numAttributes()-1;i++) {
            Attribute atr = data.attribute(i);
            hizt.put(atr.name(),1);
        }

        BufferedReader br = new BufferedReader(new FileReader(inputHizt));
        String sCurrentLine = br.readLine();
        sCurrentLine = br.readLine();
		while (sCurrentLine != null) {
			String atr = "";
            Integer freq;
			if(sCurrentLine.split(",").length==2) {
	            atr = sCurrentLine.split(",")[0];
	            freq = Integer.parseInt(sCurrentLine.split(",")[1]);
			}
			else {
				int luzera = sCurrentLine.split(",").length;
				for(int i=0;i<luzera-2;i++) {
					atr = atr + sCurrentLine.split(",")[i];
				}
	            freq = Integer.parseInt(sCurrentLine.split(",")[luzera-1]);
			}
            if(hizt.containsKey(atr)){
            	report.append(atr+","+freq+"\n");
            }
            sCurrentLine = br.readLine();
        }
	    
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputHizt)));
		writer.write(report.toString());
		writer.close();
		br.close();
	}
}
	
	
	

