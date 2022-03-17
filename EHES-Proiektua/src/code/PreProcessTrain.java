package code;


import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

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
	
	public Instances bow(Instances data) {
		return null;
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
	
}
