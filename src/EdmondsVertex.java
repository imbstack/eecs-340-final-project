public class EdmondsVertex {
    int discoveredState;
    EdmondsVertex parentNode;
    long pathCapacityToNode;
    public static int numNodes = 0;
    public int id;
    public boolean s; //this is the source of the flow
    public boolean t; //this is the sink of the flow

    EdmondsVertex() {
	numNodes += 1;
	this.id = numNodes;
        this.discoveredState = -1; // undiscovered
        this.pathCapacityToNode = 4294967296L; // = max int + 1 (roughly equal to infinity)
	this.s = false;
	this.t = false;
    }
}
