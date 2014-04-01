// DECISION TREE
// Borrowed the structure from
// Frans Coenen
// Department of Computer Science, University of Liverpool
// http://cgi.csc.liv.ac.uk/~frans/OldLectures/COMP101/AdditionalStuff/javaDecTree.html
// 
// 

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

class DecisionTree {

    /* ------------------------------- */
    /*                                 */
    /*              FIELDS             */
    /*                                 */
    /* ------------------------------- */

    // The input dataset.
	// Each ArrayList in the hash set stands for an instance.
	// Replace the ArrayList with other class in the future.
	private HashSet< ArrayList<String> > dataSet;
	// The training set is extracted from the original dataset.
	private HashSet< ArrayList<String> > trainingSet;

    /* NESTED CLASS */

    private class BinTree {
    	
		/* FIELDS */
	
		private int     nodeID;
    	private String  questOrAns = null;
    	private BinTree yesBranch  = null;
    	private BinTree noBranch   = null;
	
		/* CONSTRUCTOR */
	
		public BinTree(int newNodeID, String newQuestAns) {
	    	nodeID     = newNodeID;
	    	questOrAns = newQuestAns;
    	}
	}

    /* OTHER FIELDS */

    static BufferedReader    keyboardInput = new
                           BufferedReader(new InputStreamReader(System.in));
    BinTree rootNode = null;

    /* ------------------------------------ */
    /*                                      */
    /*              CONSTRUCTORS            */
    /*                                      */
    /* ------------------------------------ */

    /* Default Constructor */
    public DecisionTree() {
    	readTrainingFile ();
	}

	// Read the training data file into the hash set.
	// See reference to the ParseData class.
	public void readTrainingFile () {
	}

	// Divide the original training set into two parts.
	public void divideTrainingSet () {

	}

	/* ----------------------------------------------- */
    /*                                                 */
    /*                     GINI Part                   */
    /*                                                 */
    /* ----------------------------------------------- */

    // Calculate the GINI value of the input attribute.
	// The input should be two ArrayList.
	// attribute_array stores the training instances' values of the selected attribute.
	// label_array stores all the instances' labels.
	// Returns the GINI value of the selected attribute.
	public double GINI ( ArrayList<String> attribute_array, ArrayList<String> label_array, int type ) {
		int nAttributesNum;
		int nAttr1Num, nAttr2Num;
		
		nAttributesNum = attribute_array.size();
		
		for (String s : attribute_array) {
	        System.out.println(s);
	    }
		
		return 0.0;
	}

    /* ----------------------------------------------- */
    /*                                                 */
    /*               TREE BUILDING METHODS             */
    /*                                                 */
    /* ----------------------------------------------- */

    /* CREATE ROOT NODE */

    public void createRoot(int newNodeID, String newQuestAns) {
		rootNode = new BinTree(newNodeID,newQuestAns);	
		System.out.println("Created root node " + newNodeID);	
	}
			
    /* ADD YES NODE */

    public void addYesNode(int existingNodeID, int newNodeID, String newQuestAns) {
	// If no root node do nothing
	
		if (rootNode == null) {
	   	 	System.out.println("ERROR: No root node!");
	    	return;
	    }
	
		// Search tree
	
		if (searchTreeAndAddYesNode(rootNode,existingNodeID,newNodeID,newQuestAns)) {
	    	System.out.println("Added node " + newNodeID +
	    		" onto \"yes\" branch of node " + existingNodeID);
	    }
		else System.out.println("Node " + existingNodeID + " not found");
	}

    /* SEARCH TREE AND ADD YES NODE */

    private boolean searchTreeAndAddYesNode(BinTree currentNode,
    			int existingNodeID, int newNodeID, String newQuestAns) {
    	if (currentNode.nodeID == existingNodeID) {
	    	// Found node
	    	if (currentNode.yesBranch == null) currentNode.yesBranch = new
	    			BinTree(newNodeID,newQuestAns);
	    	else {
	        	System.out.println("WARNING: Overwriting previous node " +
					"(id = " + currentNode.yesBranch.nodeID +
					") linked to yes branch of node " +
					existingNodeID);
				currentNode.yesBranch = new BinTree(newNodeID,newQuestAns);
			}		
    	    return(true);
	    }
		else {
	    	// Try yes branch if it exists
	    	if (currentNode.yesBranch != null) { 	
	        	if (searchTreeAndAddYesNode(currentNode.yesBranch,
		        	existingNodeID,newNodeID,newQuestAns)) {    	
	            	return(true);
		    	}	
				else {
    	        	// Try no branch if it exists
	    	    	if (currentNode.noBranch != null) {
    	    			return(searchTreeAndAddYesNode(currentNode.noBranch,
						existingNodeID,newNodeID,newQuestAns));
					}
		    	else return(false);	// Not found here
		    	}
    		}
	    	return(false);		// Not found here
	    }
   	} 	
    		
    /* ADD NO NODE */

    public void addNoNode(int existingNodeID, int newNodeID, String newQuestAns) {
		// If no root node do nothing
	
		if (rootNode == null) {
	    	System.out.println("ERROR: No root node!");
	    	return;
	    }
	
		// Search tree
	
		if (searchTreeAndAddNoNode(rootNode,existingNodeID,newNodeID,newQuestAns)) {
	    	System.out.println("Added node " + newNodeID +
	    		" onto \"no\" branch of node " + existingNodeID);
	    }
		else System.out.println("Node " + existingNodeID + " not found");
	}
	
    /* SEARCH TREE AND ADD NO NODE */

    private boolean searchTreeAndAddNoNode(BinTree currentNode,
    			int existingNodeID, int newNodeID, String newQuestAns) {
    	if (currentNode.nodeID == existingNodeID) {
	    	// Found node
	    	if (currentNode.noBranch == null) currentNode.noBranch = new
	    		BinTree(newNodeID,newQuestAns);
	    	else {
	        	System.out.println("WARNING: Overwriting previous node " +
				"(id = " + currentNode.noBranch.nodeID +
				") linked to yes branch of node " +
				existingNodeID);
			currentNode.noBranch = new BinTree(newNodeID,newQuestAns);
			}		
    	    return(true);
	    }
		else {
	   		// Try yes branch if it exists
	    	if (currentNode.yesBranch != null) { 	
	        	if (searchTreeAndAddNoNode(currentNode.yesBranch,
		        	existingNodeID,newNodeID,newQuestAns)) {    	
	            	return(true);
		    	}	
			else {
    	        // Try no branch if it exists
	    	    if (currentNode.noBranch != null) {
    	    		return(searchTreeAndAddNoNode(currentNode.noBranch,
						existingNodeID,newNodeID,newQuestAns));
				}
		    	else return(false);	// Not found here
		    }
			}
	    	else return(false);	// Not found here
	    }
   	} 	

    /* --------------------------------------------- */
    /*                                               */
    /*               TREE QUERY METHODS             */
    /*                                               */
    /* --------------------------------------------- */

    public void queryBinTree() throws IOException {
        queryBinTree(rootNode);
    }

    private void queryBinTree(BinTree currentNode) throws IOException {

        // Test for leaf node (answer) and missing branches

        if (currentNode.yesBranch==null) {
            if (currentNode.noBranch==null) System.out.println(currentNode.questOrAns);
            else System.out.println("Error: Missing \"Yes\" branch at \"" +
            		currentNode.questOrAns + "\" question");
            return;
        }
        if (currentNode.noBranch==null) {
            System.out.println("Error: Missing \"No\" branch at \"" +
            		currentNode.questOrAns + "\" question");
            return;
        }

        // Question

        askQuestion(currentNode);
    }

    private void askQuestion(BinTree currentNode) throws IOException {

        System.out.println(currentNode.questOrAns + " (enter \"Yes\" or \"No\")");
        String answer = keyboardInput.readLine();
        if (answer.equals("Yes")) queryBinTree(currentNode.yesBranch);
        else {
            if (answer.equals("No")) queryBinTree(currentNode.noBranch);
            else {
                System.out.println("ERROR: Must answer \"Yes\" or \"No\"");
                askQuestion(currentNode);
            }
        }
    }

    /* ----------------------------------------------- */
    /*                                                 */
    /*               TREE OUTPUT METHODS               */
    /*                                                 */
    /* ----------------------------------------------- */

    /* OUTPUT BIN TREE */

    public void outputBinTree() {

        outputBinTree("1",rootNode);
     }

    private void outputBinTree(String tag, BinTree currentNode) {

        // Check for empty node

        if (currentNode == null) return;

        // Output

        System.out.println("[" + tag + "] nodeID = " + currentNode.nodeID +
        		", question/answer = " + currentNode.questOrAns);
        		
        // Go down yes branch

        outputBinTree(tag + ".1",currentNode.yesBranch);

        // Go down no branch

        outputBinTree(tag + ".2",currentNode.noBranch);
	}      		
}
