import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class DigraphSort {

	public static void main (String[] args) throws NumberFormatException, IOException{
		boolean isDAG = true;
		//Read in number of arcs
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int arcNumber = Integer.parseInt(in.readLine().split("\\s+")[0]);
		//Read the arcs into HashMap

		String[] line;
		HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
		for(int i = 0; i < arcNumber; i++){
			line = in.readLine().split("\\s+");
			readArc(line, nodes);
		}
		int temp = -1;
		int max = -1;
		for(Node node : nodes.values()){
			if(node.getStrata() == -1){
				try {
					temp = sort(node);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("nonDAG");
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
			printStratification(stratificaton);
		}
	}
	private static int sort(Node x) throws Exception{
		if(x.isSet()){
			throw new IllegalArgumentException();
		}	
		x.setFlag();
		int max = -1;
		int temp;
		for(Node y : x.getSources()){
			if(y.getStrata() == -1){
				temp = sort(y);
			} else{
				temp = y.getStrata();
			}
			if(temp > max){
				max = temp;
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
			nodes.put(source, new Node());
		}
		if(!nodes.containsKey(destination)){
			nodes.put(destination, new Node());
		}
		nodes.get(source).addDestination(nodes.get(destination));
		nodes.get(destination).addSource(nodes.get(source));
	}
	
	private static void printStratification(ArrayList<ArrayList<Integer>> stratification){
		for(ArrayList<Integer> strata : stratification){
			Collections.sort(strata);
			System.out.println(strata.size());
			for(int node : strata){
				System.out.println(node);
			}
		}
	}
}
class Node{
	protected HashSet<Node> _sources;
	protected HashSet<Node> _destinations;
	protected boolean _flagSet;
	protected int _strata;
	public Node(){
		_sources = new HashSet<Node>();
		_destinations = new HashSet<Node>();
		_flagSet = false;
		_strata = -1;
	}
	public void addSource(Node n){
		_sources.add(n);
	}

	public HashSet<Node> getSources(){
		return _sources;
	}
	public void addDestination(Node n){
		_destinations.add(n);
	}
	public HashSet<Node> getDestinations(){
		return _destinations;
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

class CollapsedNode extends Node{
	private ArrayList<Node> _stronglyConnectedNodes;
	
	public CollapsedNode(){
		super();
		_stronglyConnectedNodes = new ArrayList<Node>();
	}
	
	public void addNode(Node n){
		_stronglyConnectedNodes.add(n);
		_sources.addAll(n.getSources());
		_destinations.addAll(n.getDestinations());
	}
}