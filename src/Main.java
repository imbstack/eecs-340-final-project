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
import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import edu.uci.ics.jung.visualization.annotations.*;
import org.apache.commons.collections15.Predicate;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import java.awt.geom.Point2D;

class Main{
	static SimpleGraphView sgv;
	static Layout<EdmondsVertex, EdmondsEdge> layout;
	static VisualizationViewer<EdmondsVertex, EdmondsEdge> vv;
	static JFrame frame;
	static int graphDims;
	static long time;
	static ProgressDisplay pd;
	static ControlPanel controls;
	static final Dimension SIZE_OF_WINDOW = new Dimension(1000,730);
	static final Dimension SIZE_OF_GRAPH = new Dimension(900,650);
	public static void main(String[] args){
		if ( args.length > 0 && args[0].equals("test")){
			System.out.println("Begin Tests:");
			for (int i = 2; i <= 256; i+=2){
				for (int j = 0; j < 10; j++){
					graphDims = i;
					sgv = new SimpleGraphView(true);
					time = System.currentTimeMillis();
					sgv.performEdmondsKarp(true);
					System.out.println(Integer.toString(i*i) + ": "  + Long.toString(System.currentTimeMillis() - time));
				}
			}
		}
		else{
			graphDims = 3;
			frame = new JFrame("Ford Fulkerson Method (Edmonds Karp Implementation) Demo");
			pd = new ProgressDisplay();
			controls = new ControlPanel(pd);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
			frame.getContentPane().setBackground(new Color(25,25,35));
			frame.getContentPane().add(controls);
			frame.getContentPane().add(pd);
			renderGraph();
			frame.getContentPane().add(vv);
			frame.pack();
			frame.setLocation(12,0);
			frame.setSize(SIZE_OF_WINDOW);
			frame.setVisible(true);
		}
	}
	public static void renderGraph() {
		sgv = new SimpleGraphView(); //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		layout = new ISOMLayout(sgv.g);
		layout.setSize(SIZE_OF_GRAPH); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		vv = new VisualizationViewer<EdmondsVertex, EdmondsEdge>(layout);
		vv.setPreferredSize(SIZE_OF_GRAPH); //Sets the viewing area size
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
				if (edge.getFlow() == 0) {
					return new Color(100,100,100);
				}
				return new Color(50,50,50+(int)(205*((double)edge.getFlow()/EdmondsEdge.maxUsed)));
			}
		};
		Transformer<EdmondsEdge,Font> edgeFont = new Transformer<EdmondsEdge,Font>() {
			public Font transform(EdmondsEdge edge) {
				return new Font("sans", Font.TRUETYPE_FONT, 20);
			}
		};
		Transformer<EdmondsVertex,Font> vertexFont = new Transformer<EdmondsVertex,Font>() {
			public Font transform(EdmondsVertex edge) {
				return new Font("sans", Font.BOLD, 12);
			}
		};
		Transformer<EdmondsEdge, Stroke> edgeStrokeTransformer = new Transformer<EdmondsEdge, Stroke>() {
			public Stroke transform(EdmondsEdge edge) {
				float wid = 4*(float)edge.capacity/EdmondsEdge.maxCapacity;
				return new BasicStroke(wid, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
		vv.getRenderContext().setEdgeFontTransformer(edgeFont);
		vv.getRenderContext().setVertexFontTransformer(vertexFont);
		vv.getRenderContext().setArrowFillPaintTransformer(edgePaint);
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.setForeground(Color.WHITE);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		//vv.getRenderer().getVertexLabelRenderer().setForeground(Color.black);
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.setBackground(slate);
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
		//        AnnotationManager am = new AnnotationManager(vv.getRenderContext());
		//        am.add(Annotation.Layer.UPPER,(new Annotation("Testing",Annotation.Layer.UPPER,vv.getForeground(),false,new Point(0,0))));
		vv.revalidate();
		vv.repaint();
		Main.controls.step.setEnabled(true);
	}
	static class ControlPanel extends JPanel {
		JButton maxFlow;
		JButton reset;
		JButton step;
		JButton newGraph;
		JPanel debug;

		JSlider graphSize;
		final ProgressDisplay pd;
		ControlPanel(final ProgressDisplay pd) {
			this.pd = pd;
			maxFlow = new JButton("Compute Max Flow");
			reset = new JButton("Reset");
			step = new JButton("Step");
			newGraph = new JButton("New Random Graph");
			graphSize = new JSlider(2, 10, graphDims);

			maxFlow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int maxFlow = sgv.performEdmondsKarp();
					Main.pd.showFinal(maxFlow);
					Main.controls.step.setEnabled(false);
					//vv.update(frame.getGraphics());
					vv.repaint();
				}
			});

			reset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Main.controls.step.setEnabled(true);
					Main.pd.reset();
					sgv.reset();
					vv.updateUI();
					vv.repaint();
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
					Main.pd.reset();
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
			this.add(reset);
			this.add(step);
			this.add(newGraph);
			this.add(graphSize);
		}
	}
	static class ProgressDisplay extends JPanel {
		final TJLabel awaitingText = new TJLabel("Waiting To Begin...");
		final TJLabel executingNow = new TJLabel("Current Status: ");
		final TJLabel foundPath    = new TJLabel("Found Path ");
		final TJLabel withCapacity = new TJLabel(" with capacity ");
		final TJLabel finished     = new TJLabel("Finished, with max flow of ");
		int totalCapacity;
		static class TJLabel extends JLabel {
			TJLabel(String contents) {
				super(contents);
				this.setFont(new Font("sans",Font.BOLD,20));
				this.setForeground(new Color(240,240,230));
			}
		}
		ProgressDisplay() {
			int totalCapacity = 0;
			this.setBackground(new Color(25,25,35));
			this.setForeground(new Color(240,240,230));
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.add(awaitingText);
		}
		public void takeStep(String path, int capacity) {
			totalCapacity += capacity;
			TJLabel pathLabel = new TJLabel(path);
			TJLabel capacityLabel = new TJLabel((new Integer(capacity)).toString());
			this.removeAll();
			this.add(executingNow);
			if (capacity > 0) {
				this.add(foundPath);
				this.add(pathLabel);
				this.add(withCapacity);
				this.add(capacityLabel);
			} else {
				TJLabel totalCapacityLabel = new TJLabel((new Integer(totalCapacity).toString()));
				this.add(finished);
				this.add(totalCapacityLabel);
				Main.controls.step.setEnabled(false);
			}
			this.update();
		}
		public void showFinal(int capacity) {
			this.removeAll();
			TJLabel totalCapacityLabel = new TJLabel((new Integer(capacity).toString()));
			this.add(finished);
			this.add(totalCapacityLabel);
			this.update();
		}
		public void reset() {
			totalCapacity = 0;
			this.removeAll();
			this.add(awaitingText);
			this.update();
		}
		public void update() {
			this.updateUI();
			this.repaint();
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
		this(false);
	}

	public SimpleGraphView(boolean isTest){

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

		this.generateGraph(isTest);
	}

	private void generateGraph(){
		this.generateGraph(false);
	}

	private void generateGraph(boolean isTest){
		Lattice2DGenerator kswg = new Lattice2DGenerator(graphFact, vertFact, edgeFact, Main.getDims(), false);
		g = (DirectedSparseGraph)kswg.create();

		if (!isTest){
			Predicate<EdmondsEdge> selRandom = new Predicate<EdmondsEdge>(){
				public boolean evaluate(EdmondsEdge edge){
					if (R.nextFloat() > 0.85){
						return false;
					}
					return true;
				}
			};
			EdgePredicateFilter<EdmondsVertex, EdmondsEdge> edgeFilter = new EdgePredicateFilter<EdmondsVertex, EdmondsEdge>(selRandom);
			g = (DirectedSparseGraph)edgeFilter.transform(g);
		}
		vertices = new EdmondsVertex[g.getVertexCount()];
		int c = 0;
		for(EdmondsVertex vert : g.getVertices()){
			vert.name = (new Integer(c)).toString();
			vertices[c++] = vert;
		}
		if (!isTest){
			//use these for the source and sink, select out 
			//of first half and second half to avoid selecting the same node
			s = R.nextInt(vertices.length/2);
			t = R.nextInt(vertices.length/2) + vertices.length/2;
		}
		else{
			s = 0;
			t = vertices.length - 1;
		}
		vertices[s].s = true;
		vertices[t].t = true;
		UnweightedShortestPath<EdmondsVertex,EdmondsEdge> chkPath = new UnweightedShortestPath<EdmondsVertex,EdmondsEdge>(g);
		if (chkPath.getDistance(vertices[s], vertices[t]) == null){
			this.generateGraph();
		}
		EdmondsEdge.maxUsed = 0;
		return;
	}
	public void generateNewGraph() {
		this.generateGraph();
	}

	public int performEdmondsKarp(){
		return this.performEdmondsKarp(false);
	}

	public int performEdmondsKarp(boolean isTest) {
		//Lets now try our edmonds-karp algorithm... fingers crossed
		ek = new EdmondsKarp(g, isTest);
		int maxFlow = ek.maxFlow(vertices[s], vertices[t], true);
		if (!isTest){
			Main.pd.showFinal(maxFlow);
		}
		return maxFlow;
	}

	public void performEdmondsStep() {
		if (ek == null){
			ek = new EdmondsKarp(g);
		}
		ek.maxFlow(vertices[s], vertices[t], false);
		Main.pd.takeStep(ek.returnPath(vertices[t]), (int)ek.capacity);
	}
    public void reset() {
        for(EdmondsEdge oneEdge: g.getEdges()) {
            oneEdge.setNewFlow(0);
        }
        for(EdmondsVertex oneVertex: g.getVertices()) {
            oneVertex.parentNode = null;
            oneVertex.pathCapacityToNode = 4294967296L;
        }
        EdmondsEdge.maxUsed = 0;
    }
}
