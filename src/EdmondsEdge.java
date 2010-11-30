public class EdmondsEdge {
    public int capacity;
    private int flow;
    private int reverseFlow;
    private int residualCapacity;
    public static int maxCapacity, maxUsed;

    EdmondsEdge(int capacity) {
        this.capacity = capacity;
        this.flow = 0;
        this.residualCapacity = capacity;
        maxCapacity = Math.max(this.capacity, maxCapacity);
        maxUsed = Math.max(0, this.maxUsed);
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
        maxUsed = Math.max(maxUsed, this.flow);
    }
    public void addFlow(int flow) {
        this.flow += flow;
        this.reverseFlow -= flow;
        this.residualCapacity = this.capacity - flow;
        maxUsed = Math.max(maxUsed, this.flow);
    }
    public int getRemainingCapacity() {
        return residualCapacity;
    }
    public int getFlow() {
        return flow;
    }
    @Override
    public String toString() {
        if (this.flow != 0) {
            return Integer.toString(flow) + "/" + Integer.toString(capacity);
        } else {
            return "";
        }
    }
}
