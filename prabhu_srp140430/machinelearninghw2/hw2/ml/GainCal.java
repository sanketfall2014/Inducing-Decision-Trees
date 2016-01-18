package hw2.ml;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GainCal {

	private static int phase = 0;
	HashMap<String, ArrayList<String>> mapOfData;
	HashMap<String, Double> gainMap ;



	public static HashMap<String,ArrayList<ArrayList<String>>> mapBasedOnBestAttribute(ArrayList<ArrayList<String>> data, String bestAttribute){
		HashMap<String, ArrayList<ArrayList<String>>> reducedMap = new HashMap<String, ArrayList<ArrayList<String>>>();
		int index = data.get(0).indexOf(bestAttribute);
		
		for(int i=1;i<data.size();i++){
			if(data.get(i).get(index).equalsIgnoreCase("0")){
				if(reducedMap.containsKey("0")){
					reducedMap.get("0").add(data.get(i));
				}
				else{
					ArrayList<ArrayList<String>> dataAdd = new ArrayList<ArrayList<String>>();
					dataAdd.add(data.get(0));
					dataAdd.add(data.get(i));
					reducedMap.put("0",dataAdd);
				}

			}
			else{
				if(reducedMap.containsKey("1")){
					reducedMap.get("1").add(data.get(i));
				}
				else{
					ArrayList<ArrayList<String>> dataAdd = new ArrayList<ArrayList<String>>();
					dataAdd.add(data.get(0));
					dataAdd.add(data.get(i));
					reducedMap.put("1",dataAdd);
				}
			}
		}

		return reducedMap;
	}


	public static HashMap<String, ArrayList<String>> mapPopulation(ArrayList<ArrayList<String>> data) throws FileNotFoundException{
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		ArrayList<String> keys = data.get(0);	

		for(int i=0;i<keys.size();i++){
			for(int j=1;j<data.size();j++){
				if (map.containsKey(keys.get(i))){
					map.get(keys.get(i)).add(data.get(j).get(i));
				}
				else{
					ArrayList<String> values = new ArrayList<String>();
					values.add(data.get(j).get(i));
					map.put(keys.get(i), values);
				}
			}
		}
		return map;
	}


	public static double logOfBase(double num, double base){
		return Math.log(num)/Math.log(base);
	}

	public static double entropy(double pos, double neg){
		double total = pos + neg;
		double probabpos = pos/total;
		double probabneg = neg/total;

		if(pos == neg){
			return 1;
		}
		else if(pos == 0 || neg == 0){
			return 0;
		}
		else{
			double entropy = ((-probabpos) * (logOfBase (probabpos,2))) + ((-probabneg)*(logOfBase(probabneg, 2)));
			return entropy;
		}

	}
	
	public double informationGain(double rootPositive, double rootNegative, double positiveLeft, double negativeLeft, double positiveRight, double negativeRight){
		double totalRoot = rootPositive + rootNegative;
		double rootEntropy = entropy(rootPositive, rootNegative);
		double leftEntropy = entropy(positiveLeft,negativeLeft);
		double rightEntropy = entropy(positiveRight, negativeRight);
		double totalLeft = positiveLeft + negativeLeft;
		double totalRight = positiveRight + negativeRight;

		double gain = rootEntropy - (((totalLeft/totalRoot)* leftEntropy) + ((totalRight/totalRoot) * rightEntropy));

		return gain;
	}

	


	public String bestAttribute(ArrayList<ArrayList<String>> data, ArrayList<String> attributeList) throws FileNotFoundException{
		String bestAttribute = "";
		mapOfData = mapPopulation(data);
		gainMap = new HashMap<String, Double>();
		
		double classPositive = 0;
		double classNegative = 0;
		for(String value : mapOfData.get("Class")){
			if(value.equalsIgnoreCase("1")){
				classPositive++;
			}
			else{
				classNegative++;
			}
		}

		for(String key: attributeList.subList(0, attributeList.size()-1)){		
			ArrayList<String> temp = mapOfData.get(key);
			double positiveLeft = 0;
			double positiveRight = 0;
			double negativeLeft = 0;
			double negativeRight = 0;
			for(int i=0; i<temp.size();i++){								
				if(temp.get(i).equalsIgnoreCase("0")){
					if(mapOfData.get("Class").get(i).equalsIgnoreCase("1")){
						positiveLeft++;
					}
					else{
						negativeLeft++;
					}
				}
				else{
					if(mapOfData.get("Class").get(i).equalsIgnoreCase("1")){
						positiveRight++;
					}
					else{
						negativeRight++;
					}
				}
			}

			Double gainForEachKey = informationGain(classPositive, classNegative, positiveLeft, negativeLeft, positiveRight, negativeRight);
			gainMap.put(key, gainForEachKey);
		}

		ArrayList<Double> valueList = new ArrayList<Double>(gainMap.values());
		Collections.sort(valueList);
		Collections.reverse(valueList);
		for(String key: gainMap.keySet()){
			if (valueList.get(0).equals(gainMap.get(key))){
				bestAttribute = key;
				break;
			}
		}
		return bestAttribute;		
	}

}
