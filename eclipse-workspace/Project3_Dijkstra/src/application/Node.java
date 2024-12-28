package application;

class Node<T> {
	T data; // The data stored in the node
	Node<T> next; // Reference to the next node in the list

	public Node(T data) {
		this.data = data;
		this.next = null;
	}
	
}