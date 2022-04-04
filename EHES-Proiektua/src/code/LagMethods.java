package code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

/**
 * Klase honetan proiektu osoan zehar klase batean baino gehiagotan erabiltzen diren metodoak daude, hauek kasu zehatz batzuentzat erabilgarri izango dira.
 * <p>
 * Date: Mar 30-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio
 *
 */
public class LagMethods {

	/**
	 * Proiektua identifikatuko duen goiburua datu-korronte zehatz batean inprimatzea ahalbidetuko duen metodoa.
	 * @param ps Goiburua inprimatuko den datu-korrontea (fluxua).
	 */
	public static void printHeader(PrintStream ps) {
		ps.println("#############################################################################################################################################");
		ps.println("#                                                                                                                                           #");
		ps.println("#                                                  ERABAKIAK HARTZEKO EUSKARRI SISTEMAK                                                     #");
		ps.println("#                                                                   -                                                                       #");
		ps.println("#                                             SPAM classification with Multilayer Perceptron                                                #");
		ps.println("#                                                                   -                                                                       #");
		ps.println("#                                            EGILEAK: Jon Blanco, Gorka del Rio eta Peio Llano                                              #");
		ps.println("#                                                                                                                                           #");
		ps.println("#############################################################################################################################################");
		ps.println("");
	}

	/**
	 * Helbide errelatiboa izanda helbide absolutoa lotzeko metodoa.
	 * @param relativePath Helbide absolutoa jakin nahi dugun helbide errelatiboa.
	 * @return Sartutako helbide errelatiboaren helbide absolutoa.
	 */
	public static String relative2absolute(String relativePath) {
		return FileSystems.getDefault().getPath(relativePath).normalize().toAbsolutePath().toString();
	}
	
    /**
     * Arff motako fitxategia izanik Java Instances motako datu sorta lortzeko metodoa.
     * @param path Arff motako fitxategiaren helbidea.
     * @return Java Instances motako datu sorta.
     */
    public static Instances path2instances(String path) throws Exception {
        DataSource ds = new DataSource(path);
        Instances data = ds.getDataSet();
        return data;
    }
    
    /**
     * Edozein motako objektuak gordetzeko balio duen metodoa.
     * @param path Objektua non gorde nahi den helbidea.
     * @param gordetzeke Gorden nahi den objektua.
     */
    public static void saver(String path, Object gordetzeke) throws Exception {
    	FileOutputStream os = new FileOutputStream(path);
		PrintStream ps = new PrintStream(os);
		ps.print(gordetzeke);
		ps.close();
    }
    
    /**
     * Datu sorta zehatz baten Hold-Out-en ataletakoren bat (Train edo Test) lortzea ahalbideetuko digun metodoa.
     * @param mode 'test' edo 'train' nahi dugun erabakitzeko parametroa.
     * @param data Hold-Out egin nahi diogun datu sorta.
     * @param percent Hold-Out-en portzentaia ezartzeko parametroa. 
     * @param seed Hold-Out-en random seed ezartzeko parametroa.
     * @return Sartutako parametroaren arabera datu sorta osoaren hautatutako zatia.
     */
    public static Instances holdOut(String mode, Instances data, Double percent, Integer seed) throws Exception {
        Instances dataEdit = null;

		Randomize rd = new Randomize();
		rd.setInputFormat(data);
		rd.setRandomSeed(seed);
		Instances rdata = Filter.useFilter(data, rd);
		
		if(mode.equals("test")) {
			RemovePercentage rptest = new RemovePercentage();
			rptest.setInputFormat(rdata);
			rptest.setPercentage(70);
			dataEdit = Filter.useFilter(rdata, rptest);
		}else if(mode.equals("train")) {
			RemovePercentage rptrain = new RemovePercentage();
			rptrain.setInputFormat(rdata);
			rptrain.setPercentage(70);
			rptrain.setInvertSelection(true);
			dataEdit = Filter.useFilter(rdata, rptrain);
		}

		return dataEdit;
    }
    
    /**
     * Evaluation objektu baten emaitzak datu-korronte zehatz batean inprimatzea ahalbidetuko duen metodoa.
	 * @param ps Goiburua inprimatuko den datu-korrontea (fluxua).
     * @param ev Noren emaitzak inprimatu nahi diren Evaluation objektua.
     * @param name Erabili den sailkatzeilearen izena.
     */
    public static void printResults(PrintStream ps, Evaluation ev, String name) throws Exception {
		//
    	ps.println("\n#########################  " + name + "  #########################");
		ps.println(ev.toSummaryString("\n" + name + " SUMMARY", false));
		ps.println("\n"+ev.toClassDetailsString());
		ps.println(ev.toMatrixString("\n" + name + " CONFUSSION MATIX"));
	}
    

    /**
     * Textu fitxategia jasota arff formatura pasatu gorde eta Java-ko Instances motako datu sorta moduan bueltatuko du.
     * @param pathInput Textu fitxategi garbia egongo den 'path'-a.
     * @param pathOutput Java-ko Instances motako datu sorta gordeko den fitxategiaren helbidea.
     * @return Textuan zegoen fitxategia Instances motako datu sorta moduan.
     */
    public static Instances txt2Intances(String pathInput, String pathOutput) throws Exception {

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
	    
	    //Java BufferedReader is a public Java class that reads text, using buffering to enable large reads 
    	//at a time for efficiency, storing what is not needed immediately in memory for later use.
	    BufferedReader br = new BufferedReader(new FileReader(pathInput));
	    //Uneko ilara
	    String sCurrentLine;
	    //Data sorta hasten dela definitu
		report.append("@data\n");
	    //Uneko ilara null ez den bitartean...
	    while ((sCurrentLine = br.readLine()) != null) {
	    	//Uneko ilara banatu tabulazioa ikusten duen unean.
	    	//Lehenengo zatia klasea izango da.
	    	String klasea = sCurrentLine.split("\t")[0];
	    	//Bigarren zatia textua izango da.
	    	String textua = sCurrentLine.split("\t")[1];
	    	//Textua "-rik dagoen bilatu (arazoak ematen dituzte string-a bukatu dela pentsatzen baitu)
	    	ArrayList<Integer> list = new ArrayList<>();
	    	int index = textua.indexOf("\"");
	    	while (index >= 0) {
	    		list.add(index);
	    		index = textua.indexOf("\"", index + 1);
	    	}
	    	
	    	//Behin "-ren posizioa jakinda \ bat jarriko diogu aurrean textu irakurketan ez erratzeko
	    	Integer i = 0; 
	    	for (Integer integer : list) {
	    		textua = textua.substring(0, integer+i) + "\\" + textua.substring(integer+i);
	    		i++;
			}
	    	
	    	//ARFF formatua jarraituz ilara gorde, hau da: klasea, textua
		    report.append(klasea + ", \"" + textua + "\"\n");
	    }
	    
	    //Gorde sortutakoa fitzategi batean.
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathOutput)));
		writer.write(report.toString());
		writer.close();
	    br.close();
	    
	    return path2instances(pathOutput);
	}
    
}
