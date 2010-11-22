import java.awt.*;
import javax.swing.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.renderers.*;
import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.Factory;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.HashSet;
import edu.uci.ics.jung.algorithms.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.visualization.control.*;

class Main{
	public static void main(String[] args){
		SimpleGraphView sgv = new SimpleGraphView(); //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<EdmondsVertex, EdmondsEdge> layout = new FRLayout2(sgv.g);
		layout.setSize(new Dimension(1024,768)); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		VisualizationViewer<EdmondsVertex, EdmondsEdge> vv =
			new VisualizationViewer<EdmondsVertex, EdmondsEdge>(layout);
		vv.setPreferredSize(new Dimension(1024,768)); //Sets the viewing area size
		Color slate = new Color(25,25,35);
		Transformer<EdmondsVertex,Paint> vertexPaint = new Transformer<EdmondsVertex,Paint>() {
			public Paint transform(EdmondsVertex i) {
				Random R = new Random();
				Color c = new Color(R.nextInt(255), R.nextInt(255), R.nextInt(255));
				return c;
			}
		}; 
		Transformer<EdmondsEdge,Paint> edgePaint = new Transformer<EdmondsEdge,Paint>() {
			public Paint transform(EdmondsEdge i) {
				return Color.white;
			}
		}; 
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
		vv.getRenderContext().setArrowFillPaintTransformer(edgePaint);
		vv.setBackground(slate);
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm); 
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setLocation(200,200);
		frame.setVisible(true);
	}

}

class SimpleGraphView{
	public DirectedSparseGraph<EdmondsVertex, EdmondsEdge> g;
	public DirectedGraph<EdmondsVertex, EdmondsEdge> ed;
	private Transformer<EdmondsEdge, Integer> trans;
	private Map<EdmondsEdge, Double> cart;
	private Factory<EdmondsEdge> edgeFact;
	private Factory<EdmondsVertex> vertFact;
	private Factory<DirectedGraph<EdmondsVertex, EdmondsEdge>> graphFact;
	//Fix the random thing soon
	private Random R = new Random();
	public SimpleGraphView(){

		cart = new HashMap<EdmondsEdge, Double>();

		trans = new Transformer<EdmondsEdge, Integer>(){
			public Integer transform(EdmondsEdge link){
				return link.capacity;
			}
		};

		edgeFact = new Factory<EdmondsEdge>(){
			public EdmondsEdge create(){
				return new EdmondsEdge(R.nextInt());
			}
		};

		vertFact = new Factory<EdmondsVertex>(){
			public EdmondsVertex create(){
				return new EdmondsVertex();
			}
		};

		graphFact = new Factory<DirectedGraph<EdmondsVertex, EdmondsEdge>>(){
			public DirectedGraph<EdmondsVertex, EdmondsEdge> create(){
				return new DirectedSparseGraph<EdmondsVertex, EdmondsEdge>();
			}
		};

		EppsteinPowerLawGenerator eplg = new EppsteinPowerLawGenerator(graphFact, vertFact, edgeFact, 80, 240, 100);
		g = (DirectedSparseGraph)eplg.create();
		//g.addVertex(new EdmondsVertex());
		//g.addVertex(new EdmondsVertex);
		//g.addEdge("ONE", 1,2);

		//TEST EDMONDS-KARP

		//EdmondsKarpMaxFlow maxFlow = new EdmondsKarpMaxFlow(g, 1, 2, trans, cart, edgeFact);
		//maxFlow.evaluate();
		//ed = maxFlow.getFlowGraph();
		//System.out.println("---------------------------------------------" + maxFlow.getMaxFlow());
	}
}

