import java.util.ArrayList;

/** A Generic heap class. Unlike Java's priority queue, this heap doesn't just
  * store Comparable objects. Instead, it can store any type of object
  * (represented by type T) and an associated priority value.
  * @author CS 61BL Staff*/
public class ArrayHeap<T> {

    /* DO NOT CHANGE THESE METHODS. */

    /* An ArrayList that stores the nodes in this binary heap. */
    private ArrayList<Node> contents;

    /* A constructor that initializes an empty ArrayHeap. */
    public ArrayHeap() {
        contents = new ArrayList<>();
        contents.add(null);
    }

    /* Returns the number of elments in the priority queue. */
    public int size() {
        return contents.size() - 1;
    }

    /* Returns the node at index INDEX. */
    private Node getNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    /* Sets the node at INDEX to N */
    private void setNode(int index, Node n) {
        // In the case that the ArrayList is not big enough
        // add null elements until it is the right size
        while (index + 1 > contents.size()) {
            contents.add(null);
        }
        contents.set(index, n);
    }

    /* Returns and removes the node located at INDEX. */
    private Node removeNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.remove(index);
        }
    }

    /* Swap the nodes at the two indices. */
    private void swap(int index1, int index2) {
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);
        this.contents.set(index1, node2);
        this.contents.set(index2, node1);
    }

    /* Prints out the heap sideways. Use for debugging. */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /* Recursive helper method for toString. */
    private String toStringHelper(int index, String soFar) {
        if (getNode(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = getRightOf(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getNode(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getNode(index) + "\n";
            int leftChild = getLeftOf(index);
            if (getNode(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }

    /* A Node class that stores items and their associated priorities. */
    public class Node {
        private T item;
        private double priority;

        private Node(T item, double priority) {
            this.item = item;
            this.priority = priority;
        }

        public T item() {
            return this.item;
        }

        public double priority() {
            return this.priority;
        }

        public void setPriority(double priority) {
            this.priority = priority;
        }

        @Override
        public String toString() {
            return this.item.toString() + ", " + this.priority;
        }
    }



    /* FILL IN THE METHODS BELOW. */

    /* Returns the index of the left child of the node at i. */
    private int getLeftOf(int i) {
        //TODO
        return i * 2;
    }

    /* Returns the index of the right child of the node at i. */
    private int getRightOf(int i) {
        //TODO
        return 1 + (2 * i) ;
    }

    /* Returns the index of the node that is the parent of the node at i. */
    private int getParentOf(int i) {
        //TODO
        return i / 2;
    }

    /* Adds the given node as a left child of the node at the given index. */
    private void setLeft(int index, Node n) {
        //TODO
        setNode(getLeftOf(index), n);
    }

    /* Adds the given node as the right child of the node at the given index. */
    private void setRight(int index, Node n) {
        //TODO
        setNode(getRightOf(index), n);
    }

    /** Returns the index of the node with smaller priority. Precondition: not
      * both nodes are null. */
    private int min(int index1, int index2) {
        //TODO
        if (contents.get(index2).priority() <= contents.get(index1).priority()) {
            return index2;
        }
        return index1;
    }

    /* Returns the Node with the smallest priority value, but does not remove it
     * from the heap. */
    public Node peek() {
        //TODO
        return getNode(1);

    }

    /* Bubbles up the node currently at the given index. */
    private void bubbleUp(int index) {
        //TODO
        if (getParentOf(index) > 0) {
            while (contents.get(index).priority()  < contents.get(getParentOf(index)).priority()) {
                swap(index, getParentOf(index));
                index = getParentOf(index);
                if (getParentOf(index) <= 0) {
                    break;
                }
            }
        }
    }

    /* Bubbles down the node currently at the given index. */
    private void bubbleDown(int index) {
        //TODO
        int izquierda = getLeftOf(index); int derecha = getRightOf(index); int pequeño;
        if (izquierda >= contents.size() && derecha  >= contents.size()) {
            return;
        } else if (derecha  >= contents.size()) {
            pequeño = izquierda;
        } else if (izquierda >= contents.size()) {
            pequeño = derecha ;
        } else {
            pequeño = min(izquierda, derecha );
        }
        if (pequeño < contents.size()) {
            while (contents.get(index).priority()
                    > contents.get(pequeño).priority()) {
                swap(index, pequeño);
                index = pequeño;
                izquierda = getLeftOf(index); derecha  = getRightOf(index);
                if (izquierda >= contents.size() && derecha  >= contents.size()) {
                    break;
                } else if (derecha  >= contents.size()) {
                    pequeño = izquierda;
                } else if (izquierda >= contents.size()) {
                    pequeño = derecha ;
                } else {
                    pequeño = min(izquierda, derecha );
                }
            }
        }
    }

    /* Inserts an item with the given priority value. Same as enqueue, or offer. */
    public void insert(T item, double priority) {
        Node n = new Node(item, priority);
        contents.add(n);
        bubbleUp(size());
    }

    /* Returns the element with the smallest priority value, and removes it from
     * the heap. Same as dequeue, or poll. */
    public T removeMin() {
        //TODO
        T sn = peek().item();
        swap(1, size());
        removeNode(size());
        bubbleDown(1);
        return sn;
    }

    /* Changes the node in this heap with the given item to have the given
     * priority. You can assume the heap will not have two nodes with the same
     * item. Check for item equality with .equals(), not == */
    public void changePriority(T item, double priority) {
        //TODO
        if (size() <= 0) {
            return;
        }
        for (int i = 1; i < contents.size(); i++) {
            double origprio = contents.get(i).priority();
            if (contents.get(i).item.equals(item)) {
                contents.get(i).setPriority(priority);
                if (priority < origprio) {
                    bubbleUp(i);
                } else {
                    bubbleDown(i);
                }
                break;
            }
        }
    }

}
