package code;

import java.io.FileOutputStream;
import java.io.PrintStream;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Klase honetan lortutako sailkatzailearekin test moduan jasotzen diren instantzien klaseen iragarpenak egingo dira.
 * <p>
 * Date: Apr 06-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio
 *
 */
public class IragarpenSortzailea {

	/**
	 * Metodo honen bidez jasotzen sailkatzailearekin jasotako datu-sortaren  instantzien klaseen iragarpenak egingo dira eta hirugarren atributuaren path-ean gordeko dira.
	 * 
	 * @param dataTest Iragarriko den datu-sorta.
	 * @param model Iragarle bezala erabiliko den sailkatzailea.
	 * @param pathToSave Iragarpenak gordetzeko helbidea.
	 */
	public static void eginIragarpenak(Instances dataTest, Classifier model, String pathToSave) throws Exception {
		
		//ECLIPSE-tik FILE-era pasatzeaz arduratuko den 'stremer'-a.
		FileOutputStream fos = new FileOutputStream(pathToSave);
		PrintStream ps = new PrintStream(fos);

		//Iragarpenak egiteaz arduratuko den ebaluatzailea.
		Evaluation ev = new Evaluation(dataTest);
		
		ps.println("-----------------------------------------------------------------------");
		ps.println("-                              PREDICTIONS                            -");
		ps.println("-----------------------------------------------------------------------");
		ps.println("|   INSTANTZIA ZENB    |    REAL     |   PREDICTED   |    ERROR (*)   |");
		ps.println("-----------------------------------------------------------------------");
				
		//Uneko intantzea zenbatzeaz arduratuko den parametroa.
        int k = 1;
        //Erroreak zenbatzeaz arduratuko den parametroa.
        //int error = 0;
        
        //Instantzia bakoitzeko...
		for (Instance instance : dataTest) {
			
			//Klasearen balioa erreala lortu eta gure sailkatzeileak iragarri duen klasearen balioa lortu.
            double actual = instance.classValue();
            double prediction = ev.evaluateModelOnce(model, instance);
            
            //Iragarpena eta erreala fitxategian gorde eta 'error' parametroa eguneratu
            ps.println("|"+ hustuneaLortu(k) + "       " + k++ +"          |     "+  actual + "     |       " +  (int)prediction + "       |       " + (prediction != actual? "*        |": "         |"));
            //if(prediction != actual) error++;
        }
		ps.println("-----------------------------------------------------------------------");

		//Iragarpenen datuak fitxategian gorde.
		ps.println("EGINDAKO PREDIZIO KOP= " + dataTest.numInstances());
		//ps.println("PREDIZIO ERRATU KOP= " + error);
		//ps.println("ERRORE PORTZENTAIA= " + error*100.0/dataTest.numInstances());

		
		ps.close();
	}
	
	private static String hustuneaLortu(int i) {
		String emaitza = "";
		if(i==10 || i==100 || i==1000) i=i+1;
		int digito = (int) Math.floor(Math.log10(Math.abs(i)) + 1);
		int hutsune = 4-digito;
		for(int j=0; j<=hutsune ;j++) emaitza = emaitza + " ";
		return emaitza;
	}
}
