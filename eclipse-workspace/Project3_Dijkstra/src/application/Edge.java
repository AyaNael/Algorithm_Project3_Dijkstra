package application;

public class Edge {
    private String destination; // Destination capital's name
    private double distance;    // Distance between capitals (in kilometers)
    private double cost;        // Cost of the edge
    private int time;           // Time required for the edge in minutes

    // Constructor
    public Edge(String destination, double startLat, double startLong, double endLat, double endLong, double cost, int time) {
        this.destination = destination;
        this.distance = calculateDistance(startLat, startLong, endLat, endLong);
        this.cost = cost;
        this.time = time;
    }

    // Calculate distance using the Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // Distance in kilometers
    }

    public String getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    public double getCost() {
        return cost;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return destination + " (Distance: " + distance + " km, Cost: $" + cost + ", Time: " + time + " mins)";
    }
}
