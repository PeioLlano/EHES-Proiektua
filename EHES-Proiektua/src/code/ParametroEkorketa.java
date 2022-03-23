package code;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

public class ParametroEkorketa {

	public static MultilayerPerceptron parametroakEkortu(Instances dataTrain) throws Exception {
		//Azalpena:
		//-------------
		//Kasu honetan ekortzeko logika gehien duen parametroa pertzeptroiak izango dituen izkutatutako geruzak izango da.
		//Enuntziatuan bi parametro ekortu behar direla ipintzen du, gure kasuan oso zentzuzkoa izango
		//litzateke izkutatutako geruza bakoitzak izango dituen neurona kopurua ekortzea baina ez dago metodorik
		//Multilayer Perceptron klasean hori ekortzeko (set egiteko). Baina badago hau:
		
		//h - A string with a comma seperated list of numbers. Each number is the number of nodes to be on a hidden layer. 
		
		//Ekorketa hold-outekin klase minoritarioaren f-measure-a ekortzeko egin behar da:
		int minoritarioa = Utils.minIndex(dataTrain.attributeStats(dataTrain.classIndex()).nominalCounts);
		
		//Setter-a egiteko aukerak jarriko ditugu:
		String[] layerAukerak = {"0", "a", "i", "o", "t"};
		//p
		//	zergatik goiko aukera horiek? weka.sourceforge:
		//  Note to have no hidden units, just put a single 0, 
		//	Any more 0's will indicate that the string is badly formed and make it unaccepted. 
		//	Negative numbers, and floats will do the same. 
		//	There are also some wildcards. 
		//	These are 'a' = (number of attributes + number of classes) / 2, 
		//	'i' = number of attributes, 
		//	'o' = number of classes, 
		//	and 't' = number of attributes + number of classes.
		
		//konputazionalki oso kostu handi-handiak, errepasatu beharko dut!!!!
		
		
		
		double fm = 0;
		MultilayerPerceptron mp = new MultilayerPerceptron();
		String layer1 = "";
		String layer2 = "";
		String m = "";
		mp.setBatchSize("32");
		
			for (int i = 0; i < layerAukerak.length; i++) {
				for (int k = 0; k < layerAukerak.length; k++) {
					mp.setHiddenLayers(layerAukerak[i]+" , "+layerAukerak[k]);
					System.out.println("Geruza kopurua iterazio honetan: "+mp.getHiddenLayers());
					
					
					//hold-out %70
					Randomize rd = new Randomize();
					rd.setInputFormat(dataTrain);
					rd.setRandomSeed(i);
					Instances rdata = Filter.useFilter(dataTrain, rd);
					
					RemovePercentage rptest = new RemovePercentage();
					rptest.setInputFormat(rdata);
					rptest.setPercentage(70);
					Instances dataRandomTest = Filter.useFilter(rdata, rptest);
					//Instances dataRandomTest = LagMethods.holdOut("test", dataTrain, 70.0, 1);
					
					RemovePercentage rptrain = new RemovePercentage();
					rptrain.setInputFormat(rdata);
					rptrain.setPercentage(70);
					rptrain.setInvertSelection(true);
					Instances dataRandomTrain = Filter.useFilter(rdata, rptrain);
					//Instances dataRandomTrain = LagMethods.holdOut("train", dataTrain, 70.0, 1);
					
					Evaluation eval = new Evaluation(dataRandomTrain);
					eval.evaluateModel(mp, dataRandomTest);
					
					if (eval.fMeasure(minoritarioa)>fm) {
						fm = eval.fMeasure(minoritarioa);
						layer1 = layerAukerak[i];
						layer2 = layerAukerak[k];
						m = eval.toMatrixString("####### NAHASMEN MATRIZEA ######");
						
					}
					
				}
				
				
			}
			String layerAzalpena = layerTypeToExplanation(layer1);
			String layerAzalpena2 = layerTypeToExplanation(layer2);
			System.out.println("Lehenengo geruzako azalpena: "+layerAzalpena+"\n");
			System.out.println("Lehenengo geruzako azalpena: "+layerAzalpena2+"\n");

			System.out.println("Klase minoritarioaren f-measure optimoa: "+fm+"\n");
			System.out.println(m);
	

			return mp;
		}
		
		
		
		
		
		
		
		
	

	private static String layerTypeToExplanation(String layer) {
		// TODO Auto-generated method stub
		String erantzuna = "None";
		if (layer.equals("0")) {
			erantzuna = "No hidden units.";
		} else if (layer.equals("a")) {
			erantzuna = "(Number of attributes + Number of classes) / 2";
		} else if (layer.equals("i")) {
			erantzuna = "Number of attributes";
		} else if (layer.equals("o")) {
			erantzuna = "Number of classes";
		} else if (layer.equals("t")) {
			erantzuna = "Number of attributes + Number of classes"; 
		} else {
			erantzuna = "Not specified";
		}
			
		return erantzuna;
		
		
	}
}

