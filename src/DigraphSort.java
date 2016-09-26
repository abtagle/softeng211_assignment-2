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
	public static boolean isDAG = true;
	@SuppressWarnings("unchecked")
	public static void main (String[] args) throws NumberFormatException, IOException{

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
		nodes = (HashMap<Integer, Node>) originalNodes.clone();
		int max = -1;
		int temp = -1;
		for(Node node : originalNodes.values()){
			if(node.getStrata() == -1){
				temp = sort(node);
			} else{
				temp = node.getStrata();
			}
			if(temp > max){
				max = temp;
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
		}
		System.out.println(stratificaton.size());
		printStratification(stratificaton, nodes);
	}
	private static int sort(Node x){
		if(x.isSet()){
			isDAG = false;
			throw new InvalidInputException(x);
		}
		x.setFlag();
		int max = -1;
		int temp = -1;
		for(Node y : x.getSources()){
			if(y.getStrata() == -1){
				try{
					temp = sort(y);
				}catch (InvalidInputException e){
					if(e.getNode() != x){
						e.getNode().merge(x);
						throw new InvalidInputException(e.getNode());
					} else{
						x.removeSource(x);
					}
				}
				if(temp > max){
					max = temp;
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
					nodes.get(node).printCollapse();
					System.out.println();
				} else{		
					System.out.println("" + node);
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
		int maxStrata = strata;
		if(_isCollapsed){
			for(int i = 1; i < _collapsed.size(); i++){
				int currentStrata = DigraphSort.originalNodes.get(_collapsed.get(i)).getStrata();
				if(DigraphSort.originalNodes.get(_collapsed.get(i)).getStrata() > maxStrata){
					maxStrata = DigraphSort.originalNodes.get(_collapsed.get(i)).getStrata();
				}
			}
			for(int i = 1; i < _collapsed.size(); i++){
				DigraphSort.originalNodes.get(_collapsed.get(i)).setStrata(maxStrata);
			}
		} 
		_strata = strata;
	}
	public void removeFlag(){
		_flagSet = false;
	}
	public int getKey(){
		return _key;
	}

	public boolean isCollapsed(){
		return _isCollapsed;
	}
	public void printCollapse(){
		Collections.sort(_collapsed);
		for(int i : _collapsed){
			System.out.print(i+" ");
		}
	}
	public void merge(Node n){
		this._isCollapsed = true;
		//Remove the node collapsing onto this
		DigraphSort.nodes.remove(n.getKey());
		//this._sources.remove(n);
		//this._sources.addAll(n.getSources());
		this._collapsed.add(n.getKey());
		//Update in nodes
		DigraphSort.nodes.remove(this._key);
		//Replace this node's key if the collapsed node is shorter
		if(n.getKey() < this._key){
			this._key = n.getKey();
		}
		DigraphSort.nodes.put(this._key, this);
	}

}

@SuppressWarnings("serial")
class InvalidInputException extends RuntimeException{
	Node _invokingNode;
	InvalidInputException(Node n){
		_invokingNode = n;
	}

	public Node getNode(){
		return _invokingNode;
	}
}
