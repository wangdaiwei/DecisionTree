import java.util.ArrayList;


public class Result {

	private double GiniValue;
	private ArrayList<String> alMinLeftChild; 
	private ArrayList<String> alMinRightChild;
	
	public double getGiniValue() {
		return GiniValue;
	}
	public void setGiniValue(double giniValue) {
		GiniValue = giniValue;
	}
	public ArrayList<String> getAlMinLeftChild() {
		return alMinLeftChild;
	}
	public void setAlMinLeftChild(ArrayList<String> alMinLeftChild) {
		this.alMinLeftChild = alMinLeftChild;
	}
	public ArrayList<String> getAlMinRightChild() {
		return alMinRightChild;
	}
	public void setAlMinRightChild(ArrayList<String> alMinRightChild) {
		this.alMinRightChild = alMinRightChild;
	}
	
}
