package code;

import weka.core.Instances;

public class PreProcessTrain {
	
	public static Instances preProcess(String dataPath) {
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
	
	public Instances fss(Instances data) {
		return null;
	}
	
}
