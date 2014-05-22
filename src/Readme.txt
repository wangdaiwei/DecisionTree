This txt explains how to use DecisionTreeApp program.

Building The Example
========
1. Before you begin, you will need to install JDK.
JDK Home Page: http://www.oracle.com/technetwork/java/javase/downloads/index.html?ssSourceSiteId=otnjp

2. Configure the java library path if necessary.
3. Open a command line tool and change the current directory into the DecisionTree folder.
4. Do the following:
	% javac DecisionTreeApp.java
4. Ensure the parsed data file "data_parsed.txt" and "test_parsed.txt" is located in /data folder.
5. Run the program:
	% java DecisionTreeApp
   Or with the training data file path and testing data file path:
   	% java DecisionTreeApp ./data/data_parsed.txt ./data/test_parsed.txt
6. See the result is printing from Console. The result is following:
	The accuracy of the decision tree is: 0.7547495682210709
	Error rate: 0.24525043177892913
   It will also generate a decision tree file.
   The decision tree file is in the following format:

		capital-gain <= 5013:
		|	age <= 29:
		|	|	age <= 25:
		|	|	|	hours-per-week <= 72:
		|	|	|	|	fnlwgt <= 23438:
		|	|	|	|	|	age <= 20: <=50K

	The first row is the root node, then the second row is the first layer node and so forth.
	Nodes on the same layer will have the same indent.