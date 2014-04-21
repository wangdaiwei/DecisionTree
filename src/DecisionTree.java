///////////////////
// DECISION TREE //
///////////////////

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;

class DecisionTree {

    ////////////////
    // THE FIELDS //
    ////////////////

    // The input dataset.
	// Each ArrayList in the hash set stands for an instance.
	// Replace the ArrayList with other class in the future.
	private HashSet< ArrayList<String> > dataSet;
	// The training set is extracted from the original dataset.
	private HashSet< ArrayList<String> > trainingSet;
	private HashSet< ArrayList<String> > validationSet;

	private final String STRING_DEFAULT_CLASS = "<=50K";
	private final String STRING_OTHER_CLASS = ">50K";
	private final int INT_CLASS_INDEX = 13;

	private static final int LEAF = 0;
	private static final int BINARY = 1;
	private static final int CATEGORY = 2;
	private static final int CONTINUOUS = 3;

	private String[] strAttrNames = { "age", "workclass", "fnlwgt", "education", "education-num",
                            "marital-status", "occupation", "relationship", "race", "sex", "capital-gain",
                            "capital-loss", "hours-per-week" };

	private Gini gini;
	private int nodeID = 0;;

    /* NESTED CLASS */

    private class BinTree {
    	
		/* FIELDS */
	
		private int nodeID;
		private int nodeType;
		private int nodeAttrIndex;
    	private HashSet<String>  nodeInfo = null;
    	private int continuousSplit = 0;
    	private BinTree yesBranch  = null;
    	private BinTree noBranch   = null;

    	private String majorClass = null;
	
		/* CONSTRUCTOR */
	
		public BinTree(	int newNodeID, 
						int newNodeType, 
						int newNodeAttrIndex, 
						HashSet<String> newNodeInfo, 
						String newMajorClass) {

	    	nodeID = newNodeID;
	    	nodeType = newNodeType;
	    	nodeAttrIndex = newNodeAttrIndex;
	    	nodeInfo = newNodeInfo;
	    	majorClass = newMajorClass;
	    	if ( newNodeType == CONTINUOUS ) {
	    		try{
					continuousSplit = Integer.parseInt(newNodeInfo.iterator().next());
				}
				catch (Exception e){
					System.out.print(e.getMessage());
					continuousSplit = 0;
				}	
			}
    	}

    	public void setNodeInfo( HashSet<String> newNodeInfo ) {
			nodeInfo = newNodeInfo;
    	}
    	public void setNodeType( int newNodeType ) {
			nodeType = newNodeType;
    	}
	}

    /* OTHER FIELDS */

    static BufferedReader    keyboardInput = new
                           BufferedReader(new InputStreamReader(System.in));
    BinTree rootNode = null;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    /* Default Constructor */
    public DecisionTree() {
    	String fileName = "./data/data_parsed.txt";
    	readTrainingFile( fileName );
    	divideTrainingSet();
    	System.out.println("File has been read!!");
    	gini = new Gini();
	}

	/**
	 * Construct a decision tree with given file path
	 * @param  fileName
	 */
	public DecisionTree( String fileName ) {
    	readTrainingFile( fileName );
    	divideTrainingSet();
    	gini = new Gini();
	}

	/**
	 * Read the training data file into the hash set.
	 * See reference to the ParseData class.
	 * @param fileName
	 */
	public void readTrainingFile ( String fileName ) {
		dataSet = new HashSet< ArrayList<String> >();
		try {
			BufferedReader reader = new BufferedReader( new FileReader(fileName) );
			String line = null;
			boolean flag;
			ArrayList<String> record;
			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
					continue;
				}
				String[] tmp = line.split(", ");
				
				flag = true;
				for (String test: tmp) {
					if (test.equals("?")) {
						flag = false;
					}
				}
				if ( flag == false ) continue;

				record = new ArrayList<String>();
				// Every record contains all attributes and the classification
				record = new ArrayList<String>(Arrays.asList(tmp));
				// for (String attribute: tmp) {
				// 	record.add( attribute );
				// }
				dataSet.add( record );
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Divide the original training set into two parts.
	public void divideTrainingSet () {
		if ( dataSet == null ) {
			System.out.println( "The dataset hasn't been read!!" );
			return;
		}
		trainingSet = new HashSet< ArrayList<String> >();
		validationSet = new HashSet< ArrayList<String> >();
		int num = 0;
		for ( ArrayList<String> record: dataSet ) {
			num++;
			if ( num % 10 == 0 ) validationSet.add(record);
			else trainingSet.add(record);
		}
	}

    //////////////////////////
    // THE BUILDING METHODS //
    //////////////////////////

    /**
     * Create root node
     * @param newNodeID        [description]
     * @param newNodeType      [description]
     * @param newNodeAttrIndex [description]
     * @param newNodeInfo      [description]
     * @param newMajorClass    [description]
     */
    private void createRoot(int newNodeID, 
    						int newNodeType, 
    						int newNodeAttrIndex, 
    						HashSet<String> newNodeInfo, 
    						String newMajorClass) {

    	rootNode = new BinTree( newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass );
		System.out.println("Created root node " + newNodeID);	
	}
			
    /**
     * Add yes node
     * @param existingNodeID   [description]
     * @param newNodeID        [description]
     * @param newNodeType      [description]
     * @param newNodeAttrIndex [description]
     * @param newNodeInfo      [description]
     */
    private void addYesNode(int existingNodeID, 
    						int newNodeID, 
    						int newNodeType, 
    						int newNodeAttrIndex, 
    						HashSet<String> newNodeInfo,
    						String newMajorClass) {
		
		// If no root node do nothing
	
		if (rootNode == null) {
	   	 	System.out.println("ERROR: No root node!");
	    	return;
	    }
	
		// Search tree
	
		if (searchTreeAndAddYesNode( rootNode, existingNodeID, newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass )) {
	    	System.out.println("Added node " + newNodeID +
	    		" onto \"yes\" branch of node " + existingNodeID);
	    }
		else System.out.println("Node " + existingNodeID + " not found");
	}

    /**
     * Search tree and add yes node
     * @param  currentNode    	[description]
     * @param  existingNodeID 	[description]
     * @param  newNodeID      	[description]
     * @param  newNodeType    	[description]
     * @param  newNodeAttrIndex [description]
     * @param  newNodeInfo   	[description]
     * @return                	[description]
     */
    private boolean searchTreeAndAddYesNode(BinTree currentNode,
    										int existingNodeID, 
    										int newNodeID, 
    										int newNodeType, 
    										int newNodeAttrIndex, 
    										HashSet<String> newNodeInfo, 
    										String newMajorClass) {

    	if (currentNode.nodeID == existingNodeID) {
	    	// Found node
	    	if (currentNode.yesBranch == null) currentNode.yesBranch = new
	    			BinTree( newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass );
	    	else {
	        	System.out.println("WARNING: Overwriting previous node " +
					"(id = " + currentNode.yesBranch.nodeID +
					") linked to yes branch of node " +
					existingNodeID);
				currentNode.yesBranch = new BinTree( newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass );
			}		
    	    return true;
	    }
		else {
	    	// Try yes branch if it exists
	    	if (currentNode.yesBranch != null) { 	
	        	if (searchTreeAndAddYesNode( currentNode.yesBranch,
		        	existingNodeID, newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass )) {    	
	            	return true;
		    	}	
				else {
    	        	// Try no branch if it exists
	    	    	if (currentNode.noBranch != null) {
    	    			return(searchTreeAndAddYesNode( currentNode.noBranch,
						existingNodeID, newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass ));
					}
		    	else return false;	// Not found here
		    	}
    		}
	    	return false;		// Not found here
	    }
   	} 	
    		
    /**
     * Add no node
     * @param existingNodeID [description]
     * @param newNodeID      [description]
     * @param newNodeType    [description]
     * @param newNodeInfo    [description]
     */
    private void addNoNode(	int existingNodeID, 
    						int newNodeID, 
    						int newNodeType, 
    						int newNodeAttrIndex, 
    						HashSet<String> newNodeInfo, 
    						String newMajorClass) {

		// If no root node do nothing
	
		if (rootNode == null) {
	    	System.out.println("ERROR: No root node!");
	    	return;
	    }
	
		// Search tree
	
		if (searchTreeAndAddNoNode( rootNode, existingNodeID, newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass )) {
	    	System.out.println("Added node " + newNodeID +
	    		" onto \"no\" branch of node " + existingNodeID);
	    }
		else System.out.println("Node " + existingNodeID + " not found");
	}
	
    /**
     * Search tree and add no node
     * @param  currentNode    [description]
     * @param  existingNodeID [description]
     * @param  newNodeID      [description]
     * @param  newNodeType    [description]
     * @param  newNodeInfo    [description]
     * @return                [description]
     */
    private boolean searchTreeAndAddNoNode(	BinTree currentNode,
    										int existingNodeID, 
    										int newNodeID, 
    										int newNodeType, 
    										int newNodeAttrIndex, 
    										HashSet<String> newNodeInfo, 
    										String newMajorClass) {

    	if (currentNode.nodeID == existingNodeID) {
	    	// Found node
	    	if (currentNode.noBranch == null) currentNode.noBranch = new
	    		BinTree( newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass );
	    	else {
	        	System.out.println("WARNING: Overwriting previous node " +
				"(id = " + currentNode.noBranch.nodeID +
				") linked to yes branch of node " +
				existingNodeID);
			currentNode.noBranch = new BinTree( newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass );
			}		
    	    return true;
	    }
		else {
	   		// Try yes branch if it exists
	    	if (currentNode.yesBranch != null) { 	
	        	if (searchTreeAndAddNoNode( currentNode.yesBranch,
		        	existingNodeID, newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass )) {    	
	            	return true;
		    	}	
			else {
    	        // Try no branch if it exists
	    	    if (currentNode.noBranch != null) {
    	    		return(searchTreeAndAddNoNode( currentNode.noBranch,
						existingNodeID, newNodeID, newNodeType, newNodeAttrIndex, newNodeInfo, newMajorClass ));
				}
		    	else return false;	// Not found here
		    }
			}
	    	else return false;	// Not found here
	    }
   	} 	

   	public void generateTree() {

   		searchBestNode( 0, 1, trainingSet );
   		reviseTree( rootNode );

        // System.out.println("\nGENERATE DECISION TREE");
        // System.out.println("======================");
        // newTree.createRoot(1,"Does animal eat meat?");
        // newTree.addYesNode(1,2,"Does animal have stripes?");
        // newTree.addNoNode(1,3,"Does animal have stripes?");
        // newTree.addYesNode(2,4,"Animal is a Tiger");
        // newTree.addNoNode(2,5,"Animal is a Leopard");
        // newTree.addYesNode(3,6,"Animal is a Zebra");
        // newTree.addNoNode(3,7,"Animal is a Horse");
    }

    private void searchBestNode( int currentNodeID, int newNodeID, HashSet< ArrayList<String> > inputData ) {
    	// if ( newNodeID > 20 ) {
    	// 	System.out.println( "Node ID > 20!!" );
    	// 	return;
    	// }
    	// Check if the input data is empty
    	if ( inputData.isEmpty() ) {
    		HashSet<String> newNodeInfo = new HashSet<String>();
    		newNodeInfo.add(STRING_DEFAULT_CLASS);
    		if ( newNodeID % 2 == 0 ) addYesNode( newNodeID/2, newNodeID, 0, 0, newNodeInfo, STRING_DEFAULT_CLASS );
    		else addNoNode( newNodeID/2, newNodeID, 0, 0, newNodeInfo, STRING_DEFAULT_CLASS );
    		return;
    	}

    	// Check if input data have all the same class
    	boolean sameFlag = true;
    	String firstClass = inputData.iterator().next().get( INT_CLASS_INDEX );
    	for ( ArrayList<String> record: inputData ) {
    		if ( !firstClass.equals( record.get( INT_CLASS_INDEX ) ) ) {
    			sameFlag = false;
    			break;
    		}
    	}
    	if ( sameFlag == true ) {
    		HashSet<String> newNodeInfo = new HashSet<String>();
    		newNodeInfo.add(firstClass);
    		String newMajorClass = firstClass;
    		if ( newNodeID % 2 == 0 ) addYesNode( newNodeID/2, newNodeID, 0, 0, newNodeInfo, newMajorClass );
    		else addNoNode( newNodeID/2, newNodeID, 0, 0, newNodeInfo, newMajorClass );
    		return;
    	}

    	double minGiniValue = 100000;
    	String minAttrName = null;
    	int minAttrIndex = -1;
    	ArrayList<String> alMinLeftChild = null;
		ArrayList<String> alMinRightChild = null;
		ArrayList< ArrayList<String> > oneAttributeRecordList;
		Result rst;
    	for ( int i = 0; i < INT_CLASS_INDEX; i++ ) {
    		oneAttributeRecordList = getSingleAttributeWithClass( inputData, i );
    		rst = gini.getGini( oneAttributeRecordList.get(0), oneAttributeRecordList.get(1), strAttrNames[i]);
    		double giniValue = rst.getGiniValue();
    		if ( giniValue < minGiniValue ) {
    			minGiniValue = giniValue;
    			minAttrName = strAttrNames[i];
    			minAttrIndex = i;
    			alMinLeftChild = rst.getAlMinLeftChild();
    			alMinRightChild = rst.getAlMinRightChild();
    		}
    	}

    	if ( alMinLeftChild == alMinRightChild ) return;

    	HashSet<String> newNodeInfo = new HashSet<String>();
    	if ( gini.hmAttrNames.get( minAttrName ) == CONTINUOUS ) {
    		newNodeInfo = new HashSet<String>();
    		if ( alMinLeftChild.size() > 0 ) newNodeInfo.add( alMinLeftChild.get( alMinLeftChild.size() - 1 ) );
    	} else {
    		newNodeInfo = new HashSet<String>( alMinLeftChild );
    	}

    	HashSet<String> setMinLeftChild = new HashSet<String>( alMinLeftChild );
    	HashSet<String> setMinRightChild = new HashSet<String>( alMinRightChild );
    	HashSet< ArrayList<String> > setLeftChildData = splitRecordWithSelectedAttr( inputData, minAttrIndex, setMinLeftChild );

    	@SuppressWarnings("unchecked")
    	HashSet< ArrayList<String> > setRightChildData = ( HashSet< ArrayList<String> > ) inputData.clone();
    	// The left child node is empty
    	if ( !setRightChildData.removeAll( setLeftChildData ) ) {
    		System.out.println( "minGiniValue: " + minGiniValue );
    		System.out.println( "minAttrName: " + minAttrName );
    		return;
    	} 
    	int attrType = gini.hmAttrNames.get(minAttrName);

    	if ( rootNode == null ) {
    		createRoot( newNodeID, attrType, minAttrIndex, newNodeInfo, STRING_DEFAULT_CLASS );
    		searchBestNode( 1, 2, setLeftChildData );
    		searchBestNode( 1, 3, setRightChildData );
    	} else {
    		double previousAccuracy = testAccuracy( validationSet );
    		System.out.println( "Previous accuracy: " + previousAccuracy );
    		String newMajorClass = getMajorClass( inputData );
    		if ( newNodeID % 2 == 0 ) addYesNode( newNodeID/2, newNodeID, attrType, minAttrIndex, newNodeInfo, newMajorClass );
    		else addNoNode( newNodeID/2, newNodeID, attrType, minAttrIndex, newNodeInfo, newMajorClass );
    		double accuracy = testAccuracy( validationSet );
    		System.out.println( "Current accuracy: " + accuracy );
    		if ( accuracy >= previousAccuracy ) {
    			searchBestNode( newNodeID, 2*newNodeID, setLeftChildData );
	    		searchBestNode( newNodeID, 2*newNodeID + 1, setRightChildData );
    		}
    	}
    	return;
    }

    private String getMajorClass( HashSet< ArrayList<String> > inputData ) {
    	int defaultCount = 0;
    	for ( ArrayList<String> record: inputData ) {
    		if ( record.get( INT_CLASS_INDEX ) == STRING_DEFAULT_CLASS ) defaultCount++;
    	}
    	return defaultCount > inputData.size()/2 ? STRING_DEFAULT_CLASS: STRING_OTHER_CLASS;
    }

    private HashSet< ArrayList<String> > splitRecordWithSelectedAttr( HashSet< ArrayList<String> > inputData, 
    	int attrIndex, HashSet<String> attributes ) {

    	HashSet< ArrayList<String> > result = new HashSet< ArrayList<String> >();
    	for ( ArrayList<String> record: inputData ) {
    		if ( attributes.contains( record.get( attrIndex ) ) ) result.add( record );
    	}

    	return result;
    }

    private ArrayList< ArrayList<String> > getSingleAttributeWithClass( HashSet< ArrayList<String> > inputData, int attrIndex ) {
    	ArrayList< ArrayList<String> > result = new ArrayList< ArrayList<String> >();
    	ArrayList<String> attributeList = new ArrayList<String>();
    	ArrayList<String> classList = new ArrayList<String>();
    	for ( ArrayList<String> record: inputData ) {
    		attributeList.add( record.get( attrIndex ) );
    		classList.add( record.get( INT_CLASS_INDEX ) );
    	}
    	result.add( attributeList );
    	result.add( classList );
    	return result;
    }

    private double testAccuracy( HashSet< ArrayList<String> > targetSet ) {
    	int sum = targetSet.size();
    	int hitCount = 0;
    	for ( ArrayList<String> record: targetSet ) {
    		String predictClass = getPredictClass( record, rootNode );
    		if ( predictClass.equals( record.get( INT_CLASS_INDEX ) ) ) hitCount++;
    	}
    	return (double) hitCount / (double) sum;
    }

    private String getPredictClass( ArrayList<String> record, BinTree rootNode ) {
    	BinTree currentNode = rootNode;
    	if ( currentNode == null ) return STRING_DEFAULT_CLASS;
    	String predictClass = STRING_DEFAULT_CLASS;
    	while ( currentNode != null ) {
    		int nodeAttrIndex = currentNode.nodeAttrIndex;
    		predictClass = currentNode.majorClass;
    		if ( currentNode.nodeType == CONTINUOUS ) {
    			if ( Integer.parseInt(record.get( nodeAttrIndex )) <= currentNode.continuousSplit ) currentNode = currentNode.yesBranch;
    			else currentNode = currentNode.noBranch;
    		} else {
    			if ( currentNode.nodeInfo.contains( record.get( nodeAttrIndex ) ) ) currentNode = currentNode.yesBranch;
    			else currentNode = currentNode.noBranch;
    		}
    	}
    	return predictClass;
    }

    /**
     * Revise the tree
     */
    private void reviseTree( BinTree parentNode ) {
    	BinTree leftChildNode = parentNode.yesBranch;
    	BinTree rightChildNode = parentNode.noBranch;
    	if ( leftChildNode != null && leftChildNode.yesBranch == null ) {
    		leftChildNode.setNodeType( LEAF );
    		HashSet<String> newNodeInfo = new HashSet<String>();
    		newNodeInfo.add( leftChildNode.majorClass );
    		leftChildNode.setNodeInfo( newNodeInfo );
    		parentNode.yesBranch = leftChildNode;
    	}
    	else if ( leftChildNode != null ) reviseTree( leftChildNode );
    	if ( rightChildNode != null &&rightChildNode.yesBranch == null ) {
    		rightChildNode.setNodeType( LEAF );
    		HashSet<String> newNodeInfo = new HashSet<String>();
    		newNodeInfo.add( rightChildNode.majorClass );
    		rightChildNode.setNodeInfo( newNodeInfo );
    		parentNode.noBranch = rightChildNode;
    	}
    	else if ( rightChildNode != null ) reviseTree( rightChildNode );
    }

    ///////////////////////
    // THE QUERY METHODS //
    ///////////////////////

    public void predictTestData() {
        HashSet< ArrayList<String> > testSet = readTestingFile("./data/test_parsed.txt");
        double accuracy = getAccuracyAndPrintFile( testSet );
        System.out.println( "The accuracy of the decision tree is: " + accuracy );
    }

	private HashSet< ArrayList<String> > readTestingFile ( String fileName ) {
		HashSet< ArrayList<String> > testSet = new HashSet< ArrayList<String> >();
		try {
			BufferedReader reader = new BufferedReader( new FileReader(fileName) );
			String line = null;
			boolean flag;
			ArrayList<String> record;
			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
					continue;
				}
				String[] tmp = line.split(", ");

				// Remove the last notation
				tmp[ tmp.length - 1 ] = tmp[ tmp.length - 1 ].substring( 0, tmp[ tmp.length - 1 ].length() - 1 );

				// Every record contains all attributes and the classification
				// record = new ArrayList<String>();
				record = new ArrayList<String>(Arrays.asList(tmp));
				// for (String attribute: tmp) {
				// 	record.add( attribute );
				// }

				testSet.add( record );
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testSet;
	}

	private double getAccuracyAndPrintFile( HashSet< ArrayList<String> > targetSet ) {
    	int sum = targetSet.size();
    	int hitCount = 0;

        try {
        	BufferedWriter writer = new BufferedWriter( new FileWriter("decision_tree.txt", false) );
        	for ( ArrayList<String> record: targetSet ) {
        		for ( int i = 0; i < record.size() - 1; i++ ) {
        			writer.write( record.get(i) + ", " );
        		}
	    		String predictClass = getPredictClass( record, rootNode );
	    		if ( predictClass.equals( record.get( INT_CLASS_INDEX ) ) ) {
	    			hitCount++;
	    			writer.write( "TRUE" );
	    		} else writer.write( "FALSE" );
	    		writer.newLine();
	    	}
		    writer.flush();
		    writer.close();
        } catch ( IOException e ) {
        	e.printStackTrace();
        }
    	return (double) hitCount / (double) sum;
    }

    ////////////////////////
    // THE OUTPUT METHODS //
    ////////////////////////

    /* OUTPUT BIN TREE */

    public void outputBinTree() {

        outputBinTree("1",rootNode);

        try {
		    BufferedWriter writer = new BufferedWriter( new FileWriter("decision_tree.txt", false) );
		    writeBinTree( "", rootNode.yesBranch, rootNode, true, writer );
		    writeBinTree( "", rootNode.noBranch, rootNode, false, writer );
		    writer.flush();
		    writer.close();
		} catch (IOException ex) {
		  // report
		}
     }

    private void outputBinTree(String tag, BinTree currentNode) {

        // Check for empty node

        if (currentNode == null) return;

        // Output

        System.out.println("[" + tag + "] nodeID = " + currentNode.nodeID +
        		", nodeType = " + currentNode.nodeType + ", nodeAttrIndex = " + 
        		currentNode.nodeAttrIndex + ", nodeInfo " + currentNode.nodeInfo);
        		
        // Go down yes branch

        outputBinTree(tag + ".1",currentNode.yesBranch);

        // Go down no branch

        outputBinTree(tag + ".2",currentNode.noBranch);
	}      		

	private void writeBinTree( String tag, BinTree currentNode, BinTree parentNode, boolean leftFlag, BufferedWriter writer ) throws IOException {

        // Check for empty node

        if (currentNode == null) return;

    	if ( parentNode.nodeType == CONTINUOUS ) {
    		writer.newLine();
    		if ( leftFlag == true )
    			writer.write( tag + strAttrNames[parentNode.nodeAttrIndex] + " <= " + parentNode.continuousSplit + ":" );
    		else
    			writer.write( tag + strAttrNames[parentNode.nodeAttrIndex] + " > " + parentNode.continuousSplit + ":" );
    	} else {
    		writer.newLine();
    		if ( leftFlag == true )
    			writer.write( tag + strAttrNames[parentNode.nodeAttrIndex] + " = " + parentNode.nodeInfo + ":" );
    		else
    			writer.write( tag + strAttrNames[parentNode.nodeAttrIndex] + " != " + parentNode.nodeInfo + ":" );
    	} 
    	if ( currentNode.nodeType == LEAF ) {
    		writer.write( " " + currentNode.nodeInfo.iterator().next() );
    	}
        
        // if ( currentNode == rootNode ) {
        // 	if ( leftFlag == true ) writeBinTree(tag + "|	",currentNode.yesBranch, true, writer);
        // 	else writeBinTree(tag + "|	",currentNode.noBranch, false, writer);
        // } else {
        	writeBinTree(tag + "|	",currentNode.yesBranch, currentNode, true, writer);
	        writeBinTree(tag + "|	",currentNode.noBranch, currentNode, false, writer);
        // }
	}     
}
