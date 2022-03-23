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

public class LagMethods {
	

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

	public static String relative2absolute(String relativePath) {
		return FileSystems.getDefault().getPath(relativePath).normalize().toAbsolutePath().toString();
	}
	
    public static Instances path2instances(String path) throws Exception {
        DataSource ds = new DataSource(path);
        Instances data = ds.getDataSet();
        return data;
    }
    
    public static void saver(String path, Object gordetzeke) throws Exception {
    	FileOutputStream os = new FileOutputStream(path);
		PrintStream ps = new PrintStream(os);
		ps.print(gordetzeke);
		ps.close();
    }
    
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
    
    public static void printResults(PrintStream ps, Evaluation ev, String name) throws Exception {
		ps.println("\n#########################  " + name + "  #########################");
		ps.println("\n"+ev.toClassDetailsString());
		ps.println(ev.toSummaryString("\n" + name + " SUMMARY", false));
		ps.println(ev.toMatrixString("\n" + name + " CONFUSSION MATIX"));
	}
    
    public static Instances txt2Intances(String pathInput, String pathOutput) throws Exception {
		//String name = path.split("\\")[path.split("\\").length-1];
		StringBuilder report = new StringBuilder();
	    report.append("@relation SMS_SpamCollection\n");
    	report.append("\n");
	    report.append("@attribute klasea {spam, ham}\n");
	    report.append("@attribute testua string\n");
    	report.append("\n");
	    
	    BufferedReader br = new BufferedReader(new FileReader(pathInput));
	     String sCurrentLine;
	     
		report.append("@data\n");
	    while ((sCurrentLine = br.readLine()) != null) {
	    	String klasea = sCurrentLine.split("\t")[0];
	    	String textua = sCurrentLine.split("\t")[1];
	    	ArrayList<Integer> list = new ArrayList<>();
	    	int index = textua.indexOf("\"");
	    	while (index >= 0) {
	    		list.add(index);
	    		index = textua.indexOf("\"", index + 1);
	    	}

	    	Integer i = 0; 
	    	for (Integer integer : list) {
	    		textua = textua.substring(0, integer+i) + "\\" + textua.substring(integer+i);
	    		i++;
			}
		    report.append(klasea + ", \"" + textua + "\"\n");
	    }
	    
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathOutput)));
		writer.write(report.toString());
		writer.close();
	    br.close();
	    
	    return path2instances(pathOutput);
	}
    
}
