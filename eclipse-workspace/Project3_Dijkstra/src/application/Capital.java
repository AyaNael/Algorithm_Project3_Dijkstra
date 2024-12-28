package application;


public class Capital {
    private String capitalName; // Name of the capital
    private double longitude;   // Longitude of the capital
    private double latitude;    // Latitude of the capital
    private LinkedList<Edge> edges; // List of edges (connections to other capitals)

    // Constructor
    public Capital(String capitalName, double longitude, double latitude) {
        this.capitalName = capitalName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.edges = new LinkedList<>();
    }

    // Add an edge to the list of edges
    public void addEdge(Capital destinationCapital, double cost, int time) {
        this.edges.add(new Edge(
            destinationCapital.getCapitalName(),
            this.latitude, this.longitude,
            destinationCapital.getLatitude(), destinationCapital.getLongitude(),
            cost, time
        ));
    }

    public String getCapitalName() {
        return capitalName;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Capital: ").append(capitalName)
              .append(" (Longitude: ").append(longitude)
              .append(", Latitude: ").append(latitude).append(")\nEdges:");
        if (edges.isEmpty()) {
            result.append(" None");
        } else {
            for (Edge edge : edges) {
                result.append("\n  -> ").append(edge);
            }
        }
        return result.toString();
    }
}
