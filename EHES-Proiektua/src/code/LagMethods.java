package code;

import java.io.PrintStream;
import java.nio.file.FileSystems;

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
        data.setClassIndex(0);
        return data;
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
    
    
}
