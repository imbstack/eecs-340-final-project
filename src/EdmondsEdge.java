public class EdmondsEdge {
    public int capacity;
    private int usedCapacity;
    private int residualCapacity;

    EdmondsEdge(int capacity) {
        this.capacity = capacity;
        this.usedCapacity = 0;
        this.residualCapacity = capacity;
    }

    /**
     * Pass in the new flow of this edge and the usedCapacity and residualCapacity
     * fields will be updated automatically.
     * @param flow
     */
    public void setNewFlow(int flow) {
        this.usedCapacity = flow;
        this.residualCapacity = this.capacity - flow;
    }
}