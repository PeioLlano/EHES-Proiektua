package code;

import java.io.FileOutputStream;
import java.io.PrintStream;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;

public class IragarpenSortzailea {

	public static void eginIragarpenak(Instances dataTest, MultilayerPerceptron mp, String pathToSave) throws Exception {
		
		//ECLIPSE-tik FILE-era pasatzeaz arduratuko den 'stremer'-a.
		FileOutputStream fos = new FileOutputStream(pathToSave);
		PrintStream ps = new PrintStream(fos);

		//Iragarpenak egiteaz arduratuko den ebaluatzailea.
		Evaluation ev = new Evaluation(dataTest);
		
		ps.println("-----------------------------------------------------------------");
		ps.println("-                           PREDICTIONS                         -");
		ps.println("-----------------------------------------------------------------");
		ps.println("|  INSTANTZIA ZENB   |   REAL    |   PREDICTED   |   ERROR (*)   |");
		
		//Uneko intantzea zenbatzeaz arduratuko den parametroa.
        int k = 0;
        //Erroreak zenbatzeaz arduratuko den parametroa.
        int error = 0;
        
        //Instantzia bakoitzeko...
		for (Instance instance : dataTest) {
			
			//Klasearen balioa erreala lortu eta gure sailkatzeileak iragarri duen klasearen balioa lortu.
            double actual = instance.classValue();
            double prediction = ev.evaluateModelOnce(mp, instance);
            
            //Iragarpena eta erreala fitxategian gorde eta 'error' parametroa eguneratu
            ps.print("|          " + ++k +"          |     "+  (int)actual + "     |       " +  (int)prediction + "       |       " + (prediction != actual? "*        |": "         |"));
            ps.println(prediction != actual? " *": "");
            if(prediction != actual) error++;
        }
		
		//Iragarpenen datuak fitxategian gorde.
		ps.println("EGINDAKO PREDIZIO KOP= " + dataTest.numInstances());
		ps.println("PREDIZIO ERRATU KOP= " + error);
		ps.println("ERRORE PORTZENTAIA= " + error*100.0/dataTest.numInstances());

		
		ps.close();
	}
}
