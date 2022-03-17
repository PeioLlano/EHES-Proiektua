package code;

import java.io.FileOutputStream;
import java.io.PrintStream;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;

public class IragarpenSortzailea {

	public static void eginIragarpenak(Instances dataTest, NaiveBayes nb) throws Exception {
		
		//ECLIPSE-tik FILE-era pasatzeaz arduratuko den 'stremer'-a.
		FileOutputStream fos = new FileOutputStream(" -------------Non doan jarri behar da-------------");
		PrintStream ps = new PrintStream(fos);

		//Iragarpenak egiteaz arduratuko den ebaluatzailea.
		Evaluation ev = new Evaluation(dataTest);
		
		ps.println("=======================PREDICTIONS=============================");
		ps.println(" K  REAL PREDICTED (* -> erroreak)");
		
		//Uneko intantzea zenbatzeaz arduratuko den parametroa.
        int k = 0;
        //Erroreak zenbatzeaz arduratuko den parametroa.
        int error = 0;
        
        //Instantzia bakoitzeko...
		for (Instance instance : dataTest) {
			
			//Klasearen balioa erreala lortu eta gure sailkatzeileak iragarri duen klasearen balioa lortu.
            double actual = instance.classValue();
            double prediction = ev.evaluateModelOnce(nb, instance);
            
            //Iragarpena eta erreala fitxategian gorde eta 'error' parametroa eguneratu
            ps.printf("%2d.%4.0f%4.0f", ++k , actual, prediction);
            ps.println(prediction != actual? " *": "");
            if(prediction != actual) error++;
        }
		
		//Iragarpenen datuak fitxategian gorde.
		ps.println("EGINDAKO PREDIZIO KOP= " + dataTest.numInstances());
		ps.println("PREDIZIO ERRATU KOP= " + error);
		
		ps.close();
	}
}
