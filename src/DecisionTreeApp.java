///////////////////
// DECISION TREE //
///////////////////

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

class DecisionTreeApp {

    ////////////
    // FIELDS //
    ////////////

    static BufferedReader keyboardInput = new
                           BufferedReader(new InputStreamReader(System.in));
    static DecisionTree newTree;

    // The input dataset.
    // Each ArrayList in the hash set stands for an instance.
    // Replace the ArrayList with other class in the future.
    static HashSet< ArrayList<String> > dataSet;
    // The training set is extracted from the original dataset.
    static HashSet< ArrayList<String> > trainingSet;
    static HashSet< ArrayList<String> > validationSet;

    /////////////
    // METHODS //
    /////////////

    /* MAIN */

    public static void main(String[] args) throws IOException {

        String testFileName = "./data/test_parsed.txt";
        // Create instance of class DecisionTree
        if ( args.length < 1 ) newTree = new DecisionTree();
        else if ( args.length == 1 ) newTree = new DecisionTree( args[0] );
        else if ( args.length == 2 ) {
            newTree = new DecisionTree( args[0] );
            testFileName = args[1];
        }

        // Generate tree

        newTree.generateTree();

        // Output tree
        newTree.outputBinTree();
        newTree.predictTestData(testFileName);
    }
}