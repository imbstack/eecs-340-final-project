public class EdmondsVertex {
    String name;
    int discoveredState;
    EdmondsVertex parentNode;
    long pathCapacityToNode;

    EdmondsVertex(String name) {
        this.name = name;
        this.discoveredState = -1; // undiscovered
        this.pathCapacityToNode = 4294967296L; // = max int + 1 (roughly equal to infinity)
    }
}