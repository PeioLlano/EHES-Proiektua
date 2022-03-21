package code;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class PreProcessTrain {
	//hola
	public static Instances preProcess(String dataPath) throws Exception {
		PreProcessTrain ppt = new PreProcessTrain();
		
		Instances dataTrain = ppt.raw2arff(dataPath);
		dataTrain = ppt.bow(dataTrain);
		dataTrain = ppt.fss(dataTrain);
		
		return dataTrain;
	}

	public Instances raw2arff(String dataPath) {
		return null;
	}
	
	public Instances bow(Instances data) throws Exception {
		
		//################################################################ TRAIN BOW ################################################################
		
		StringToWordVector str2vector = new StringToWordVector();
		
		String[] options = new String[11];
        options[0] = "-R";
        options[1] = "first-last";
        options[2] = "-W";
        options[3] = "2000";
        options[4] = "-prune-rate";
        options[5] = "-1.0";
        options[6] = "-N";
        options[7] = "0";
        options[8] = "-L";
        options[9] = "-dictionary";
        options[10] = LagMethods.relative2absolute("src/files/Dictionary.txt");
        
        str2vector.setOptions(options);
        str2vector.setInputFormat(data);
        Instances dataTrainBoW = Filter.useFilter(data,str2vector);
        dataTrainBoW.setClassIndex(0);
		
//      FileOutputStream os = new FileOutputStream(args[2]);
//		PrintStream ps = new PrintStream(os);
//      ps.print(dataTrainBoW);
//		ps.close();
		
		//################################################################ TRAIN BOW ################################################################
		
		return dataTrainBoW;
	}
	
	public Instances fss(Instances data) throws Exception {
		AttributeSelection aSelection = new AttributeSelection();
		CfsSubsetEval cfsSubsetEval = new CfsSubsetEval();
		BestFirst bFirst = new BestFirst();
		aSelection.setEvaluator(cfsSubsetEval);
		aSelection.setSearch(bFirst);
		aSelection.setInputFormat(data);
		Instances selectedData = Filter.useFilter(data, aSelection);
		return (selectedData);
		
	}
	
	public void csv2arff(String unekoa, String path, String karpetaBerria) throws IOException {
		System.out.println(path+"/"+unekoa);
		String csvfitx=unekoa;
		// CSV-a kargatu
		CSVLoader loader = new CSVLoader();
		//Loader.setSource(new File(args[0]))
		loader.setSource(new File(path +"/"+csvfitx));
		//Instantzien objektua lortu
		Instances data = loader.getDataSet();
		//ARFF-a gorde
		ArffSaver saver = new ArffSaver();
		//Bihurtu nahi dugun dataset-a lortu
		saver.setInstances(data);
		//ARFF moduan gorde
		saver.setFile(new File(karpetaBerria+"/"+unekoa+".arff"));
		saver.setDestination(new File(karpetaBerria+"/"+unekoa+".arff"));
		saver.writeBatch();
		System.out.println(".arff FITXATEGIA: "+csvfitx);
		}
	}
	
	
	

