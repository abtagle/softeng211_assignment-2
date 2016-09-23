import java.util.TreeSet;

public class DigraphSort {

	public static void main (String[] args){
			
	}
	private int sort(Node x){
		if(x.isSet()){
		}	
		x.setFlag();
		int max = -1;
		for(){

		}
		x.removeFlag();
	}
}
class Node{
	private TreeSet<Node> _destinations;
	private boolean _flagSet;
	public Node(Node destination){
		_destinations.add(destination);
		_flagSet = false;
	}

	public boolean isSet(){
		return _flagSet;
	}

	public void setFlag(){
		_flagSet = true;
	}

	public void removeFlag(){
		_flagSet = false;
	}
}