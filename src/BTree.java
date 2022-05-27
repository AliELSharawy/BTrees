import javax.management.RuntimeErrorException;
import java.util.Collections;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
    private IBTreeNode<K, V> root;
    private final int minimumDegree;

    public BTree(int minimumDegree) {
        if (minimumDegree < 2)
            throw new RuntimeErrorException(null);
        this.minimumDegree = minimumDegree;
        this.root = new BTreeNode<>();
    }

    @Override
    public int getMinimumDegree() {
        return this.minimumDegree;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return this.root;
    }

    @Override
    public void insert(K key, V value) {
        if (key == null || value == null)
            throw new RuntimeErrorException(null);

        if (search(key) != null) {
            System.out.println("Key is already present.");
            return;
        }

        if (root == null) {
            root = new BTreeNode<>();
            root.getKeys().add(key);
            root.getValues().add(value);
            root.setNumOfKeys(1);
        }

        if (root.getNumOfKeys() == (minimumDegree * 2 - 1)) {
            IBTreeNode<K, V> root = new BTreeNode<>();
            root.setLeaf(false);
            root.getChildren().add(this.root);
            splitChild(root, 0);
            if (root.getKeys().get(0).compareTo(key) < 0)
                insertNonFull(root.getChildren().get(1), key, value);
            else insertNonFull(root.getChildren().get(0), key, value);
            this.root = root;
        } else insertNonFull(this.root, key, value);
    }

    /**
     * Function to help with insertion.
     * Given the index of the child, this method splits a node
     * into two nodes and maintains the BTree property.
     */
    private void splitChild(IBTreeNode<K, V> ibTreeNode, int index) {
        IBTreeNode<K, V> leftChild, rightChild;
        leftChild = ibTreeNode.getChildren().get(index);
        rightChild = new BTreeNode<>();

        rightChild.setLeaf(leftChild.isLeaf());

        // Set the keys and values of the right child
        rightChild.getKeys().addAll(leftChild.getKeys().subList(minimumDegree, (minimumDegree * 2 - 1)));
        rightChild.getValues().addAll(leftChild.getValues().subList(minimumDegree, (minimumDegree * 2 - 1)));
        rightChild.setNumOfKeys(rightChild.getKeys().size());

        // Modify the keys and values of the left child.
        leftChild.getKeys().subList(minimumDegree, (minimumDegree * 2 - 1)).clear();
        leftChild.getValues().subList(minimumDegree, (minimumDegree * 2 - 1)).clear();

        // Set the children of the left and right children
        if (!leftChild.isLeaf()) {
            rightChild.getChildren().addAll(leftChild.getChildren().subList(minimumDegree, minimumDegree * 2));
            leftChild.getChildren().subList(minimumDegree, minimumDegree * 2).clear();
        }

        // Modify the node children, then keys and values.
        ibTreeNode.getChildren().add(index + 1, rightChild);
        ibTreeNode.getKeys().add(index, leftChild.getKeys().remove(minimumDegree - 1));
        ibTreeNode.getValues().add(index, leftChild.getValues().remove(minimumDegree - 1));
        ibTreeNode.setNumOfKeys(ibTreeNode.getNumOfKeys() + 1);
        leftChild.setNumOfKeys(leftChild.getKeys().size());
    }

    /**
     * Function to help with insertion.
     */
    private void insertNonFull(IBTreeNode<K, V> ibTreeNode, K key, V value) {
        if (ibTreeNode.isLeaf()) {
            ibTreeNode.getKeys().add(key);
            Collections.sort(ibTreeNode.getKeys());
            ibTreeNode.getValues().add(ibTreeNode.getKeys().indexOf(key), value);
            ibTreeNode.setNumOfKeys(ibTreeNode.getNumOfKeys() + 1);
        } else {
            int index = ibTreeNode.getNumOfKeys() - 1;

            // Find the child to have the new key.
            while (index >= 0 && ibTreeNode.getKeys().get(index).compareTo(key) > 0)
                index--;

            // Check if the child is full.
            if (ibTreeNode.getChildren().get(index + 1).getNumOfKeys() == (2 * this.minimumDegree - 1)) {
                // If the child is full, split.
                splitChild(ibTreeNode, index + 1);

                // After split, the middle key of C[i] goes up and
                // C[i] is split into two. See which of the two
                // is going to have the new key
                if (ibTreeNode.getKeys().get(index + 1).compareTo(key) < 0)
                    index++;
            }
            insertNonFull(ibTreeNode.getChildren().get(index + 1), key, value);
        }

    }

    @Override
    public V search(K key) {
        if (key == null)
            throw new RuntimeErrorException(null);

        return search(root, key);
    }

    private V search(IBTreeNode<K, V> ibTreeNode, K key) {
        if (key == null)
            throw new RuntimeErrorException(new Error());
        if (root == null)
            return null;
        IBTreeNode<K, V> root = this.root;
        int index;
        while (!root.isLeaf()) {
            index = 0;
            while (index < root.getNumOfKeys() && key.compareTo(root.getKeys().get(index)) > 0) {
                index++;
            }
            if (index < root.getNumOfKeys() && root.getKeys().get(index).compareTo(key) == 0) {
                return root.getValues().get(index);
            }
            root = root.getChildren().get(index);
        }
        index = 0;
        while (index < root.getNumOfKeys() && key.compareTo(root.getKeys().get(index)) > 0) {
            index++;
        }
        if (index < root.getNumOfKeys() && root.getKeys().get(index).compareTo(key) == 0) {
            return root.getValues().get(index);
        }
        return null;
    }

    @Override
    public boolean delete(K key) {
        IBTreeNode<K, V> toBeDeleted = search_node(root, key);
        if (toBeDeleted == null)
            return false;
        List<K> keys = toBeDeleted.getKeys();
        int position = -1;
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).compareTo(key) == 0) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            return false;
        }
        delete(root, key);
        iterateAndFix(root);
        return true;
    }

    private int find(IBTreeNode<K, V> node, K key) {
        List<K> keys = node.getKeys();
        int position = -1;
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).compareTo(key) == 0) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void delete(IBTreeNode<K, V> root, K key) {
        int pos = find(root, key);
        if (pos != -1) {//case 1
            if (root.isLeaf()) {
                deleteLeaf(root, pos);
                return;
            }
            if (!root.isLeaf()) {//case 2
                IBTreeNode<K, V> suc = successor(root.getChildren().get(pos));
                IBTreeNode<K, V> pre = predecessor(root.getChildren().get(pos + 1));
                if (pre.getKeys().size() > minimumDegree - 1) {
                    K pre_key = pre.getKeys().get(0);
                    V pre_value = pre.getValues().get(0);
                    root.getKeys().set(pos, pre_key);
                    root.getValues().set(pos, pre_value);
                    delete(root.getChildren().get(pos + 1), pre_key);
                    reArrange(root, pos);
                } else {
                    K suc_key = suc.getKeys().get(suc.getKeys().size() - 1);
                    V suc_value = suc.getValues().get(suc.getKeys().size() - 1);
                    root.getKeys().set(pos, suc_key);
                    root.getValues().set(pos, suc_value);
                    delete(root.getChildren().get(pos), suc_key);
                    reArrange(root, pos);
                }
            }
        } else {//move down in tree and change value
            //get changed position
            int i = 0;
            for (K nodeKey : root.getKeys()) {
                if (key.compareTo(nodeKey) > 0)
                    i++;
                else break;
            }
            delete(root.getChildren().get(i), key);
            reArrange(root, i);
        }
    }

    private IBTreeNode<K, V> search_node(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        int i;
        for (i = 0; i < keys.size(); i++) {
            if (key.compareTo(keys.get(i)) < 0) {
                break;
            }
            if (key.compareTo(keys.get(i)) == 0) {
                return x;
            }
        }
        if (x.isLeaf()) {
            return null;
        } else {
            return search_node(x.getChildren().get(i), key);
        }
    }

    public void reArrange(IBTreeNode<K, V> node, int index) {
        IBTreeNode<K, V> leftChild;
        IBTreeNode<K, V> rightChild;
        if (index == node.getKeys().size()) {
            leftChild = node.getChildren().get(index - 1);
            rightChild = node.getChildren().get(index);
            index--;
        } else {
            leftChild = node.getChildren().get(index);
            rightChild = node.getChildren().get(index + 1);
        }
        if (((leftChild.getKeys().size()) >= minimumDegree - 1) && (rightChild.getKeys().size()) >= minimumDegree - 1)//no need to rearrange tree
            return;
        if (leftChild.getKeys().size() > minimumDegree - 1) {
            K donated_key = leftChild.getKeys().get(leftChild.getKeys().size() - 1);
            V donated_val = leftChild.getValues().get(leftChild.getKeys().size() - 1);
            K parent_key = node.getKeys().get(index);
            V parent_val = node.getValues().get(index);
            leftChild.getKeys().remove(leftChild.getKeys().size() - 1);
            node.getKeys().set(index, donated_key);
            node.getValues().set(index, donated_val);
            rightChild.getKeys().add(0, parent_key);
            rightChild.getValues().add(0, parent_val);
            if (!leftChild.isLeaf()) {
                int leftChildN = leftChild.getChildren().size();
                IBTreeNode<K, V> maxChild = leftChild.getChildren().remove(leftChildN - 1);
                rightChild.getChildren().add(0, maxChild);
            }
        } else if (rightChild.getKeys().size() > minimumDegree - 1) {
            K donated_key = rightChild.getKeys().get(0);
            V donated_val = rightChild.getValues().get(0);
            K parent_key = node.getKeys().get(index);
            V parent_val = node.getValues().get(index);
            rightChild.getKeys().remove(0);
            node.getKeys().set(index, donated_key);
            node.getValues().set(index, donated_val);
            leftChild.getKeys().add(parent_key);
            leftChild.getValues().add(parent_val);
            if (!rightChild.isLeaf()) {
                IBTreeNode<K, V> minChild = rightChild.getChildren().remove(0);
                leftChild.getChildren().add(minChild);
            }
        } else {
            K key = node.getKeys().get(index);
            V value = node.getValues().get(index);
            leftChild.getKeys().add(key);
            leftChild.getValues().add(value);
            node.getKeys().remove(index);
            node.getValues().remove(index);
            node.getChildren().remove(index + 1);
            leftChild.getChildren().addAll(rightChild.getChildren());
            leftChild.getValues().addAll(rightChild.getValues());
            leftChild.getKeys().addAll(rightChild.getKeys());
            if (root.getKeys().size() == 0) {
                root = leftChild;
                root.setLeaf(true);
            }
        }
    }

    private IBTreeNode<K, V> successor(IBTreeNode<K, V> node) {
        if (node.isLeaf()) {
            return node;
        }
        return successor(node.getChildren().get(0));
    }

    private IBTreeNode<K, V> predecessor(IBTreeNode<K, V> node) {
        if (node.isLeaf()) {
            return node;
        }
        return predecessor(node.getChildren().get(node.getNumOfKeys()));
    }

    private void deleteLeaf(IBTreeNode<K, V> root, int index) {
        root.getKeys().remove(index);
        root.getValues().remove(index);
        root.setNumOfKeys(root.getNumOfKeys() - 1);
    }

    private void iterateAndFix(IBTreeNode<K, V> root) {
        if (root.getChildren().size() == 0) {
            root.setLeaf(true);
            root.setNumOfKeys(root.getKeys().size());
        } else {
            root.setLeaf(false);
            root.setNumOfKeys(root.getKeys().size());
        }
        for (int i = 0; i < root.getChildren().size(); i++) {
            iterateAndFix(root.getChildren().get(i));
        }
    }

    /**
     * Pre-order traverse for a Btree.
     */
    public void traverse() {
        traverse(this.root);
    }


    private void traverse(IBTreeNode<K, V> root) {
        System.out.println();
        System.out.print(root);
        System.out.print(" --> " + root.getKeys());
        System.out.println(", Number of Keys = " + root.getNumOfKeys());
        System.out.println("Values = " + root.getValues());
        System.out.println("Children = " + root.getChildren());

        for (IBTreeNode<K, V> child : root.getChildren()) {
            traverse(child);
        }
    }
}
