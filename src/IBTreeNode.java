import java.util.List;

public interface IBTreeNode<K extends Comparable<K>, V> {
    /*
     * @return the numOfKeys return number of keys in this node.
     */
    public int getNumOfKeys();

    /*
     * @param numOfKeys
     */
    public void setNumOfKeys(int numOfKeys);

    /*
     * @return isLeaf if the node is a leaf or not.
     */
    public boolean isLeaf();

    /*
     * @param isLeaf.
     */
    public void setLeaf(boolean isLeaf);

    /*
     * @return the list of keys of a given node.
     */
    public List<K> getKeys();

    /*
     * @param list of keys to set the keys.
     */
    public void getKeys(List<K> keys);

    /*
     * @return the list of values of a given node.
     */
    public List<V> getValues();

    /*
     * @param list of values to set the values.
     */
    public void setValues(List<V> values);

    /*
     * @return the list of children nodes of a given node.
     */
    public List<IBTreeNode<K, V>> getChildren();

    /*
     * @param list of children nodes to set the children.
     */
    public void setChildren(List<IBTreeNode<K, V>> children);
}
