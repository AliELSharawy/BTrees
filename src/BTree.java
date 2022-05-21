public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

    @Override
    public int getMinimumDegree() {
        return 0;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return null;
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
}
