import java.awt.*;
import javax.swing.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.renderers.*;

class Main{
	public static void main(String[] args){
		SimpleGraphView sgv = new SimpleGraphView(); //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<Integer, String> layout = new CircleLayout(sgv.g);
		layout.setSize(new Dimension(300,300)); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<Integer,String> vv =
                        new BasicVisualizationServer<Integer,String>(layout);
		vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
                //RenderContext rc = vv.getRenderContext();
                //BasicEdgeLabelRenderer ll = new BasicEdgeLabelRenderer();
                //ll.prepareRenderer(rc, null, ll, true, sgv)
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

}

class SimpleGraphView{
	public DirectedSparseGraph<EdmondsVertex,EdmondsEdge> g;
	public SimpleGraphView(){
		g = new DirectedSparseGraph<EdmondsVertex,EdmondsEdge>();
                EdmondsVertex[] verticies = new EdmondsVertex[7];
                verticies[0] = new EdmondsVertex("A");
                verticies[1] = new EdmondsVertex("B");
                verticies[2] = new EdmondsVertex("C");
                verticies[3] = new EdmondsVertex("D");
                verticies[4] = new EdmondsVertex("E");
                verticies[5] = new EdmondsVertex("F");
                verticies[6] = new EdmondsVertex("G");
                for (int i=0; i < verticies.length; i++)
                    g.addVertex(verticies[i]);
                g.addEdge(new EdmondsEdge(3), verticies[0], verticies[3]);
                g.addEdge(new EdmondsEdge(3), verticies[0], verticies[1]);
                g.addEdge(new EdmondsEdge(6), verticies[3], verticies[5]);
                g.addEdge(new EdmondsEdge(3), verticies[2], verticies[0]);
                g.addEdge(new EdmondsEdge(1), verticies[2], verticies[3]);
                g.addEdge(new EdmondsEdge(2), verticies[2], verticies[5]);
                g.addEdge(new EdmondsEdge(4), verticies[1], verticies[2]);
                g.addEdge(new EdmondsEdge(1), verticies[4], verticies[1]);
                g.addEdge(new EdmondsEdge(2), verticies[3], verticies[5]);
                g.addEdge(new EdmondsEdge(9), verticies[5], verticies[6]);
                g.addEdge(new EdmondsEdge(1), verticies[4], verticies[6]);

                //EdmondsVertex source = new EdmondsVertex("A");
                //EdmondsVertex sink = new EdmondsVertex("B");
		//g.addVertex(source);
		//g.addVertex(sink);
		//g.addEdge(new EdmondsEdge(5), source, sink); // Note that Java 1.5 auto-boxes primitives
                EdmondsKarp k = new EdmondsKarp(g);
                k.maxFlow();
	}
}
