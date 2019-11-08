package implication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import sat.env.Bool;
import sat.env.Environment;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

public class Graph {
	Map<String, Vertex> vertexIdMap = new HashMap<String, Vertex>();
	public Environment env = new Environment();

	public class Vertex {
		public Literal id;
		public List<Vertex> children = new ArrayList<Vertex>();
		public List<Vertex> parent = new ArrayList<Vertex>();

		Vertex(Literal id) {
			this.id = id;
		}
	}
	
	//initialising a vertex
	private Vertex addVertex(Literal l) {
		if (!(vertexIdMap.containsKey(l.toString()))) {
			Vertex v = new Vertex(l);
			vertexIdMap.put(l.toString(), v);
			return v;
		} else
			return vertexIdMap.get(l.toString());
	}

	//initialising an edge (via children and parent lists)
	private void addEdge(Vertex from, Vertex to) {
		from.children.add(to);
		to.parent.add(from);
	}

	//uses addVertex and addEdge to create implication from any clause
	public void addImplication(Literal l1, Literal l2) {
		Vertex v1 = addVertex(l1);
		Vertex v2 = addVertex(l2);
		Vertex v1n = addVertex(l1.getNegation());
		Vertex v2n = addVertex(l2.getNegation());

		addEdge(v1n, v2);
		addEdge(v2n, v1);
	}

	//first runthrough of dfs
	private void dfs1(Vertex v, HashSet<Vertex> visited, Stack<Vertex> fringe) {
		visited.add(v);
		for(Vertex child: v.children) {
			if(!(visited.contains(child))&&!child.equals(v)) {
				dfs1(child,visited,fringe);
			}
		}
		fringe.push(v);
	}
	
	//second runthrough of dfs in tranposed direction
	private void dfs2(Vertex v, HashSet<Vertex> visited, List compList) {
		visited.add(v);
		compList.add(v.id);
		for(Vertex parent: v.parent) {
			//System.out.println("parent: " + parent.id.toString() + " v: " + v.id.toString());
			if(!(visited.contains(parent))&&!parent.equals(v)){
				dfs2(parent,visited,compList);
			}
		}
	}
	
	//returns True if UNSAT
	public boolean negInComp(LinkedList<Literal> compList) {
		HashSet<Literal> inside = new HashSet<Literal>();
		for(Literal l: compList) {
			if(inside.contains(l.getNegation())) return true;
			else inside.add(l);
		}
		return false;
	}
	//returns False if UNSAT
	public boolean checkSat() {
		Stack<Vertex> fringe = new Stack();
		HashSet<Vertex> visited = new HashSet();
		
		//first runthrough of dfs
		for (Map.Entry<String, Vertex> e : this.vertexIdMap.entrySet()) {
			Vertex vertex = e.getValue();
			if(!visited.contains(vertex)) {
				dfs1(vertex,visited,fringe);
			}
		}
		
		//second runthrough of dfs
		visited = new HashSet();
		Stack<LinkedList> graphList = new Stack();
		while(!fringe.isEmpty()) {
			LinkedList<Literal> compList = new LinkedList<Literal>();
			Vertex v = fringe.pop();
			if(!visited.contains(v)) {
				dfs2(v,visited,compList);
				if(negInComp(compList)) {
					return false;
				}
				graphList.push(compList);
			}
		}
//		Stack<LinkedList> copyGraph = (Stack<LinkedList>) graphList.clone();
//		while(!copyGraph.isEmpty()) {
//			System.out.println(copyGraph.pop());
//		}
		
		//if SAT, set True of components from reverse topo order
		while(!graphList.isEmpty()) {
			LinkedList<Literal> li = graphList.pop();
			for(Literal l: li) {
				if(env.get(l.getVariable())==Bool.UNDEFINED) {
					if(l instanceof NegLiteral) env = env.putFalse(l.getVariable());
					else if(l instanceof PosLiteral) env = env.putTrue(l.getVariable());
				}
			}
		}
		return true;
	}
}
