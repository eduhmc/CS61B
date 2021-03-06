/**
 * Scheme-like pairs that can be used to form a list of integers.
 * @author P. N. Hilfinger or unknown TA
 */
public class IntDList {

    /**
     * First and last nodes of list.
     */
    protected DNode _front, _back;

    /**
     * An empty list.
     */
    public IntDList() {
        _front = _back = null;
    }

    /**
     * @param values the ints to be placed in the IntDList.
     */
    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    /**
     * @return The first value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getFront() {
        return _front._val;
    }

    /**
     * @return The last value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getBack() {
        return _back._val;
    }

    /**
     * @return The number of elements in this list.
     */
    public int size() {
        int counter = 1;
        DNode walker = _front;
        if (walker == null) {
            return 0;
        }
        while (walker._next != null) {
            walker = walker._next;
            counter += 1;
        }
        return counter;

          // Your code here
    }

    /**
     * @param i index of element to return,
     *          where i = 0 returns the first element,
     *          i = 1 returns the second element,
     *          i = -1 returns the last element,
     *          i = -2 returns the second to last element, and so on.
     *          You can assume i will always be a valid index, i.e 0 <= i < size
     *          for positive indices and -size <= i < 0 for negative indices.
     * @return The integer value at index i
     */
    public int get(int i) {
        //        DNode walker = new DNode(null, 5, null);
        if (i < 0) {
            DNode walker = _back;
            while (i != -1) {
                i += 1;
                walker = walker._prev;
            }
            return walker._val;
        }
        else {
            DNode walker = _front;
            while (i != 0) {
                i -= 1;
                walker = walker._next;
            }
            return walker._val;
        }

          // Your code here
    }

    /**
     * @param d value to be inserted in the front
     */
    public void insertFront(int d) {
        // Your code here
        DNode newDNode = new DNode(null, d, null);
        _front = newDNode;
        if (_back == null) {
            _back = newDNode;
        }
        else {
            DNode traceCurrentBack = _back;
            while (traceCurrentBack._prev != null) {
                traceCurrentBack = traceCurrentBack._prev;
            }
            traceCurrentBack._prev = newDNode;
            newDNode._next = traceCurrentBack;
        }
    }

    /**
     * @param d value to be inserted in the back
     */
    public void insertBack(int d) {
        // Your code here
        DNode newDNode = new DNode(null, d, null);
        _back = newDNode;
        if (_front == null) {
            _front = newDNode;
        }
        else {
            DNode traceCurrentBack = _front;
            while (traceCurrentBack._next != null) {
                traceCurrentBack = traceCurrentBack._next;
            }
            traceCurrentBack._next = newDNode;
            newDNode._prev = traceCurrentBack;
        }
    }

    /**
     * Removes the last item in the IntDList and returns it.
     *
     * @return the item that was deleted
     */
    public int deleteBack() {
        DNode toBeDeleted = _back;
        if (_back != null){
            _back = _back._prev;
            if (_back == null){
                _front = null;
            }else {

                _back._next = null;
            }
        }
        return toBeDeleted._val;
    }

    /**
     * @return a string representation of the IntDList in the form
     * [] (empty list) or [1, 2], etc.
     * Hint:
     * String a = "a";
     * a += "b";
     * System.out.println(a); //prints ab
     */
    public String toString() {

        String theStr = "[";
        DNode walker = _front;
        Boolean firstItem = true;
        while (walker != null) {
            if (firstItem) {
                theStr = theStr + walker._val;
                walker = walker._next;
                firstItem = false;
            }
            else {
                theStr = theStr + ", " + walker._val;
                walker = walker._next;
            }
        }
        if (walker != null) {
            theStr = theStr + ", " + walker._val;
        }
        return theStr + ']';   // Your code here
    }

    /**
     * DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. This is also referred to as encapsulation.
     * Look it up for more information!
     */
    protected static class DNode {
        /** Previous DNode. */
        protected DNode _prev;
        /** Next DNode. */
        protected DNode _next;
        /** Value contained in DNode. */
        protected int _val;

        /**
         * @param val the int to be placed in DNode.
         */
        protected DNode(int val) {
            this(null, val, null);
        }

        /**
         * @param prev previous DNode.
         * @param val  value to be stored in DNode.
         * @param next next DNode.
         */
        protected DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}
