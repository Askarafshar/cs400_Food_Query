package application;


import java.util.*;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *         Takuto Sugita (tsugita@wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;
    
    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;

    private static final String LESS = "<=";
    private static final String GREATER = ">=";
    private static final String EQUAL = "==";

    // use this whenever the empty list is needed to avoid the empty list
    // instantiation.
    private final List<V> EMPTY_VALUES = Collections.emptyList();

    /**
     * Public constructor
     * 
     * @param branchingFactor 
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException(
               "Illegal branching factor: " + branchingFactor);
        }
        this.branchingFactor = branchingFactor;
        root = new LeafNode();
    }

    /**
     * Insert a key value pair into the instance of the B+tree
     *
     * @param key key used for comparing things
     * @param value value associated with the key
     */
    @Override
    public void insert(K key, V value) {
        root.insert(key, value);
    }


    /**
     * Search the entries that meet the specification
     * return empty list if there is no entry that satisfies the specification
     * return empty list if the comparator value passed as an argument is
     * not one of "<=", "==", or ">=".
     *
     * @param key to be searched
     * @param comparator is a string
     * @return entries which satisfy the specification.
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
        if (!comparator.contentEquals(LESS) &&
            !comparator.contentEquals(GREATER) &&
            !comparator.contentEquals(EQUAL) )
            return new ArrayList<V>();

        List<V> result = root.rangeSearch(key, comparator);
        return result;
    }

    /**
     * returns the string that represents the internal structure of
     * this instance
     * @return
     */
    @Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }
    
    
    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan
     */
    private abstract class Node {
        
        // List of keys
        List<K> keys;

        /**
         * Package constructor
         */
        Node() {
        	this.keys = new ArrayList<K>();
        }
        
        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();
        
        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();
        
        /**
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * 
         * @return boolean
         */
        abstract boolean isOverflow();

        public String toString() {
            return keys.toString();
        }

        /**
         * make the root node when the previous root node is overflow
         * return the newly created root node
         *
         * @param entry1 1st child node of the newly created root node
         * @param entry2 2nd child node of the newly created root node
         * @param rootValue the value to be stored in the newly created root node
         * @return newly created root node
         */
        public InternalNode makeRoot(Node entry1, Node entry2, K rootValue) {
            InternalNode newRoot = new InternalNode();
            newRoot.keys.add(rootValue);
            newRoot.children.add(entry1);
            newRoot.children.add(entry2);

            return newRoot;
        }


    } // End of abstract class Node
    
    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;
        // direct parent node
        InternalNode parent;

        /**
         * Package constructor
         */
        InternalNode() {
            super();
            this.children = new ArrayList<Node>();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return children.get(0).getFirstLeafKey();
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return children.size() > branchingFactor;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
        	int childIndex = Collections.binarySearch(keys, key);
        	if (childIndex < 0){
        	    childIndex = -childIndex - 1;
            }

        	Node child = children.get(childIndex);
        	child.insert(key, value);

        	if (child.isOverflow()) {
        		Node sibling = child.split();

                K keyToPromote = sibling.getFirstLeafKey();

                int keyPosition = 0;
                for ( ; keyPosition < children.size(); keyPosition++) {
                    if (children.get(keyPosition) == child) break;
                }
                keys.add(keyPosition, keyToPromote);
                children.add(keyPosition + 1, sibling);

                InternalNode temp = this;
                while (temp != null && temp.isOverflow()) {
                    InternalNode internalSibling = (InternalNode) temp.split();
                    K tempKeyToPromote = internalSibling.getFirstLeafKey();
                    internalSibling.keys.remove(0);

                    // change parents of the sibling's children to sibling itself
                    for (Node c : internalSibling.children) {
                        try {
                            ((InternalNode) c).parent = internalSibling;
                        } catch (ClassCastException e) {
                            // NO-OP this is a leaf_node instance
                        }
                    }

                    if (temp.parent == null) { // root is overflowed
                        root = makeRoot(temp, internalSibling, tempKeyToPromote);
                        temp.parent = (InternalNode) root;
                        internalSibling.parent = (InternalNode) root;

                        return;
                    }

                    int tempKeyPosition
                            = Collections.binarySearch(temp.parent.keys, tempKeyToPromote);
                    int tempPositionModified = tempKeyPosition;
                    if (tempKeyPosition < 0) {
                        tempPositionModified *= -1;
                        tempPositionModified -= 1;
                    }
                    temp.parent.keys.add(tempPositionModified, tempKeyToPromote);
                    temp.parent.children.add(tempPositionModified + 1, internalSibling);
                    internalSibling.parent = temp.parent;

                    temp = temp.parent;
                }
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	InternalNode sibling = new InternalNode();
        	int start = keys.size() / 2;
            for (int i = start; i < keys.size(); i++) {
            	sibling.keys.add(keys.get(i));
            	sibling.children.add(children.get(i + 1));
            }

            keys.subList(start, keys.size()).clear();
            children.subList(start + 1, children.size()).clear();
            
            return sibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            int childIndex = Collections.binarySearch(keys, key);
            if (childIndex < 0){
                childIndex = -childIndex - 1;
            }

            Node child = children.get(childIndex);

            return child.rangeSearch(key, comparator);
        }
    
    } // End of class InternalNode
    
    
    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan
     */
    private class LeafNode extends Node {
        
        // List of values
        List<V> values;
        
        // Reference to the next leaf node
        LeafNode next;
        
        // Reference to the previous leaf node
        LeafNode previous;
        
        /**
         * Package constructor
         */
        LeafNode() {
            super();
            values = new ArrayList<V>();
        }
        
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return values.size() > branchingFactor - 1;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
        	int valueIndex = Collections.binarySearch(keys, key);
            if (valueIndex < 0) {
			    valueIndex = -valueIndex - 1;
            }

            values.add(valueIndex, value);
            keys.add(valueIndex, key);

			if (root.isOverflow()) {
                Node sibling = split();
				root = makeRoot(this, sibling, sibling.getFirstLeafKey());
			}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
        	LeafNode sibling = new LeafNode();
            int start = keys.size() / 2;
            for (int i = start; i < keys.size(); i++) {
            	sibling.keys.add(keys.get(i));
            	sibling.values.add(values.get(i));
            }
            
            keys.subList(start, keys.size()).clear();
            values.subList(start, values.size()).clear();

            if (next != null) {
                next.previous = sibling;
            }
            sibling.next = next;
            sibling.previous = this;
            this.next = sibling;
            
            return sibling;
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
            int valueIndex = Collections.binarySearch(keys, key);
            if (valueIndex < 0) {
                if (comparator.equals(EQUAL)) {
                    if (key.compareTo(keys.get(0)) < 0) return EMPTY_VALUES;
                    if (next == null) return EMPTY_VALUES;

                    return next.rangeSearch(key, comparator);
                }
                if (comparator.equals(LESS)) valueIndex = -valueIndex - 1;
                if (comparator.equals(GREATER)) valueIndex = -valueIndex;
            }

            List<V> answer = new ArrayList<>();
            if (!comparator.equals(LESS)) {
                answer.addAll(getRangeValues(comparator, valueIndex));
            } else {
                // valueIndex points to the leftmost index of the key
                // there might be the same value on the right side of the tree.
                // so in the case of less than equal filter,
                // before collecting values toward the left side of the tree,
                // gather the same values in the right side of the tree.
                for (int i = valueIndex; i < keys.size(); i++) {
                    if (key.compareTo(keys.get(i)) != 0) break;

                    answer.add(values.get(i));
                }

                LeafNode sameValueNode = next;
                while (sameValueNode != null &&
                        key.compareTo(sameValueNode.keys.get(0)) == 0) {
                    for (int i = 0; i < sameValueNode.keys.size(); i++) {
                        if (key.compareTo(sameValueNode.keys.get(i)) != 0) break;

                        answer.add(sameValueNode.values.get(i));
                    }

                    sameValueNode = sameValueNode.next;
                }
            }
            LeafNode temp = getNextLeaf(comparator);
            while (temp != null) {
                if (comparator.equals(EQUAL) &&
                        key.compareTo(temp.keys.get(0)) != 0) {
                    break;
                }

                int start = comparator.equals(LESS) ? temp.keys.size() - 1 : 0;
                answer.addAll(temp.getRangeValues(comparator, start));
                temp = temp.getNextLeaf(comparator);
            }

            return answer;
        }

        /**
         * Returns a list of values from the index, start, as long as
         * the condition is met
         *
         * @param comparator one of "<=", "==", or ">="
         * @param start starting index to collect the value from
         * @return a list of values that satisfy the condition
         */
        private List<V> getRangeValues(String comparator, int start) {
            List<V> answer = new ArrayList<>();

            if (comparator.equals(LESS)) {
                for (int i = start; 0 <= i; i--) {
                    answer.add(values.get(i));
                }

            } else if (comparator.equals(GREATER)){
                for (int i = start; i < keys.size(); i++) {
                    answer.add(values.get(i));
                }

            } else {
                for (int i = start; i < keys.size(); i++) {
                    if (keys.get(start).compareTo(keys.get(i)) != 0) {
                        return answer;
                    }

                    answer.add(values.get(i));
                }
            }

            return answer;
        }

        /**
         * get the next leaf depending on the comparator
         * for example, if the comparator is "<=", the next leaf would be
         * the previous one.
         *
         * @param comparator
         * @return the next node depending on the comparator
         */
        private LeafNode getNextLeaf(String comparator) {
            return comparator.equals(LESS) ? previous : next;
        }
        
    } // End of class LeafNode
    
    
    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(3);

        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Double j = dd[rnd1.nextInt(4)];
            list.add(j);
            System.out.println("value is => " + j);
            bpTree.insert(j, j);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
        }
        List<Double> filteredValues = bpTree.rangeSearch(0.2d, ">=");
        System.out.println("Filtered values: " + filteredValues.toString());
    }

} // End of class BPTree
