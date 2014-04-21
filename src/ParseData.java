import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;


public class ParseData {

	public static HashSet< ArrayList<String> > preprocessFile ( String fileName, String outputFileName ) {
		HashSet< ArrayList<String> > dataset = new HashSet< ArrayList<String> >();
		try {
			BufferedReader reader = new BufferedReader( new FileReader(fileName) );
			BufferedWriter writer = new BufferedWriter( new FileWriter(outputFileName, false) );
			String line = null;
			int count = 0;
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
					continue;
				}
				count++;
				String[] tmp = line.split(", ");
				//System.out.println("Line:	" + count);
				
				boolean flag = true;
				for (String test: tmp) {
					if (test.equals("?")) {
						flag = false;
					}
				}
//				for (String test: tmp) {
//					System.out.println(test);
//				}
				//System.out.println("===================");
				if (flag == true) {
					// Remove the attribute "native-country"
					for ( int i = 0; i < tmp.length - 2; i++ ) writer.write( tmp[i] + ", " );
					writer.write( tmp[tmp.length - 1] );
					writer.newLine();
				    
				}
				if (count == 100) {
					writer.flush();
				}
			}
			writer.flush();
		    writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dataset;
	}
	
	public static void main(String[] args) {
		preprocessFile("./data/adult.test", "./data/test_parsed.txt");
	}

}
