package hw2.ml;

import java.io.IOException;
import java.util.ArrayList;


public class srp140430 {

	public static void main(String[] args) {
		if(args.length == 0){
			System.out.println("Please pass some arguments.");
			System.exit(0);
		}
		else{
			boolean printCounter;
			int L = Integer.parseInt(args[0]);
			int K = Integer.parseInt(args[1]);
			String fileNameTrainingData = args[2];
			String fileNameValidationData = args[3];
			String fileNameTest = args[4];
			String print = args[5];

			if(print.equalsIgnoreCase("Yes")){
				printCounter = Boolean.TRUE;
			}
			else{
				printCounter = Boolean.FALSE;
			}

			Read read = new Read();
			Pruning pruning = new Pruning();
			
			try {

				ArrayList<ArrayList<String>> dataSetTraining = read.read(fileNameTrainingData);
				ArrayList<ArrayList<String>> dataSetValidation = read.read(fileNameValidationData);
				ArrayList<ArrayList<String>> dataSetTest = read.read(fileNameTest);

				ArrayList<String> attributeList = dataSetTraining.get(0);

				TreeNode treeLearningEntropy = pruning.treeConstruct(dataSetTraining, attributeList);
			
				
				System.out.println("Accuracy of Tree with Information Gain Heuristic on test data: " + pruning.accuracyOfTree(treeLearningEntropy, dataSetTest));
			
				System.out.println();
				
				TreeNode prunedTreeEntropy = pruning.prunedTree(treeLearningEntropy, L, K, dataSetValidation);
			
				
				System.out.println("Accuracy of Pruned Tree with Information Gain Heuristic on test data: " + pruning.accuracyOfTree(prunedTreeEntropy, dataSetTest));
			
				System.out.println();
				
				
				if(printCounter){
					System.out.println("------------Tree Using Information Gain Heuristic--------------");
					System.out.println();
					treeLearningEntropy.print();
					System.out.println();
		
					System.out.println();
				}
				
			} catch (IOException e) {
				System.out.println("File could not be found, please check the file name properly");

			}
		}
	}
}
