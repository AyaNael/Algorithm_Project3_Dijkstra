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
	public int getVertexIndex(String name) {
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

    public void dijkstra(String sourceName, GraphTable[] T, String filter) {
        int sourceIndex = getVertexIndex(sourceName);

        if (sourceIndex == -1) {
            throw new IllegalArgumentException("Source vertex not found!");
        }

        // Initialize the table
        for (int i = 0; i < numberOfVertices; i++) {
            T[i] = new GraphTable(vertices[i]);
        }
        T[sourceIndex].setDistance(0);

        while (true) {
            // Find the smallest unknown distance vertex
            int smallestIndex = -1;
            double smallestDistance = Double.MAX_VALUE;

            for (int i = 0; i < numberOfVertices; i++) {
                if (!T[i].isKnown() && T[i].getDistance() < smallestDistance) {
                    smallestIndex = i;
                    smallestDistance = T[i].getDistance();
                }
            }

            if (smallestIndex == -1) break; // No more unknown vertices
            // Debug: Current vertex being processed
            System.out.printf("Processing Vertex: %s with Current Distance: %.2f%n",
                              vertices[smallestIndex].getCapitalName(), smallestDistance);

            // Mark the vertex as known
            T[smallestIndex].setKnown(true);;

            // Update distances for each adjacent vertex
            for (Edge edge : vertices[smallestIndex].getEdges()) {
                int adjacentIndex = getVertexIndex(edge.getDestination());

                if (!T[adjacentIndex].isKnown()) {
                    double cost = switch (filter.toLowerCase()) {
                        case "distance" -> {  System.out.printf("Using Distance for edge %s -> %s: %.2f%n", 
                                vertices[smallestIndex].getCapitalName(), edge.getDestination(), edge.getDistance());
                        yield edge.getDistance();}
                        case "cost" -> {
                            System.out.printf("Using Cost for edge %s -> %s: %.2f%n", 
                                              vertices[smallestIndex].getCapitalName(), edge.getDestination(), edge.getCost());
                            yield edge.getCost();
                        }
                        case "time" -> {
                            System.out.printf("Using Time for edge %s -> %s: %d%n", 
                                              vertices[smallestIndex].getCapitalName(), edge.getDestination(), edge.getTime());
                            yield edge.getTime();
                        }
                        default -> throw new IllegalArgumentException("Invalid filter!");
                    };

                    if (T[smallestIndex].getDistance() + cost < T[adjacentIndex].getDistance()) {
                    	// Debug: Distance Update
                        System.out.printf("Updating Path: %s -> %s | New Distance: %.2f%n",
                                          vertices[smallestIndex].getCapitalName(), edge.getDestination(),
                                          T[smallestIndex].getDistance() + cost);
                        T[adjacentIndex].setDistance(T[smallestIndex].getDistance() + cost);
                        T[adjacentIndex].setPath(vertices[smallestIndex]);
                    }
                }
            }
        }
    }

  
    public void buildPath(int targetIndex, GraphTable[] T, StringBuilder pathBuilder) {
        if (T[targetIndex].getPath() != null) {
            int previousIndex = getVertexIndex(T[targetIndex].getPath().getCapitalName());
            buildPath(previousIndex, T, pathBuilder);
            pathBuilder.append(" -> ");
        }
 
        pathBuilder.append(vertices[targetIndex].getCapitalName());
     // Debug: Current path being built
        System.out.println("Current Path: " + pathBuilder.toString());
    }
    public LinkedList<Edge> getEdgesFromTablePath(GraphTable[] T, int targetIndex) {
        LinkedList<Edge> edgesInPath = new LinkedList<>();
        Capital current = vertices[targetIndex];

        while (T[targetIndex].getPath() != null) {
            Capital previous = T[targetIndex].getPath();
            int previousIndex = getVertexIndex(previous.getCapitalName());

            // Find the edge connecting 'previous' to 'current'
            for (Edge edge : vertices[previousIndex].getEdges()) {
                if (edge.getDestination().equals(current.getCapitalName())) {
                    edgesInPath.add(edge); // Add edge at the beginning to maintain order
                    break;
                }
            }

            // Move to the previous vertex
            targetIndex = previousIndex;
            current = previous;
        }

        return edgesInPath;
    }
    // Helper method to display the table (for debugging)
    public void displayTable(GraphTable[] T) {
        for (GraphTable t : T) {
            System.out.println(t);
        }
    }
    public void displayEdges() {
        System.out.println("Graph Edges:");
        for (int i = 0; i < numberOfVertices; i++) {
            for (Edge edge : vertices[i].getEdges()) {
                System.out.printf("Edge: %s -> %s | Distance: %.2f | Cost: %.2f | Time: %d%n",
                                  vertices[i].getCapitalName(), edge.getDestination(), edge.getDistance(), edge.getCost(), edge.getTime());
            }
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
