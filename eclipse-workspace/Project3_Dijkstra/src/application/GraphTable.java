package application;


public class GraphTable {
    private Capital vertex;   // The capital (vertex) itself
    private double distance;  // Shortest known distance to this vertex
    private Capital path;     // The previous capital in the shortest path
    private boolean known;    // Whether this vertex is already visited (known)

    // Constructor
    public GraphTable(Capital vertex) {
        this.vertex = vertex;
        this.distance = Double.MAX_VALUE; // Initially, the distance is infinite
        this.path = null;                 // No path known initially
        this.known = false;               // Initially, the vertex is unknown
    }

    public Capital getVertex() {
        return vertex;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Capital getPath() {
        return path;
    }

    public void setPath(Capital path) {
        this.path = path;
    }

    public boolean isKnown() {
        return known;
    }

    public void setKnown(boolean known) {
        this.known = known;
    }

    @Override
    public String toString() {
        return "Vertex: " + vertex.getCapitalName() +
               ", Distance: " + distance +
               ", Path: " + (path != null ? path.getCapitalName() : "None") +
               ", Known: " + known;
    }
}

