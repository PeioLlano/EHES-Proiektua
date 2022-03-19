package code;


import java.io.File;
import java.io.IOException;
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
		StringToWordVector stwv = new StringToWordVector();
		//wordsToKeep -- The number of words (per class if there is a class attribute assigned) to attempt to keep.
		stwv.setWordsToKeep(1000);
		//outputWordCounts -- Output word counts rather than boolean 0 or 1(indicating presence or absence of a word)
		stwv.setOutputWordCounts(true);
		//lowerCaseTokens -- If set then all the word tokens are converted to lower case before being added to the dictionary.
		stwv.setLowerCaseTokens(true);
		//Datu sortaren formatua esleitu
		stwv.setInputFormat(data);
		//Set the dictionary file to save the dictionary to.
		stwv.setDictionaryFileToSaveTo(new File(LagMethods.relative2absolute("/src/files/dictionary.txt")));
		//Filtroa erabili datarekin
		Instances dataBoW = Filter.useFilter(data, stwv);
		
		return dataBoW;
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
	
	
	

