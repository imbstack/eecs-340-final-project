import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class EdmondsKarp {

	DirectedSparseGraph<EdmondsVertex,EdmondsEdge> f;
        EdmondsVertex source, sink;

	EdmondsKarp(DirectedSparseGraph<EdmondsVertex, EdmondsEdge> f) {
		this.f = f;

                // this.source = source;
                // this.sink = sink;
	}

        public int maxFlow(EdmondsVertex source, EdmondsVertex sink) {
            int flow = 0;
            Collection<EdmondsEdge> edges = f.getEdges();
            // Sets the initial flow to zero for all edges
            Iterator<EdmondsEdge> i = edges.iterator();
            while (i.hasNext()) {
                i.next().setNewFlow(0);
            }

            EdmondsEdge[] path = findPath(source,sink);
            while (path.length != 0) {
                for( int j = 0; j < path.length; j++) {
                    
                }
                path = findPath(source,sink);
            }
            Collection<EdmondsVertex> vertices;
            return 1;
        }
        /**
         * Finds a path with remaining capacity between s and t.
         * Returns an empty array of EdmondsEdges if there is no remaining capacity.
         * @param s Source node
         * @param t Destination node
         */
        private EdmondsEdge[] findPath(EdmondsVertex s, EdmondsVertex t) {

        }
        //private int capacity(EdmondsVertex a, EdmondsVertex b) {

        //}
}
