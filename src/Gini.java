import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Target: Calculate the Gini value of a certain attribute
 * 
 * Input: 1. The attribute list that we need to calculate - ArrayList 2. The
 * classified attribute list - ArrayList 3. The name of the attribute - String
 * 
 * Output: 1. Gini value - double 2. (Undetermined)The best divided position(if
 * have) - ArrayList
 * 
 */

public class Gini {

	public static final int BINARY = 1;
	public static final int CATEGORY = 2;
	public static final int CONTINUOUS = 3;

	public static HashMap<String, Integer> hmAttrNames; // define the
														// attributes' type

	public Gini() {
		hmAttrNames = new HashMap<String, Integer>();
		buildAttrNamesSet();
	}

	public Result getGini(ArrayList<String> alCalcAttr,
			ArrayList<String> alClasAttr, String strAttrName) {
		int nType;
//		double nGiniValue = -1;
		Result rst = new Result();

		nType = (int) hmAttrNames.get(strAttrName);

		if (nType == BINARY) {
			rst = calculateBinGini(alCalcAttr, alClasAttr);
		} else if (nType == CATEGORY) {
			rst = calculateCateGini(alCalcAttr, alClasAttr);
		} else if (nType == CONTINUOUS) {
			rst = calculateContGini(alCalcAttr, alClasAttr);
		} else {
			System.out.println("Wrong input");
		}

		return rst;
	}

	private Result calculateContGini(ArrayList<String> alCalcAttr,
			ArrayList<String> alClasAttr) {
		int[][] array = new int[2][2];
		double fGini, fMinGini = -1;
		Result rst = new Result();

		ArrayList<String> alMinLeftChild, alMinRightChild;

		alMinLeftChild = new ArrayList<String>();
		alMinRightChild = new ArrayList<String>();

		ArrayList<String> alItems = getAllItems(alCalcAttr);
		int nItemNum = getItemNum(alItems);

		if ( nItemNum <= 1 ) {
			alMinLeftChild = alCalcAttr;
			alMinRightChild = alCalcAttr;
			array = setAccuArray(alCalcAttr, alClasAttr, alMinLeftChild, alMinRightChild);
			array[0][1] = array[0][0];
			array[1][1] = array[1][0];
			fMinGini = getGiniValue(array);
			rst.setGiniValue(fMinGini);
			rst.setAlMinLeftChild(alMinLeftChild);
			rst.setAlMinRightChild(alMinRightChild);
			return rst;
		}

		Collections.sort(alItems, new Comparator<String>() {
			public int compare(String str1, String str2) {
//				int result = Integer.parseInt(str1) - Integer.parseInt(str2);
				try{
					return Integer.parseInt(str1) - Integer.parseInt(str2) > 0 ? 1 : -1;
				}
				catch (Exception e){
					System.out.print(e.getMessage());
				}	
				return 1;
			}
		});

		HashMap< String, int[] > attrClassRatioMap = getAttrClassRatio ( alCalcAttr, alClasAttr );
		List< Map.Entry< String, int[] > > list = new LinkedList< Map.Entry<String, int[]> >(attrClassRatioMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator< Map.Entry<String, int[]> >() {
			public int compare(Map.Entry<String, int[]> o1, Map.Entry<String, int[]> o2) {
				int[] attr1 = o1.getValue();
				int[] attr2 = o2.getValue();
				double attr1Ratio , attr2Ratio;
				if ( attr1[1] == 0 )  attr1Ratio = (double) attr1[0] + 1;
				else attr1Ratio = (double) attr1[0] / (double) attr1[1];
				if ( attr2[1] == 0 )  attr2Ratio = (double) attr2[0] + 1;
				else attr2Ratio = (double) attr2[0] / (double) attr2[1];
				if ( attr1Ratio - attr2Ratio > 0 ) return 1;
				else if ( attr1Ratio - attr2Ratio == 0 ) return 0;
				return -1;
			}
		});

		List<String> alLeftChild, alRightChild;

		alLeftChild = new ArrayList<String>();
		alRightChild = new ArrayList<String>();

		alRightChild.addAll(alItems);
		for (int i = 0; i < nItemNum - 1; ++i) {
			alLeftChild = new ArrayList<String>(alItems.subList(0, i + 1));
			alRightChild = new ArrayList<String>(alItems.subList(i + 1,
					nItemNum));

			array = new int[][]{ {0, 0}, {0, 0} };
			for ( String attr: alLeftChild ) {
				int[] classCount = attrClassRatioMap.get( attr );
				array[0][0] += classCount[0];
				array[1][0] += classCount[1];
			}
			for ( String attr: alRightChild ) {
				int[] classCount = attrClassRatioMap.get( attr );
				array[0][1] += classCount[0];
				array[1][1] += classCount[1];
			}

			// array = setAccuArray(alCalcAttr, alClasAttr,
			// 		(ArrayList<String>) alLeftChild,
			// 		(ArrayList<String>) alRightChild);
			fGini = getGiniValue(array);

			if (fMinGini == -1 || fGini < fMinGini) {
				fMinGini = fGini;
				alMinLeftChild = (ArrayList<String>) alLeftChild;
				alMinRightChild = (ArrayList<String>) alRightChild;
			}
		}

		rst.setGiniValue(fMinGini);
		rst.setAlMinLeftChild(alMinLeftChild);
		rst.setAlMinRightChild(alMinRightChild);
		return rst;
		}

	private Result calculateCateGini(ArrayList<String> alCalcAttr,
			ArrayList<String> alClasAttr) {
		int[][] array = new int[2][2];
		double fGini, fMinGini = -1;
		Result rst = new Result();

		ArrayList<String> alMinLeftChild, alMinRightChild;

		alMinLeftChild = new ArrayList<String>();
		alMinRightChild = new ArrayList<String>();

		ArrayList<String> alItems = getAllItems(alCalcAttr);
		int nItemNum = getItemNum(alItems);

		if ( nItemNum <= 1 ) {

			alMinLeftChild = alCalcAttr;
			alMinRightChild = alCalcAttr;
			array = setAccuArray(alCalcAttr, alClasAttr, alMinLeftChild, alMinRightChild);
			array[0][1] = array[0][0];
			array[1][1] = array[1][0];
			fMinGini = getGiniValue(array);
			rst.setGiniValue(fMinGini);
			rst.setAlMinLeftChild(alMinLeftChild);
			rst.setAlMinRightChild(alMinRightChild);
			return rst;
		}
		
		HashMap< String, int[] > attrClassRatioMap = getAttrClassRatio ( alCalcAttr, alClasAttr );
		List< Map.Entry< String, int[] > > list = new LinkedList< Map.Entry<String, int[]> >(attrClassRatioMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator< Map.Entry<String, int[]> >() {
			public int compare(Map.Entry<String, int[]> o1, Map.Entry<String, int[]> o2) {
				int[] attr1 = o1.getValue();
				int[] attr2 = o2.getValue();
				double attr1Ratio , attr2Ratio;
				if ( attr1[1] == 0 )  attr1Ratio = (double) attr1[0] + 1;
				else attr1Ratio = (double) attr1[0] / (double) attr1[1];
				if ( attr2[1] == 0 )  attr2Ratio = (double) attr2[0] + 1;
				else attr2Ratio = (double) attr2[0] / (double) attr2[1];
				if ( attr1Ratio - attr2Ratio > 0 ) return 1;
				else if ( attr1Ratio - attr2Ratio == 0 ) return 0;
				return -1;
			}
		});
 
		Map<String, int[]> sortedAttrClassRatioMap = new LinkedHashMap<String, int[]>();
		for (Map.Entry<String, int[]> entry : list) {
			sortedAttrClassRatioMap.put(entry.getKey(), entry.getValue());
		}
		ArrayList<String> sortedItemList = new ArrayList<String>(sortedAttrClassRatioMap.keySet());

		for (int i = 0; i < nItemNum; ++i) {
			List<String> alLeftChild, alRightChild;

			alLeftChild = new ArrayList<String>();
			alRightChild = new ArrayList<String>();
			alRightChild.addAll(sortedItemList);

			for( int groupIndex = 0; groupIndex < i; groupIndex++ ) {

				alLeftChild.add( sortedItemList.get(groupIndex) );
				alRightChild.remove( sortedItemList.get(groupIndex) );
			}

			array = new int[][]{ {0, 0}, {0, 0} };
			for ( String attr: alLeftChild ) {
				int[] classCount = attrClassRatioMap.get( attr );
				array[0][0] += classCount[0];
				array[1][0] += classCount[1];
			}
			for ( String attr: alRightChild ) {
				int[] classCount = attrClassRatioMap.get( attr );
				array[0][1] += classCount[0];
				array[1][1] += classCount[1];
			}
			fGini = getGiniValue(array);

			if (fMinGini == -1 || fGini < fMinGini) {
				fMinGini = fGini;
				alMinLeftChild = (ArrayList<String>) alLeftChild;
				alMinRightChild = (ArrayList<String>) alRightChild;
			}
		}

		rst.setGiniValue(fMinGini);
		rst.setAlMinLeftChild(alMinLeftChild);
		rst.setAlMinRightChild(alMinRightChild);
		return rst;
	}

	private Result calculateBinGini(ArrayList<String> alCalcAttr,
			ArrayList<String> alClasAttr) {
		int[][] array = new int[2][2];
		double fGini;
		Result rst = new Result();
		ArrayList<String> alLeftChild, alRightChild;

		alLeftChild = new ArrayList<String>();
		alRightChild = new ArrayList<String>();

		int nLength = alCalcAttr.size();

		String strX = alCalcAttr.get(0);
		String strY = "";
		for (int i = 0; i < nLength; ++i) {
			if (!alCalcAttr.get(i).equals(strX)) {
				strY = alCalcAttr.get(i);
				break;
			}
		}

		alLeftChild.add(strX);
		alRightChild.add(strY);

		if ( strY.equals("") ) {

			alLeftChild = alCalcAttr;
			alRightChild = alCalcAttr;
			array = setAccuArray(alCalcAttr, alClasAttr, alLeftChild, alRightChild);
			array[0][1] = array[0][0];
			array[1][1] = array[1][0];
			fGini = getGiniValue(array);
			rst.setGiniValue(fGini);
			rst.setAlMinLeftChild(alLeftChild);
			rst.setAlMinRightChild(alRightChild);
			return rst;
		}

		array = setAccuArray(alCalcAttr, alClasAttr, alLeftChild, alRightChild);
		fGini = getGiniValue(array);
		rst.setGiniValue(fGini);
		rst.setAlMinLeftChild(alLeftChild);
		rst.setAlMinRightChild(alRightChild);
		return rst;
	}

	/**
	 * Get every different attribute's class count
	 */
	private HashMap<String, int[] > getAttrClassRatio ( ArrayList<String> alCalcAttr, ArrayList<String> alClasAttr ) {
		HashMap< String, int[] > attrClassRatioMap = new HashMap<String, int[] >();
		String classA = alClasAttr.get(0);
		for ( int i = 0; i < alCalcAttr.size(); i++ ) {
			String attr = alCalcAttr.get(i);
			String classification = alClasAttr.get(i);
			if ( !attrClassRatioMap.containsKey(attr) ) {
				attrClassRatioMap.put( attr, new int[2] );
			}
			if ( classA.equals( classification ) ) {
				int[] classCount = attrClassRatioMap.get( attr );
				++classCount[0];
				attrClassRatioMap.put( attr, classCount );
			} else {
				int[] classCount = attrClassRatioMap.get( attr );
				++classCount[1];
				attrClassRatioMap.put( attr, classCount );
			}
		}
		return attrClassRatioMap;
	}
	
	private ArrayList<String> getAllItems(ArrayList<String> alCalcAttr) {
		ArrayList<String> alItems = new ArrayList<String>();
		int nLength = alCalcAttr.size();

		for (int i = 0; i < nLength; ++i) {
			if (!alItems.contains(alCalcAttr.get(i))) {
				alItems.add(alCalcAttr.get(i));
			}
		}
		return alItems;
	}

	private int getItemNum(ArrayList<String> alCalcAttr) {
		int nNum = getAllItems(alCalcAttr).size();
		return nNum;
	}

	// get the 2*2 array
	private int[][] setAccuArray(ArrayList<String> alCalcAttr,
			ArrayList<String> alClasAttr, ArrayList<String> alLeftChild,
			ArrayList<String> alRightChild) {
		int[][] array = new int[2][2];
		int nLength = alCalcAttr.size();

		String strClas0 = alClasAttr.get(0);
		for (int i = 0; i < nLength; ++i) {

			String strCalci = alCalcAttr.get(i);
			String strClasi = alClasAttr.get(i);

			if (alLeftChild.contains(strCalci)) {
				if (strClasi.equals(strClas0)) {
					++array[0][0];
				} else {
					++array[1][0];
				}
			} else if (alRightChild.contains(strCalci)) {
				if (strClasi.equals(strClas0)) {
					++array[0][1];
				} else {
					++array[1][1];
				}
			}
		}
		return array;
	}

	// calculate the Gini value by 2*2 array
	private double getGiniValue(int[][] array) {
		double fN1, fN2, fGini;

		fN1 = 1
				- Math.pow(
						((double) array[0][0] / (array[0][0] + array[1][0])), 2)
				- Math.pow(
						((double) array[1][0] / (array[0][0] + array[1][0])), 2);
		fN2 = 1
				- Math.pow(
						((double) array[0][1] / (array[0][1] + array[1][1])), 2)
				- Math.pow(
						((double) array[1][1] / (array[0][1] + array[1][1])), 2);

		fGini = fN1
				* ((double) (array[0][0] + array[1][0]) / (array[0][0]
						+ array[1][0] + array[0][1] + array[1][1]))
				+ fN2
				* ((double) (array[0][1] + array[1][1]) / (array[0][0]
						+ array[1][0] + array[0][1] + array[1][1]));

		return fGini;
	}

	// according to file IO, build the hashmap
	private void buildAttrNamesSet() {
		FileReader file = null;
		String fileName = "data/attributesType.txt";

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			String line = "";
			while ((line = reader.readLine()) != null) {
				String strLine[] = line.split(" ");
				int type = Integer.parseInt(strLine[1]);
				hmAttrNames.put(strLine[0], new Integer(type));
			}

			file.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * // Get a set of the entries Set set = hmAttrNames.entrySet(); // Get
		 * an iterator Iterator i = set.iterator(); // Display elements while
		 * (i.hasNext()) { Map.Entry me = (Map.Entry) i.next();
		 * System.out.print(me.getKey() + ": ");
		 * System.out.println(me.getValue()); } System.out.println();
		 */
	}

	public static void main(String[] args) {
		Gini gini = new Gini();

		String[] strClassified = { "<", ">", ">", "<", "<", "<" };
		String[] strAttrNames = { "sex", "age", "native-country" };

		String[] strBinary = { "Female", "Female", "Male", "Female", "Male",
				"Male" };
		String[] strContinuous = { "10", "2", "99", "100", "28", "37" };
		String[] strCategory = { "United-States", "England", "Canada",
				"Germany", "United-States", "Germany" };

		ArrayList<String> alCalc = new ArrayList<String>();
		ArrayList<String> alClas = new ArrayList<String>();

		for (int i = 0; i < strCategory.length; ++i) {
			alCalc.add(strContinuous[i]);
			alClas.add(strClassified[i]);
		}

		Result rst = gini.getGini(alCalc, alClas, strAttrNames[1]);
		
		double GiniValue = rst.getGiniValue();
		ArrayList<String> alMinLeftChild = rst.getAlMinLeftChild();
		ArrayList<String> alMinRightChild = rst.getAlMinRightChild();
		
		System.out.println(GiniValue);
		System.out.println(alMinLeftChild);
		System.out.println(alMinRightChild);

	}
}
