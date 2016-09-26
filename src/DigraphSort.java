import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class DigraphSort {
	public static HashMap<Integer, Node>  originalNodes;
	public static HashMap<Integer, Node> nodes;
	public static void main (String[] args) throws NumberFormatException, IOException{
		boolean isDAG = true;
		//Read in number of arcs
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int arcNumber = Integer.parseInt(in.readLine().split("\\s+")[0]);
		//Read the arcs into HashMap

		String[] line;
		originalNodes = new HashMap<Integer, Node>();
		for(int i = 0; i < arcNumber; i++){
			line = in.readLine().split("\\s+");
			readArc(line, originalNodes);
		}
		@SuppressWarnings("unchecked")
		HashMap<Integer, Node> nodes = (HashMap<Integer, Node>) originalNodes.clone();
		int temp = -1;
		int max = -1;
		for(Node node : nodes.values()){
			if(node.getStrata() == -1){
				try {
					temp = sort(node);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					isDAG = false;
				}
				if(!isDAG){
					break;
				}
				if (temp > max){
					max = temp;
				}
			}
		}
		int numberOfStrata = max;
		if(isDAG){
			System.out.println("DAG");
		} else{
			System.out.println("nonDAG");
		}
		ArrayList<ArrayList<Integer>> stratificaton = new ArrayList<ArrayList<Integer>>();
		for (int j = 0; j <= numberOfStrata; j++){
			stratificaton.add(new ArrayList<Integer>());
		}
		for(int nodeNumber : nodes.keySet()){
			//add the node to the stratification ArrayList
			stratificaton.get(nodes.get(nodeNumber).getStrata()).add(nodeNumber);
			;			}
		System.out.println("DAG");
		System.out.println(stratificaton.size());
		printStratification(stratificaton, nodes);
	}
	private static int sort(Node x) throws Exception{
		if(x.isSet()){
			throw new InvalidInputException(x);
		}	
		x.setFlag();
		int max = -1;
		int temp;
		for(Node y : x.getSources()){
			if(y.getStrata() == -1){
				try{
					temp = sort(y);if(temp > max){
						max = temp;
					}
				} catch (InvalidInputException e){
					if(e.getNode() != y){
						e.getNode().merge(y);
						throw new InvalidInputException(e.getNode());
					} else{
						y.removeSource(y);
					}
				}
			} else{
				temp = y.getStrata();
				if(temp > max){
					max = temp;
				}
			}	
		}
		x.removeFlag();
		x.setStrata(max+1);
		return max+1;
	}
	private static void readArc(String[] line, HashMap<Integer, Node> nodes){
		int source, destination;
		source = Integer.parseInt(line[0]);
		destination = Integer.parseInt(line[1]);
		//Make new node if it does not already exist
		if(!nodes.containsKey(source)){
			nodes.put(source, new Node(source));
		}
		if(!nodes.containsKey(destination)){
			nodes.put(destination, new Node(destination));
		}
		nodes.get(destination).addSource(nodes.get(source));
	}

	private static void printStratification(ArrayList<ArrayList<Integer>> stratification, HashMap<Integer ,Node> nodes){
		for(ArrayList<Integer> strata : stratification){
			Collections.sort(strata);
			System.out.println(strata.size());
			for(int node : strata){
				if(nodes.get(node).isCollapsed()){

				} else{
					nodes.get(node).print();
				}
			}
		}
	}
}
class Node{
	private int _key;
	private HashSet<Node> _sources;
	private boolean _flagSet;
	private int _strata;
	private boolean _isCollapsed = false;
	private ArrayList<Integer> _collapsed;
	public Node(int key){
		_key = key;
		_sources = new HashSet<Node>();
		_collapsed = new ArrayList<Integer>();
		_collapsed.add(this._key);
		_flagSet = false;
		_strata = -1;
	}
	public void addSource(Node n){
		_sources.add(n);
	}

	public HashSet<Node> getSources(){
		return _sources;
	}

	public void removeSource(Node n){
		_sources.remove(n);
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
	public void print(){
		System.out.println(_key);
	}
	public int getKey(){
		return _key;
	}

	public boolean isCollapsed(){
		return _isCollapsed;
	}
	public void printCollapse(){
		Collections.sort(_collapsed);
	}
	public void merge(Node n){
		this._isCollapsed = true;
		//Remove the node collapsing onto this
		DigraphSort.nodes.remove(n);
		this._sources.remove(n);
		this._sources.addAll(n.getSources());
		this._collapsed.add(n.getKey());
		//Replace this node's key if the collapsed node is shorter
		if(n.getKey() < this._key){
			this._key = n.getKey();
		}
		//Update in nodes
		DigraphSort.nodes.remove(this);
		DigraphSort.nodes.put(this._key, this);
	}

}

class InvalidInputException extends RuntimeException{
	Node _invokingNode;
	InvalidInputException(Node n){
		_invokingNode = n;
	}

	public Node getNode(){
		return _invokingNode;
	}
}
