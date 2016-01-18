package hw2.ml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Pruning {



	private int numberNonLeafNodes = 0;

	TreeNode treeBest;
	TreeNode treePrime;


	public TreeNode treeConstruct(ArrayList<ArrayList<String>> sampleSpace, ArrayList<String> attributeList) throws FileNotFoundException{
		int countZero = 0;
		int countOne = 0;

		for(int i=1; i < sampleSpace.size();i++){
			if(sampleSpace.get(i).get(sampleSpace.get(i).size()-1).equalsIgnoreCase("1")){
				countOne++;
			}
			else{
				countZero++;
			}
		}
		if (attributeList.isEmpty() || countZero == sampleSpace.size()-1){
			return new TreeNode("0");

		}
		else if(attributeList.isEmpty() || countOne == sampleSpace.size()-1){
			return new TreeNode("1");
		}
		else{

			GainCal gc = new GainCal();
			String bestAttribute = gc.bestAttribute(sampleSpace,attributeList);

			ArrayList<String> attributes2 = new ArrayList<String>();

			HashMap<String, ArrayList<ArrayList<String>>> newMap = GainCal.mapBasedOnBestAttribute(sampleSpace, bestAttribute);
			for(String att: attributeList){
				if(!att.equalsIgnoreCase(bestAttribute)){
					attributes2.add(att);
				}
			}


			if (newMap.size() < 2){
				String value = "0";
				if(countOne > countZero){
					value = "1";
				}

				return new TreeNode(value);
			}


			return new TreeNode(bestAttribute, treeConstruct(newMap.get("0"),attributes2),treeConstruct(newMap.get("1"),attributes2));
		}


	}

	


	

	public boolean treeOutputCheck(TreeNode root, ArrayList<String> row, ArrayList<String> attributeList){
		TreeNode nodeCopy = root;
		while(true){
			if(nodeCopy.isLeafNode()){
				if(nodeCopy.getLeafValue().equalsIgnoreCase(row.get(row.size()-1))){
					return true;
				}
				else{
					return false;
				}
			}

			int index = attributeList.indexOf(nodeCopy.getName());
			String value = row.get(index);
			if(value.equalsIgnoreCase("0")){
				nodeCopy = nodeCopy.getLeft();
			}
			else{
				nodeCopy = nodeCopy.getRight();
			}
		}
	}





	public double accuracyOfTree(TreeNode node, ArrayList<ArrayList<String>> dataToBeChecked){
		double accuracy = 0;
		int positiveExamples = 0;

		ArrayList<String> attributes = dataToBeChecked.get(0);
		for(ArrayList<String> row : dataToBeChecked.subList(1, dataToBeChecked.size())){	
			boolean exampleCheck = treeOutputCheck(node, row, attributes);					
			if(exampleCheck){
				positiveExamples++;
			}
		}
		accuracy = (((double) positiveExamples / (double) (dataToBeChecked.size()-1)) * 100.00);

		return accuracy;
	}




	public void copyTree(TreeNode first, TreeNode second){
		second.setLeafNode(first.isLeafNode());
		second.setName(first.getName());
		second.setLeafValue(first.getLeafValue());

		if(!first.isLeafNode()){
			second.setLeft(new TreeNode());
			second.setRight(new TreeNode());

			copyTree(first.getLeft(), second.getLeft());
			copyTree(first.getRight(), second.getRight());

		}
	}


	public int getNumberNonLeafNodes() {		
		int number = numberNonLeafNodes;
		setNumberNonLeafNodes(0);
		return number;
	}


	public void setNumberNonLeafNodes(int numberNonLeafNodes) {
		this.numberNonLeafNodes = numberNonLeafNodes;
	}


	public void calculateNumLeafNodes(TreeNode root){		
		if(!root.isLeafNode()){								
			numberNonLeafNodes++;
			root.setNodeNumber(numberNonLeafNodes);
			calculateNumLeafNodes(root.getLeft());
			calculateNumLeafNodes(root.getRight());
		}
	}

	public List<TreeNode> retrieveLeafNodeList(TreeNode root){
		List<TreeNode> leafNodeList = new ArrayList<>();
		if(root.isLeafNode()){ 
			leafNodeList.add(root);
		}
		else{
			if(!root.getLeft().isLeafNode()){
				retrieveLeafNodeList(root.getLeft());
			}
			if(!root.getRight().isLeafNode()){
				retrieveLeafNodeList(root.getRight());
			}
		}
		return leafNodeList;
	}

	public String calculateMajorityClass(TreeNode root){
		int countZero = 0;
		int countOne = 0;
		String majority = "0";
		List<TreeNode> leafNodes = retrieveLeafNodeList(root);
		for(TreeNode node : leafNodes){
			if(node.getLeafValue().equalsIgnoreCase("1")){
				countOne++;
			}
			else{
				countZero++;
			}
		}
		if(countOne>countZero){
			majority = "1";
		}

		return majority;
	}

	public void replaceNode(TreeNode root, int P){
		if(!root.isLeafNode()){
			if(root.getNodeNumber() == P){
		
				String leafValueToBeChanged = calculateMajorityClass(root);
				root.setLeafNode(Boolean.TRUE);
				root.setLeft(null);
				root.setRight(null);
				root.setLeafValue(leafValueToBeChanged);
			}
			else{
				replaceNode(root.getLeft(), P);
				replaceNode(root.getRight(), P);
			}

		}
	}

	

	public TreeNode prunedTree(TreeNode root, int l, int k, ArrayList<ArrayList<String>> validationData){
		treeBest = new TreeNode();
		copyTree(root, treeBest);
		
		double bestAccuracyOfTree = accuracyOfTree(treeBest, validationData);
		treePrime = new TreeNode();
		for(int i=1; i<=l;i++){
			copyTree(root, treePrime);
			
			Random random = new Random();

			int M = 1 + random.nextInt(k);
			for(int j=0; j<=M; j++){
				calculateNumLeafNodes(treePrime);			
				int N = getNumberNonLeafNodes();
				
				if(N>1){
					int P = random.nextInt(N) + 1;
					replaceNode(treePrime, P);
				}
				else{
					break;
				}
			}
			double accuracyOfPrimeTree = accuracyOfTree(treePrime, validationData);
			if (accuracyOfPrimeTree > bestAccuracyOfTree){
				bestAccuracyOfTree = accuracyOfPrimeTree;
				copyTree(treePrime, treeBest);
				
			}
		}
		return treeBest;
	}



}
