import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class ParseData {

	public static HashSet< ArrayList<String> > readFile ( String fileName ) {
		HashSet< ArrayList<String> > dataset = new HashSet< ArrayList<String> >();
		try {
			BufferedReader reader = new BufferedReader( new FileReader(fileName) );
			String line = null;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
					continue;
				}
				String[] tmp = line.split(", ");
				//System.out.println("Line:	" + count);
				
				boolean flag = true;
//				if (count == 16281) {
//					for (String test: tmp) {
//						if (test.equals("?")) {
//							flag = false;
//						}
//					}
//				}
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
					//count++;
				}
				count++;
//				if (count == 100) {
//					break;
//				}
			}
			System.out.println("Line:	" + count);
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataset;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readFile("./data/adult.data");
	}

}
