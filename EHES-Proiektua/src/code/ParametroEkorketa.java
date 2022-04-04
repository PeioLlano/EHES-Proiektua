package code;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
/**
 * Parametro ekorketa egiten duen eta baita ekorketa horretarako behar diren metodo lagungarriak dituen klasea.
 * <p>
 * Date: Apr 04-2022
 * 
 * @author Peio Llano
 * @author Jon Blanco
 * @author Gorka del Rio
 *
 */

public class ParametroEkorketa {
	/**
	 * Train eta dev datu sortak hartuta, geruza kopuruaren eta geruza bakoitzeko neurona kopuruaren ekorketa egingo du.
	 * @param dataTrain entrenatzeko eta ekorketa egiteko erabiliko den datu sorta.
	 * @param dataDev ebaluazioa egiteko erabiliko den datu sorta.
	 * @return MultilayerPerceptron motako sailkatzailea, parametro optimoak kalkulatuta dituena.
	 */
	public static MultilayerPerceptron parametroakEkortu(Instances dataTrain, Instances dataDev) throws Exception {
		 if(dataTrain.classIndex()==-1) {
			 dataTrain.setClassIndex(dataTrain.numAttributes()-1);
		 }
		 
		 if(dataDev.classIndex()==-1) {
			 dataDev.setClassIndex(dataDev.numAttributes()-1);
		 }
				 
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
			
			
			
			double fm = 0;
			MultilayerPerceptron mp = new MultilayerPerceptron();
			
			String layer1 = "";
			String layer2 = "";
			String m = "";
			mp.setBatchSize("32");
			int it =0;
			for (int i = 0; i < layerAukerak.length; i++) {
				for (int k = 0; k < layerAukerak.length; k++) {
					mp.setHiddenLayers(layerAukerak[i]+" , "+layerAukerak[k]);
					System.out.println("################ "+it+" iterazioa:"+" ################");
					if (mp.getHiddenLayers().length()<2) {
						System.out.println("Nodo kopurua iterazio honetan: \nLehen geruza: "
								+layerTypeToExplanation(mp.getHiddenLayers().split(", ")[0], dataTrain)+"\nBigarren geruza: ez erabilita"
								);
					}else {
						System.out.println("Nodo kopurua iterazio honetan: \nLehen geruza: "
								+layerTypeToExplanation(mp.getHiddenLayers().split(", ")[0], dataTrain)+"\nBigarren geruza: "
								+layerTypeToExplanation(mp.getHiddenLayers().split(", ")[1], dataTrain));
								
						
					}
					System.out.println("\nF-measure iterazio honetan: "+fm);
					
					
					mp.buildClassifier(dataTrain);
					
					Evaluation eval = new Evaluation(dataTrain);
					
					eval.evaluateModel(mp, dataDev);
					
					if (eval.fMeasure(minoritarioa)>fm) {
						fm = eval.fMeasure(minoritarioa);
						layer1 = layerAukerak[i];
						layer2 = layerAukerak[k];
						m = eval.toMatrixString("####### NAHASMEN MATRIZEA ######");
						
					}
					
					it++;
					
				}
				
				
			}
			
			System.out.println("\n!!!!!!!!!!!!!!!!!!! EMAITZAK !!!!!!!!!!!!!!!!!!!\n");
			String layerAzalpena = layerTypeToExplanation(layer1, dataTrain);
			String layerAzalpena2 = layerTypeToExplanation(layer2, dataTrain);
			System.out.println("Lehenengo geruzako nodo kopurua: "+layerAzalpena+"\n");
			System.out.println("BIgarren geruzako nodo kopurua: "+layerAzalpena2+"\n");

			System.out.println("Klase minoritarioaren f-measure optimoa: "+fm+"\n");
			System.out.println(m+"\n");
			System.out.println();
	        
	        
	        System.out.println("INSTANTZIA GEHIAGO IZATEAREN NAHI JARRAITUZ, TRAIN ETA DEV MERGEATZEKO PROZESUA\n");
	        
	        System.out.println("------- Merge-aren aurreko datuak -------"); 
	        System.out.println("\tDataTrain: "); 
	        System.out.println("\t\tAtributu kopurua: " + dataTrain.numAttributes()); 
	        System.out.println("\t\tInstantzia kopurua: " + dataTrain.numInstances() + "\n"); 
			
	        System.out.println("\t\tDataDev: "); 
	        System.out.println("\t\tAtributu kopurua: " + dataDev.numAttributes()); 
	        System.out.println("\t\tInstantzia kopurua: " + dataDev.numInstances()+ "\n"); 
	        
	        Instances dataF = merge(dataTrain, dataDev);
	        
	        System.out.println("------- Merge-aren ondorengo datuak -------"); 
	        System.out.println("\tDataDev: "); 
	        System.out.println("\t\tAtributu kopurua: " + dataF.numAttributes()); 
	        System.out.println("\t\tInstantzia kopurua: " + dataF.numInstances()); 
	        
	        LagMethods.saver(LagMethods.relative2absolute("src/outputFiles/SMS_SpamCollection.dataBuild.txt"), dataF);
	        
	        return mp;
	}
	
	// Internetetik hartutako kodea: https://stackoverflow.com/questions/10771558/how-to-merge-two-sets-of-weka-instances-together
	//Please note that the following conditions should hold (there are not checked in the function):
		//Datasets must have the same attributes structure (number of attributes, type of attributes)
		//Class index has to be the same
		//Nominal values have to exactly correspond
	/**
	 * Bi datu sorta emanda, hauek fusionatuko ditu.
	 * @param data1 batu nahi den lehen datu sorta.
	 * @param data2 batu nahi den bigarren datu sorta.
	 * @return Instances motako datu sorta, sartutako datu sortak fusionatuta dituena.
	 */
	public static Instances merge(Instances data1, Instances data2)
		    throws Exception
		{
		    // Check where are the string attributes
		    int asize = data1.numAttributes();
		    boolean strings_pos[] = new boolean[asize];
		    for(int i=0; i<asize; i++)
		    {
		        Attribute att = data1.attribute(i);
		        strings_pos[i] = ((att.type() == Attribute.STRING) ||
		                          (att.type() == Attribute.NOMINAL));
		    }

		    // Create a new dataset
		    Instances dest = new Instances(data1);
		    dest.setRelationName(data1.relationName() + "+" + data2.relationName());

		    DataSource source = new DataSource(data2);
		    Instances instances = source.getStructure();
		    Instance instance = null;
		    while (source.hasMoreElements(instances)) {
		        instance = source.nextElement(instances);
		        dest.add(instance);

		        // Copy string attributes
		        for(int i=0; i<asize; i++) {
		            if(strings_pos[i]) {
		                dest.instance(dest.numInstances()-1)
		                    .setValue(i,instance.stringValue(i));
		            }
		        }
		    }

		    return dest;
		}
	
	/**
	 * Letra bat sartuta, letra horri dagokion azalpena bueltatuko du.
	 * @param layer azaldu nahi den letra.
	 * @param data erabiltzen ari garen datu sorta.
	 * @return Azalpena, String motatakoa.
	 */
	private static String layerTypeToExplanation(String layer, Instances data) {
		String erantzuna = "None";
		if (layer.equals("0")) {
			erantzuna = "No hidden units. = 0";
		} else if (layer.equals("a")) {
			erantzuna = "(Number of attributes + Number of classes) / 2 = " +(data.numAttributes()+data.numClasses())/2;
		} else if (layer.equals("i")) {
			erantzuna = "Number of attributes = " + data.numAttributes();
		} else if (layer.equals("o")) {
			erantzuna = "Number of classes = " + data.numAttributes();
		} else if (layer.equals("t")) {
			erantzuna = "Number of attributes + Number of classes = " + (data.numAttributes()+data.numClasses()); 
		
		} else {
			erantzuna = "Not specified";
		}
			
		return erantzuna;
		
		
	}
}

