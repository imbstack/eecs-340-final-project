public class EdmondsEdge {
    public int capacity;
    private int flow;
    private int reverseFlow;
    private int residualCapacity;

    EdmondsEdge(int capacity) {
        this.capacity = capacity;
        this.flow = 0;
        this.residualCapacity = capacity;
    }

    /**
     * Pass in the new flow of this edge and the usedCapacity and residualCapacity
     * fields will be updated automatically.
     * @param flow
     */
    public void setNewFlow(int flow) {
        this.flow = flow;
        this.reverseFlow = flow;
        this.residualCapacity = this.capacity - flow;
    }
    public void addFlow(int flow) {
        this.flow += flow;
        this.reverseFlow -= flow;
        this.residualCapacity = this.capacity - flow;
    }
    public int getRemainingCapacity() {
        return residualCapacity;
    }
}
