import java.util.ArrayList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
    private BTreeNode<K, V> root;
    private final int minimumDegree;

    public BTree(int minimumDegree) {
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
        IBTreeNode<K, V> root = this.root;
        if (root.getNumOfKeys() == minimumDegree - 1) {
            this.root = new BTreeNode<>();
            this.root.setLeaf(false);
            this.root.setNumOfKeys(0);
            List<IBTreeNode<K, V>> children = new ArrayList<>();
            children.add(root);
            this.root.setChildren(children);
            splitChild(this.root, 1);
            insertNonFull(this.root, key);
        } else insertNonFull(root, key);
    }

    @Override
    public V search(K key) {
        return search(root, key);
    }

    public V search(IBTreeNode<K, V> ibTreeNode, K key) {
        int index = 0;
        for (K bTreeKey : ibTreeNode.getKeys()) {
            if (bTreeKey.compareTo(key) < 0)
                index++;
            else break;
        }
        if (index <= ibTreeNode.getNumOfKeys() && key == ibTreeNode.getKeys().get(index))
            return ibTreeNode.getValues().get(index);
        else if (ibTreeNode.isLeaf())
            return null;
        else return search(ibTreeNode.getChildren().get(index), key);
    }

    @Override
    public boolean delete(K key) {
        return false;
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
        int newNumOfKeys = leftChild.getNumOfKeys() / 2;
        rightChild.setNumOfKeys(newNumOfKeys);

        // Set the keys of the right child
        List<K> keys = new ArrayList<>();
        for (int j = 0; j < newNumOfKeys; j++)
            keys.add(leftChild.getKeys().get(j + newNumOfKeys));
        rightChild.setKeys(keys);

        // Set the children of the right child
        List<IBTreeNode<K, V>> children = new ArrayList<>();
        if (!leftChild.isLeaf()) {
            for (int i = 0; i <= newNumOfKeys; i++)
                children.add(leftChild.getChildren().get(i + newNumOfKeys));
            rightChild.setChildren(children);
        }

        // Modify the number of keys of the left child.
        leftChild.setNumOfKeys(newNumOfKeys);

        // Modify the node children, then keys.
        List<IBTreeNode<K, V>> ibTreeNodeChildren = ibTreeNode.getChildren();
        ibTreeNodeChildren.add(index + 1, rightChild);
        ibTreeNode.setChildren(ibTreeNodeChildren);

        List<K> ibTreeNodeKeys = ibTreeNode.getKeys();
        ibTreeNodeKeys.add(index, leftChild.getKeys().get(newNumOfKeys - 1));
        ibTreeNode.setKeys(ibTreeNodeKeys);

        ibTreeNode.setNumOfKeys(ibTreeNode.getNumOfKeys() + 1);
    }

    /**
     * Function to help with insertion.
     */
    private void insertNonFull(IBTreeNode<K, V> node, K key) {
        int index = node.getNumOfKeys();
        if (node.isLeaf()) {
            List<K> keys = node.getKeys();
            for (; index >= 1; index--) {
                if (key.compareTo(keys.get(index)) < 0)
                    keys.set(index + 1, keys.get(index));
                else break;
            }
            keys.add(index + 1, key);
            node.setKeys(keys);
            node.setNumOfKeys(node.getNumOfKeys() + 1);
        } else {
            List<K> keys = node.getKeys();
            for (; index >= 1; index--)
                if (key.compareTo(keys.get(index)) >= 0)
                    break;
            index++;
            if (node.getChildren().get(index).getNumOfKeys() == minimumDegree - 1) {
                splitChild(node, index);
                if (key.compareTo(keys.get(index)) > 0)
                    index++;
            }
            insertNonFull(node.getChildren().get(index), key);
        }
    }

    /* TODO: Not fully implemented! Only inserts a key to a given node. No handling for recursion yet.
     * @param insertedKey.
     * @param insertedValue.
     * @param bTreeNode.
     * */
    private BTreeNode<K, V> insertInNode(K insertedKey, V insertedValue, BTreeNode<K, V> bTreeNode) {
        int lastKeyIndex = bTreeNode.getKeys().size() - 1;
        K lastKey = bTreeNode.getKeys().get(lastKeyIndex);
        List<K> keys = bTreeNode.getKeys();
        if (lastKey.compareTo(insertedKey) < 0)
            keys.add(lastKey);
        else if (lastKey.compareTo(insertedKey) > 0) {
            for (K key : keys)
                if (key.compareTo(insertedKey) > 0) {
                    int keyIndex = keys.indexOf(key);
                    keys.add(keyIndex, insertedKey);
                    break;
                }
        } else {
            // Key is already in the Btree.
            return bTreeNode;
        }
        int keysLength = keys.size();
        if (keysLength > this.minimumDegree) {
            // TODO: Call promote then split.
        } else {
            bTreeNode.setNumOfKeys(keysLength);
            bTreeNode.setKeys(keys);
        }
        return bTreeNode;
    }

}
