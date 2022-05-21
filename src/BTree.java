import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
    private BTreeNode<K, V> root;
    private final int minimumDegree;

    public BTree(int minimumDegree) {
        this.minimumDegree = minimumDegree;
        this.root = new BTreeNode<>(minimumDegree);
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

    }

    @Override
    public V search(K key) {
        return null;
    }

    @Override
    public boolean delete(K key) {
        return false;
    }

    /* TODO: Not fully implemented! Only inserts a key to a given node. No handling for recursion yet.
     * @param insertedKey.
     * @param insertedValue.
     * @param bTreeNode.
     * */
    public BTreeNode<K, V> insertInNode(K insertedKey, V insertedValue, BTreeNode<K, V> bTreeNode) {
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

    /* Promotes the middle element of a node. */
    public void promote() {

    }

    /* Splits the node into two nodes and maintains the BTree property. */
    public void split() {

    }
}
