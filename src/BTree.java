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
        if (index <= ibTreeNode.getNumOfKeys() && key.compareTo( ibTreeNode.getKeys().get(index))==0)
            return ibTreeNode.getValues().get(index);
        else if (ibTreeNode.isLeaf())
            return null;
        else return search(ibTreeNode.getChildren().get(index), key);
    }
    private IBTreeNode search_node(IBTreeNode x, K key) {
        List<K> keys=x.getKeys();
        int i=0;
        if (x == null)
            return x;
        for ( i = 0; i < keys.size(); i++) {
            if (key.compareTo(keys.get(i))>0) {
                break;
            }
            if (key.compareTo(keys.get(i))==0) {
                return x;
            }
        }
        if (x.isLeaf()) {
            return null;
        } else {
            return  search_node((IBTreeNode) x.getChildren().get(i), key);
        }
    }

    @Override
    public boolean delete(K key) {
        IBTreeNode toBeDeleted= search_node(root,key);
        if (toBeDeleted==null)
            return false;
        List<K> keys=toBeDeleted.getKeys();
        int position=-1;
        for (int i = 0; i <keys.size() ; i++) {
            if(keys.get(i).compareTo(key)==0){
                position=i;
                break;
            }
        }
        if (position==-1){
            return false;
        }
        delete(root,key);
        return true;
    }
    private int find(IBTreeNode node,K key){
        List<K> keys=node.getKeys();
        int position=-1;
        for (int i = 0; i <keys.size() ; i++) {
            if(keys.get(i).compareTo(key)==0){
                position=i;
                break;
            }
        }
        return position;
    }
    private void delete(IBTreeNode root,K key){
        int pos=find(root,key);
        if(pos!=-1){//case 1
            if(root.isLeaf()){
                deleteLeaf(root,pos);
                return;
            }
            if (!root.isLeaf()) {//case 2
                IBTreeNode suc=successor((IBTreeNode) root.getChildren().get(pos));
                IBTreeNode pre = predecessor((IBTreeNode) root.getChildren().get(pos+1));
                if(pre.getKeys().size()>minimumDegree){
                    K pre_key= (K) pre.getKeys().get(pre.getKeys().size()-1);
                    V pre_value=(V) pre.getKeys().get(pre.getKeys().size()-1);
                    delete(root,pre_key);
                    root.getKeys().set(pos,pre_key);
                    root.getValues().set(pos,pre_value);
                    return;
                }
                else {
                    K suc_key= (K) pre.getKeys().get(0);
                    V suc_value=(V) pre.getKeys().get(0);
                    delete(root,suc_key);
                    root.getKeys().set(pos,suc_key);
                    root.getValues().set(pos,suc_value);
                    return;
                }
            }
        }else{//move down in tree and change value
            //get changed position
            int changed_position=0;
            int i=0;
            for (i = 0; i < root.getNumOfKeys() ; i++) {
                if(key.compareTo((K) root.getKeys().get(i))>0){
                    break;
                }
            }
            delete((IBTreeNode) root.getChildren().get(i), key);
            reArrange(root,i);
        }

    }
    public void reArrange(IBTreeNode node,int index){
        IBTreeNode leftChild=root.getChildren().get(index);
        IBTreeNode rightChild =root.getChildren().get(index+1);
        if(leftChild.getKeys().size()>=minimumDegree&&rightChild.getKeys().size()>=minimumDegree)//no need to rearrange tree
            return;
        if(leftChild.getKeys().size()>minimumDegree){
               K donated_key= (K) leftChild.getKeys().get(leftChild.getNumOfKeys()-1);
               V donated_val=(V) leftChild.getValues().get(leftChild.getNumOfKeys()-1);
               K parent_key= (K) node.getKeys().get(index);
               V parent_val= (V) node.getValues().get(index);
               leftChild.getKeys().remove(leftChild.getNumOfKeys()-1);
               node.getKeys().set(index,donated_key);
               node.getValues().set(index,donated_val);
               rightChild.getKeys().add(0,parent_key);
               rightChild.getValues().add(0,parent_val);
               if(!leftChild.isLeaf()){
                   int leftChildN=leftChild.getChildren().size();
                   IBTreeNode maxChild= (IBTreeNode) leftChild.getChildren().get(leftChildN-1);
                   rightChild.getChildren().add(0,maxChild);
                   leftChild.getChildren().remove(leftChildN-1);
               }
        }
        if(rightChild.getKeys().size()>minimumDegree){
            K donated_key= (K) rightChild.getKeys().get(0);
            V donated_val=(V) rightChild.getValues().get(0);
            K parent_key= (K) node.getKeys().get(index);
            V parent_val= (V) node.getValues().get(index);
            rightChild.getKeys().remove(0);
            node.getKeys().set(index,donated_key);
            node.getValues().set(index,donated_val);
            rightChild.getKeys().add(parent_key);
            rightChild.getValues().add(parent_val);
            if(!rightChild.isLeaf()){

                IBTreeNode minChild= (IBTreeNode) leftChild.getChildren().get(0);
                rightChild.getChildren().add(0,minChild);
                leftChild.getChildren().remove(0);
            }
        }
        else{
            leftChild.getKeys().add(node.getKeys().get(index));
            leftChild.getValues().add(node.getValues().get(index));
            node.getKeys().remove(index);
            node.getValues().remove(index);
            node.getChildren().remove(index+1);
            leftChild.getChildren().addAll(rightChild.getChildren());
            leftChild.getValues().addAll(rightChild.getValues());
            leftChild.getKeys().addAll(rightChild.getKeys());
        }
        return;
    }
    private IBTreeNode successor(IBTreeNode node){
        if(node.isLeaf()){
            return node;
        }
        return successor((IBTreeNode) node.getChildren().get(0));
    }
    private IBTreeNode predecessor(IBTreeNode node){
        if(node.isLeaf()){
            return node;
        }
        return predecessor((IBTreeNode) node.getChildren().get(node.getNumOfKeys()));
    }
    private void deleteLeaf(IBTreeNode root,int index){
        root.getKeys().remove(index);
        root.getValues().remove(index);
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
