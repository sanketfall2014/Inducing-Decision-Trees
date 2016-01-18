package hw2.ml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Read {


	public ArrayList<ArrayList<String>> read(String fileName) throws IOException{

		

		String fileDirectory = System.getProperty("user.dir");
		StringBuilder builder = new StringBuilder(fileDirectory);
		builder.append(System.getProperty("file.separator"));
		builder.append(fileName);

		String fileNameFinal = builder.toString();
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		File file = new File(fileNameFinal);
		Scanner input;
		input = new Scanner(file);
		while(input.hasNext()){
			String[] dataForEachRow = input.next().split(",");
			data.add(new ArrayList<String>(Arrays.asList(dataForEachRow)));

		}
		input.close();
		return data;
	}

}
