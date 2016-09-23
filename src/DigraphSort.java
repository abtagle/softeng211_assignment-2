import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class DigraphSort {

	public static void main (String[] args) throws NumberFormatException, IOException{
		//Read in number of arcs
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int arcNumber = Integer.parseInt(in.readLine().split("\\s+")[0]);
		//Read the arcs into HashSet
		int source, destination;
		String[] line;
		HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
		for(int i = 0; i < arcNumber; i++){
			line = in.readLine().split("\\s+");
			source = Integer.parseInt(line[0]);
			destination = Integer.parseInt(line[1]);
			//Make new node if it does not already exist
			if(!nodes.containsKey(source)){
				nodes.put(source, new Node());
			}
			if(!nodes.containsKey(destination)){
				nodes.put(source, new Node());
			}
			nodes.get(source).addDestination(nodes.get(destination));
			nodes.get(destination).addDestination(nodes.get(source));
		}
	}
	private int sort(Node x, int rd) throws Exception{
		if(x.isSet()){
			throw new Exception("Invalid input at this stage- not a DAG");
		}	
		x.setFlag();
		int max = -1;
		int temp;
		for(Node y : x.getSources()){
			temp = sort(y, rd+1);
			if(temp > max){
				max = temp;
			}
		}
		x.removeFlag();
		x.setStrata(max);
		return max;
	}
}
class Node{
	private int _nodeNumber;
	private ArrayList<Node> _sources;
	private ArrayList<Node> _destinations;
	private boolean _flagSet;
	private int _strata;
	public Node(){
		_sources = new ArrayList<Node>();
		_destinations = new ArrayList<Node>();
		_flagSet = false;
	}
	public void addSource(Node n){
		_sources.add(n);
	}

	public ArrayList<Node> getSources(){
		return _sources;
	}
	public void addDestination(Node n){
		_destinations.add(n);
	}
	public boolean isSet(){
		return _flagSet;
	}

	public void setFlag(){
		_flagSet = true;
	}
	public int getStrata(){
		return _strata;
	}
	public void setStrata(int strata){
		_strata = strata;
	}
	public void removeFlag(){
		_flagSet = false;
	}
}