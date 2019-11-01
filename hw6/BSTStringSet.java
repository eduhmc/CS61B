import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.*;

/**
 * Implementation of a BST based String Set.
 * @author eduhmc
 */
public class BSTStringSet implements StringSet, Iterable<String> {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        // FIXME: PART A
        _root = put(s, _root);
    }


    @Override
    public boolean contains(String s) {
        // FIXME: PART A
        return contains(s, _root);

    }


    @Override
    public List<String> asList() {
        // FIXME: PART A
        List<String> result = new ArrayList<>();
        asList(result, _root);
        return result;
    }

    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }
    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {

        return new BSTIterator(_root);
    }

    private Node put(String s, Node federer){
        if (federer == null){
            return new Node(s);
        }
        int prueba = federer.s.compareTo(s);
        if (prueba > 0) {
            federer.left = put(s, federer.left);
        } else if (prueba < 0){
            federer.right = put(s, federer.right);
        }
        return federer;
    }
    private boolean contains(String s, Node federer){
        if (federer == null){
            return false;
        }
        if (federer.s.equals(s)){
            return true;
        } else {
            if (federer.s.compareTo(s) > 0){
                return contains(s, federer.left);
            } else{
                return contains(s, federer.right);
            }
        }
    }

    private  void asList(List<String> lst, Node newnode){
        if (newnode == null){
            return;
        }
        asList(lst, newnode.left);
        lst.add(newnode.s);
        asList(lst, newnode.right);
    }

    // FIXME: UNCOMMENT THE NEXT LINE FOR PART B
    //@Override
    public Iterator<String> iterator(String low, String high) {
        List<String> nuevo = asList();
        int bajo = 0;
        int alto = nuevo.size();
        for (int i = 0; i < nuevo.size(); i++){
            int check = low.compareTo(nuevo.get(i));
            if (check > 0){
                bajo = i + 1;
            }
            int chequeo = high.compareTo(nuevo.get(i));
            if (chequeo < 0){
                alto = i;
                break;
            }
        }
        return nuevo.subList(bajo, alto).iterator();
    }

    /** Root node of the tree. */
    private Node _root;
}