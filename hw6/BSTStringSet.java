import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author Rae Xin
 */
public class BSTStringSet implements StringSet, Iterable<String>, SortedStringSet {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        Node current = _root;
        if (_root == null) {
            _root = new Node(s);
        } else {
            while (current != null && (!current.s.equals(s))) {
                int compare = s.compareTo(current.s);
                if (compare > 0) {
                    if (current.right == null) {
                        current.right = new Node(s);
                        return;
                    } else {
                        current = current.right;
                    }
                } else if (compare < 0) {
                    if (current.left == null) {
                        current.left = new Node(s);
                        return;
                    } else {
                        current = current.left;
                    }
                } else {
                    current = null;
                }
            }
        }

    }

    @Override
    public boolean contains(String s) {
        Node current = _root;
        while (current != null && (!current.s.equals(s))) {
            int compare = s.compareTo(current.s);
            if (compare < 0) {
                current = current.left;
            } else if (compare > 0) {
                current = current.right;
            } else {
                current = null;
            }
        }
        if (current != null && current.s.equals(s)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> asList() {
        ArrayList returnList = new ArrayList<String>();
        Iterator<String> newIterator = iterator();
        while (newIterator.hasNext()) {
            returnList.add(newIterator.next());
        }
        return returnList;
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

    @Override
    public Iterator<String> iterator(String low, String high) {

        ArrayList<String> initialArray = new ArrayList<>();
        Stack<String> allStack = new Stack<>();
        Iterator<String> iter = iterator();
        while (iter.hasNext()) {
            initialArray.add(iter.next());
        }

        for (String x: initialArray) {
            if (x.compareTo(low) >= 0 && x.compareTo(high) <= 0) {
                allStack.add(x);
            }
        }

        return allStack.iterator();
    }


    /** Root node of the tree. */
    private Node _root;
}