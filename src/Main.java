import java.awt.*;
import javax.swing.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.Factory;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.HashSet;
import java.util.ArrayList;
import edu.uci.ics.jung.algorithms.generators.Lattice2DGenerator;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.visualization.decorators.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class Main{
	static SimpleGraphView sgv;
	static Layout<EdmondsVertex, EdmondsEdge> layout;
	static VisualizationViewer<EdmondsVertex, EdmondsEdge> vv;
	static JFrame frame;
	static int graphDims;
	public static void main(String[] args){
		graphDims = 3;
		frame = new JFrame("Simple Graph View");
		ControlPanel controls = new ControlPanel();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		frame.getContentPane().add(controls);
		renderGraph();
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setLocation(200,200);
		frame.setVisible(true);
	}
	public static void renderGraph() {
		sgv = new SimpleGraphView(); //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		layout = new ISOMLayout(sgv.g);
		layout.setSize(new Dimension(1024,768)); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		vv = new VisualizationViewer<EdmondsVertex, EdmondsEdge>(layout);
		vv.setPreferredSize(new Dimension(1024,768)); //Sets the viewing area size
		final Color slate = new Color(25,25,35);
		Transformer<EdmondsVertex,Paint> vertexPaint = new Transformer<EdmondsVertex,Paint>() {
			public Paint transform(EdmondsVertex vert) {
				Color orange = new Color(155,175,151);
				if (vert.s){
					return Color.green;
				}
				else if(vert.t){
					return Color.red;
				}
				return orange;
			}
		};
		Transformer<EdmondsEdge,Paint> edgePaint = new Transformer<EdmondsEdge,Paint>() {
			public Paint transform(EdmondsEdge edge) {
				return new Color(210-(int)(210*((double)edge.getFlow()/EdmondsEdge.maxUsed)),210,210);
			}
		};
		Transformer<EdmondsEdge, Stroke> edgeStrokeTransformer = new Transformer<EdmondsEdge, Stroke>() {
			public Stroke transform(EdmondsEdge edge) {
				float wid = 6*(float)edge.capacity/EdmondsEdge.maxCapacity;
				return new BasicStroke(wid, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
		vv.getRenderContext().setArrowFillPaintTransformer(edgePaint);
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.setForeground(Color.WHITE);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.setBackground(slate);
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
        vv.revalidate();
        vv.repaint();
	}
	static class ControlPanel extends JPanel {
		JButton maxFlow;
		JButton beginMaxFlow;
		JButton step;
		JButton newGraph;
		JSlider graphSize;
		ControlPanel() {
			maxFlow = new JButton("Compute Max Flow");
			beginMaxFlow = new JButton("Begin Animation");
			step = new JButton("Step");
			newGraph = new JButton("New Random Graph");
			graphSize = new JSlider(2, 6, graphDims);

			maxFlow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sgv.performEdmondsKarp();
					//vv.update(frame.getGraphics());
					vv.repaint();
				}
			});

			beginMaxFlow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Do stuff, but slower
					//sgv.performEdmondsKarp();
				}
			});
			step.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sgv.performEdmondsStep();
					vv.repaint();
				}
			});
			newGraph.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//sgv.generateNewGraph();
                    frame.setVisible(false);
                    frame.getContentPane().remove(vv);
					renderGraph();
                    frame.getContentPane().add(vv);
                    frame.setVisible(true);
					//layout = new ISOMLayout(sgv.g);
					//vv = new VisualizationViewer<EdmondsVertex, EdmondsEdge>(layout);
					//vv.update(frame.getGraphics());
					//renderGraph();
				}
			});
			graphSize.setSnapToTicks(true);
			graphSize.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e){
					graphDims = graphSize.getValue();	
				}
			});

			this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
			this.add(maxFlow);
			this.add(beginMaxFlow);
			this.add(step);
			this.add(newGraph);
			this.add(graphSize);
		}
	}

	public static int getDims(){
		return graphDims;
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
	private Random R = new Random();
	EdmondsVertex[] vertices;
	int s, t;
	private EdmondsKarp ek;
	public SimpleGraphView(){

		cart = new HashMap<EdmondsEdge, Double>();

		trans = new Transformer<EdmondsEdge, Integer>(){
			public Integer transform(EdmondsEdge link){
				return link.capacity;
			}
		};

		edgeFact = new Factory<EdmondsEdge>(){
			public EdmondsEdge create(){
				return new EdmondsEdge(R.nextInt(150) + 1);
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

		this.generateGraph();
	}

	private void generateGraph(){
		Lattice2DGenerator kswg = new Lattice2DGenerator(graphFact, vertFact, edgeFact, Main.getDims(), false);
		g = (DirectedSparseGraph)kswg.create();
		vertices = new EdmondsVertex[g.getVertexCount()];
		int c = 0;
		for(EdmondsVertex vert : g.getVertices()){
			vert.name = (new Integer(c)).toString();
			vertices[c++] = vert;
		}
		//use these for the source and sink, select out 
		//of first half and second half to avoid selecting the same node
		//wgttt
		s = R.nextInt(vertices.length/2);
		t = R.nextInt(vertices.length/2) + vertices.length/2;
		vertices[s].s = true;
		vertices[t].t = true;

		UnweightedShortestPath<EdmondsVertex,EdmondsEdge> chkPath = new UnweightedShortestPath<EdmondsVertex,EdmondsEdge>(g);
		if (chkPath.getDistance(vertices[s], vertices[t]) == null){
			this.generateGraph();
		}
		return;
	}
	public void generateNewGraph() {
		this.generateGraph();
	}
	public void performEdmondsKarp() {
		//Lets now try our edmonds-karp algorithm... fingers crossed
		ek = new EdmondsKarp(g);
		ek.maxFlow(vertices[s], vertices[t], true);
	}

	public void performEdmondsStep() {
		if (ek == null){
			ek = new EdmondsKarp(g);
		}
		ek.maxFlow(vertices[s], vertices[t], false);
	}
}
