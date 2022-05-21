public interface IBTree<K extends Comparable<K>, V> {
    /*
     * @return the minimum degree of the given Btree.
     * The minimum degree (m) is sent as a parameter to the constructor.
     * */
    public int getMinimumDegree();

    /*
     * @return the root of a given Btree.
     * */
    public IBTreeNode<K, V> getRoot();

    /*
     * Inserts the given key in the Btree. If the key is already in the
     * Btree, ignore the call of this method.
     * @param key.
     * @param value.
     * */
    public void insert(K key, V value);

    /*
     * Searches for a given key in the Btree.
     * @param key.
     * @return V value.
     * */
    public V search(K key);

    /*
     * Deletes the node of a given key in the Btree.
     * Returns true in case of a successful deletion, false otherwise.
     * @param key.
     * @return boolean.
     * */
    public boolean delete(K key);
}