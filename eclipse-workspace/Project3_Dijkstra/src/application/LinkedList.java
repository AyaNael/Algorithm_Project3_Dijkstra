package application;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements Iterable<T> {
    private Node<T> head; // Head of the list
    private int size; // Size of the linked list

    public LinkedList() {
        this.head = null;
        this.size = 0;
    }

    // Add an element to the linked list
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    // Get the size of the linked list
    public int getSize() {
        return size;
    }

    // Make the linked list iterable
    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    // Inner class for the iterator
    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            T data = current.data;
            current = current.next;
            return data;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

}
