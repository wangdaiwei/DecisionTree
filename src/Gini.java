import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

		Collections.sort(alItems, new Comparator<String>() {
			public int compare(String str1, String str2) {
				return str1.compareTo(str2);
			}
		});

		List<String> alLeftChild, alRightChild;

		alLeftChild = new ArrayList<String>();
		alRightChild = new ArrayList<String>();

		alRightChild.addAll(alItems);
		int nItemNum = getItemNum(alItems);
		for (int i = 0; i < nItemNum - 1; ++i) {
			alLeftChild = new ArrayList<String>(alItems.subList(0, i + 1));
			alRightChild = new ArrayList<String>(alItems.subList(i + 1,
					nItemNum));

			array = setAccuArray(alCalcAttr, alClasAttr,
					(ArrayList<String>) alLeftChild,
					(ArrayList<String>) alRightChild);
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
		
		ArrayList<int[]> lFlag = setFlag(nItemNum);
		
		

		for (int i = 0; i < lFlag.size(); ++i) {
			
			List<String> alLeftChild, alRightChild;

			alLeftChild = new ArrayList<String>();
			alRightChild = new ArrayList<String>();

			// Set Flag of categories, create alLeftChild, alRightChild
			int[] nFlag = (int[])lFlag.get(i);
			for (int j=0; j<nFlag.length;++j){
				alLeftChild.add(alItems.get(nFlag[j]));
			}
			
			for (int j=0; j<nItemNum;++j){
				if (!alLeftChild.contains(alItems.get(j))){
					alRightChild.add(alItems.get(j));
				}
			}
			
			array = setAccuArray(alCalcAttr, alClasAttr,
					(ArrayList<String>) alLeftChild,
					(ArrayList<String>) alRightChild);
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

		array = setAccuArray(alCalcAttr, alClasAttr, alLeftChild, alRightChild);
		fGini = getGiniValue(array);
		rst.setGiniValue(fGini);
		rst.setAlMinLeftChild(alLeftChild);
		rst.setAlMinRightChild(alRightChild);
		return rst;
	}

	/**
	 * 从n个数字中选择m个数字
	 * 
	 * @param a
	 * @param m
	 * @return
	 * @throws Exception
	 */
	public ArrayList<int[]> combine(int[] a, int m) throws Exception {
		int n = a.length;
		if (m > n) {
			throw new Exception("错误！数组a中只有" + n + "个元素。" + m + "大于" + 2 + "!!!");
		}

		ArrayList<int[]> result = new ArrayList<int[]>();

		int[] bs = new int[n];
		for (int i = 0; i < n; i++) {
			bs[i] = 0;
		}
		// 初始化
		for (int i = 0; i < m; i++) {
			bs[i] = 1;
		}
		boolean flag = true;
		boolean tempFlag = false;
		int pos = 0;
		int sum = 0;
		// 首先找到第一个10组合，然后变成01，同时将左边所有的1移动到数组的最左边
		do {
			sum = 0;
			pos = 0;
			tempFlag = true;
			result.add(addtoList(bs, a, m));

			for (int i = 0; i < n - 1; i++) {
				if (bs[i] == 1 && bs[i + 1] == 0) {
					bs[i] = 0;
					bs[i + 1] = 1;
					pos = i;
					break;
				}
			}
			// 将左边的1全部移动到数组的最左边

			for (int i = 0; i < pos; i++) {
				if (bs[i] == 1) {
					sum++;
				}
			}
			for (int i = 0; i < pos; i++) {
				if (i < sum) {
					bs[i] = 1;
				} else {
					bs[i] = 0;
				}
			}

			// 检查是否所有的1都移动到了最右边
			for (int i = n - m; i < n; i++) {
				if (bs[i] == 0) {
					tempFlag = false;
					break;
				}
			}
			if (tempFlag == false) {
				flag = true;
			} else {
				flag = false;
			}

		} while (flag);
		result.add(addtoList(bs, a, m));

		return result;
	}

	private int[] addtoList(int[] bs, int[] a, int m) {
		int[] result = new int[m];
		int pos = 0;
		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == 1) {
				result[pos] = a[i];
				pos++;
			}
		}
		return result;
	}

	private ArrayList<int[]> setFlag(int num){
		/**
		 * Set Flag to the tuples
		 */
		
		int[] nIndex = new int[num];
		for (int i=0; i<num;i++){
			nIndex[i]=i;
		}

		ArrayList<int[]> lFlag = new ArrayList<int[]> ();
		for (int i=0; i<num/2;i++){
			ArrayList<int[]> Flag = new ArrayList<int[]>();
			try {
				Flag = combine(nIndex,i+1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lFlag.addAll(Flag);
		}
		
		return lFlag;
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
		String[] strContinuous = { "39", "50", "38", "53", "28", "37" };
		String[] strCategory = { "United-States", "England", "Canada",
				"Germany", "United-States", "Germany" };

		ArrayList<String> alCalc = new ArrayList<String>();
		ArrayList<String> alClas = new ArrayList<String>();

		for (int i = 0; i < strBinary.length; ++i) {
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
