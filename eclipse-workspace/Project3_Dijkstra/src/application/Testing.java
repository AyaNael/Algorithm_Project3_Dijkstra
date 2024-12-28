package application;

public class Testing {
    public static void main(String[] args) {
        // Test the LinkedList class
        LinkedList<Integer> testList = new LinkedList<>();
        System.out.println("Testing LinkedList...");
        System.out.println("Is the list empty? " + testList.isEmpty()); // Should print true
        testList.add(10);
        testList.add(20);
        System.out.println("Is the list empty? " + testList.isEmpty()); // Should print false
        System.out.println("Size of the list: " + testList.getSize()); // Should print 2

        // Test the Edge class
        System.out.println("\nTesting Edge...");
        Edge edge = new Edge("Mogadishu", 0.3476, 32.5825, 2.0469, 45.3182, 250, 220);
        System.out.println(edge); // Should print the edge with calculated distance

        // Test the Capital class
        System.out.println("\nTesting Capital...");
        Capital kampala = new Capital("Kampala", 32.5825, 0.3476);
        Capital mogadishu = new Capital("Mogadishu", 45.3182, 2.0469);
        kampala.addEdge(mogadishu, 250, 220);
        System.out.println(kampala); // Should print Kampala and its edges

        // Test the Graph class
        System.out.println("\nTesting Graph...");
        Graph graph = new Graph(5);
        graph.addCapital("Kampala", 0.3476, 32.5825);
        graph.addCapital("Mogadishu", 2.0469, 45.3182);
        graph.addCapital("Addis Ababa", 9.0249, 38.7468);
        graph.addCapital("Nairobi", -1.2864, 36.8172);
        graph.addCapital("Cairo", 30.0444, 31.2357);

        // Add edges
        graph.addEdge("Kampala", "Mogadishu", 250, 220);
        graph.addEdge("Kampala", "Addis Ababa", 700, 600);
        graph.addEdge("Mogadishu", "Addis Ababa", 500, 420);
        graph.addEdge("Addis Ababa", "Nairobi", 200, 150);
        graph.addEdge("Nairobi", "Cairo", 800, 900);

        // Display the graph
        graph.displayGraph();
    }
}
