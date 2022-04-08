package code;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Klase honek interfazea egikarituko du.
 * <p>
 * Date: Apr 07-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio
 *
 */
public class SmsIragarriApp {

	/**
	 * Metodo honek interfazea egikarituko du.
	 * 
	 * @param text Sailkatuko den mezua.
	 * @param cl Mezua sailkatuko duen sailkatzailea.
	 * @return Iragarpena.
	 */
	public static String smsIragarri(String text, Classifier cl) throws Exception {
		String emaitza = "Errorea, ezin izan da iragarri";
		
		Instances preparedData = string2Intances(text);
		preparedData = PreProcessTest.fixedDictionaryStringToWordVector(preparedData);
		preparedData = PreProcessTest.reorderClass(preparedData);
		
        //Lehenengo instantzia da gurea
		Instance instance = preparedData.instance(0);

		//Gure sailkatzeileak iragarri duen klasearen balioa lortu.
		Evaluation ev = new Evaluation(preparedData);
        int prediction = (int)ev.evaluateModelOnce(cl, instance);

        //Iragarpena indizetik klse nominalaren baliora pasatu
        emaitza = preparedData.attribute(preparedData.numAttributes()-1).value(prediction);
		System.out.println("Emaitza hurrengoa da: " + emaitza);
		return emaitza;
	}
	
	private static Instances string2Intances(String text) throws Exception {
		String output = LagMethods.relative2absolute("src/outputFiles/smsIragarlea.arff");
		
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
	    
	    //Data sorta hasten dela definitu
		report.append("@data\n");

    	String klasea = "?";
    	//Ilara osoa textua izango da.
    	String textua = text;
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
	    
	    //Gorde sortutakoa fitzategi batean.
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
		writer.write(report.toString());
		writer.close();
		
	    Instances emaitza = LagMethods.path2instances(output);
	    
	    return emaitza;
	}
	
}
