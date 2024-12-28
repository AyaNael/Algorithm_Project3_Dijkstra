package application;

public class Graph {
	private Capital[] vertices; // Array to store vertices (capitals)
	private LinkedList<Edge>[] adjList; // Array of linked lists for adjacency list
	private int numberOfVertices; // Current number of vertices
	private int capacity; // Maximum capacity of the graph

	// Constructor
	public Graph(int capacity) {
		this.capacity = capacity;
		this.numberOfVertices = 0;
		this.vertices = new Capital[capacity];
		this.adjList = new LinkedList[capacity];

		// Initialize each linked list in the adjacency list array
		for (int i = 0; i < capacity; i++) {
			adjList[i] = new LinkedList<>();
		}
	}

	// Add a capital (vertex) to the graph
	public void addCapital(String name, double latitude, double longitude) {
		if (numberOfVertices >= capacity) {
			throw new IllegalStateException("Graph capacity exceeded!");
		}
		vertices[numberOfVertices++] = new Capital(name, longitude, latitude); // Add the capital
	}

	// Add an edge between two capitals
	public void addEdge(String from, String to, double cost, int time) {
		int fromIndex = getVertexIndex(from);
		int toIndex = getVertexIndex(to);
		System.out.println(fromIndex + "-" + toIndex);
		try {
			if (fromIndex == -1 || toIndex == -1) {
				throw new IllegalArgumentException("One or both capitals do not exist!");
			}

		} catch (Exception e) {
			e.toString();
		}
		// Delegate edge addition to the `addEdge` method of the source capital
		vertices[fromIndex].addEdge(vertices[toIndex], cost, time);
	}

	// Get the index of a vertex
	private int getVertexIndex(String name) {
		for (int i = 0; i < numberOfVertices; i++) {
			if (vertices[i].getCapitalName().equals(name)) {
				return i;
			}
		}
		return -1; // Vertex not found
	}
	 // Get a vertex (Capital) by its index
    public Capital getVertex(int index) {
        if (index < 0 || index >= numberOfVertices) {
            throw new IndexOutOfBoundsException("Invalid vertex index: " + index);
        }
        return vertices[index];
    }
	// Display the graph
	public void displayGraph() {
		for (int i = 0; i < numberOfVertices; i++) {
			System.out.println(vertices[i]); // Calls the `toString` method of `Capital`
		}
	}

	public Capital[] getVertices() {
		return vertices;
	}

	public void setVertices(Capital[] vertices) {
		this.vertices = vertices;
	}

	public LinkedList<Edge>[] getAdjList() {
		return adjList;
	}

	public void setAdjList(LinkedList<Edge>[] adjList) {
		this.adjList = adjList;
	}

	public int getNumberOfVertices() {
		return numberOfVertices;
	}

	public void setNumberOfVertices(int numberOfVertices) {
		this.numberOfVertices = numberOfVertices;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

}
