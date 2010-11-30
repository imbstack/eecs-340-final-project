import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedList;

public class EdmondsKarp {

	EdmondsVertex currentV;
	long capacity;
	DirectedSparseGraph<EdmondsVertex,EdmondsEdge> f;
	EdmondsVertex source, sink;

	EdmondsKarp(DirectedSparseGraph<EdmondsVertex, EdmondsEdge> f) {
		this.f = f;
		for (EdmondsEdge i: f.getEdges()) {
			i.setNewFlow(0);
		}
	}

	public int maxFlow(EdmondsVertex source, EdmondsVertex sink, boolean runFull) {


		// While there is a path with available capacity...
		capacity = findPath(source,sink);
		System.out.println("Found path of capacity " + capacity + ". It's " + returnPath(sink));
		if (runFull){
			while (capacity > 0) {
				// Travel backwards from the sink, adjusting the node capacities
				//  as we go back.
				currentV = sink;
				while (currentV != source) {
					f.findEdge(currentV.parentNode, currentV).addFlow((int)capacity);
					currentV = currentV.parentNode;
				}
				capacity = findPath(source,sink);
				if (capacity > 0){
					System.out.println("Found path of capacity " + capacity + ". It's " + returnPath(sink));
				}
				else{
					System.out.println("Finished");
				}
				//System.out.println(capacity);
			}
		}
		else{
			if (capacity > 0){
				currentV = sink;
				while (currentV != source) {
					f.findEdge(currentV.parentNode, currentV).addFlow((int)capacity);
					currentV = currentV.parentNode;
				}
				capacity = findPath(source,sink);
				if (capacity > 0){
					System.out.println("Found path of capacity " + capacity + ". It's " + returnPath(sink));
				}
				else{
					System.out.println("Finished");
				}
				//System.out.println(capacity);
			}
		}
		Collection<EdmondsVertex> vertices;
		return 1;
	}
	/**
	 * Finds a path with remaining capacity between s and t.
	 * Returns the capacity of the path it has found. Also, this method
	 * stores the path in the EdmondsVertex instances so it is possible
	 * to work backwards using EdmondsVertex.parentNode from the sink.
	 * @param s Source node
	 * @param t Destination node
	 */
	private long findPath(EdmondsVertex s, EdmondsVertex t) {
		// First, reset the Verticies to undiscovered
		for(EdmondsVertex i: f.getVertices()) {
			i.discoveredState = -1;
			i.parentNode = null;
		}
		s.discoveredState = -2;
		// Use a LinkedList as a Queue for finding verticies
		LinkedList<EdmondsVertex> discoveredV = new LinkedList<EdmondsVertex>();
		// Keep an array of distances from the source
		//
		discoveredV.add(s);

		while (discoveredV.size() > 0) {
			EdmondsVertex currentV = discoveredV.removeFirst();
			for (EdmondsVertex adjV: f.getSuccessors(currentV)) {
				EdmondsEdge connection = f.findEdge(currentV, adjV);
				if (adjV.discoveredState == -1 && (connection.getRemainingCapacity() > 0)) {
					adjV.parentNode = currentV;
					adjV.discoveredState = 1; // To prevent cycles
					adjV.pathCapacityToNode = Math.min(currentV.pathCapacityToNode, connection.getRemainingCapacity());
					if (adjV != t) {
						discoveredV.add(adjV);
					} else {
						return adjV.pathCapacityToNode;
					}
				}
			}
		}
		return 0;
	}
	// Follows the path of pointers of parentNode and prints the 
	//  name of each node along the way.
	// If you pass this a null reference, I will hunt you down.
	public String returnPath(EdmondsVertex dest) {
		String end = "";
		EdmondsVertex current = dest;
		while (current != null) {
			if (end == ""){
				end = current.name + "}";
			}
			else{
				end = current.name + " → " + end;
			}
				current = current.parentNode;
		}
		return "{" + end;
	}
	//private int capacity(EdmondsVertex a, EdmondsVertex b) {

	//}
}
