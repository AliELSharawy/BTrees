import javax.management.RuntimeErrorException;
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

        if (root == null) {
            root = new BTreeNode<>();
            root.getKeys().add(key);
            root.getValues().add(value);
            root.setNumOfKeys(1);
        }

        if (root.getNumOfKeys() == (minimumDegree * 2 - 1)) {
            IBTreeNode<K, V> root = this.root;
            this.root = new BTreeNode<>();
            this.root.getChildren().add(root);
            splitChild(this.root, 0);
        }

        IBTreeNode<K, V> nonFull = insertNonFull(this.root, key);
        if (nonFull != null) {
            int index = 0;
            for (K nonFullKey : nonFull.getKeys())
                if (key.compareTo(nonFullKey) > 0)
                    index++;
            if (index < nonFull.getNumOfKeys() && key.compareTo(nonFull.getKeys().get(index)) == 0)
                return;     // Key is already present.
            else {
                nonFull.getKeys().add(key);
                nonFull.getValues().add(value);
                nonFull.setNumOfKeys(nonFull.getNumOfKeys() + 1);
            }
        }
    }

    @Override
    public V search(K key) {
        if (key == null)
            throw new RuntimeErrorException(null);

        return search(root, key);
    }

    private V search(IBTreeNode<K, V> ibTreeNode, K key) {
        int index = 0;
        for (K bTreeKey : ibTreeNode.getKeys()) {
            if (bTreeKey.compareTo(key) > 0) {
                index = ibTreeNode.getKeys().indexOf(bTreeKey);
                break;
            }
        }
        if (index < ibTreeNode.getNumOfKeys() && key == ibTreeNode.getKeys().get(index))
            return ibTreeNode.getValues().get(index);
        else if (ibTreeNode.isLeaf())
            return null;
        else return search(ibTreeNode.getChildren().get(index + 1), key);
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
     * Pre-order traverse for a Btree.
     */
    public void traverse(IBTreeNode<K, V> root) {
        System.out.println();
        System.out.println("Node = " + root);
        System.out.println("Children = " + root.getChildren());
        System.out.println("Number of Keys = " + root.getNumOfKeys());
        System.out.println("Keys = " + root.getKeys());

        for (IBTreeNode<K, V> child : root.getChildren()) {
            traverse(child);
        }
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

        // Set the children of the right child
        if (!leftChild.isLeaf()) {
            rightChild.getChildren().addAll(leftChild.getChildren().subList(minimumDegree, minimumDegree * 2));
        }

        // Modify the node children, then keys and values.
        ibTreeNode.getChildren().add(index + 1, rightChild);
        ibTreeNode.getKeys().add(index, leftChild.getKeys().get(minimumDegree - 1));
        ibTreeNode.getValues().add(index, leftChild.getValues().get(minimumDegree - 1));
        ibTreeNode.setNumOfKeys(ibTreeNode.getNumOfKeys() + 1);

        // Modify the keys, values, and children of the left child.
        leftChild.getKeys().removeAll(rightChild.getKeys());
        leftChild.getValues().removeAll(rightChild.getValues());
        leftChild.setNumOfKeys(leftChild.getKeys().size());
        leftChild.getChildren().removeAll(rightChild.getChildren());
    }

    /**
     * Function to help with insertion.
     */
    private IBTreeNode<K, V> insertNonFull(IBTreeNode<K, V> node, K key) {
        if (node.isLeaf())
            return node;

        int index = 0;
        for (K nodeKey : node.getKeys())
            if (key.compareTo(nodeKey) > 0)
                index++;

        if (index < node.getNumOfKeys() && key.compareTo(node.getKeys().get(index)) == 0)
            return null;
        else {
            if (node.getChildren().get(index).getNumOfKeys() == (minimumDegree * 2 - 1))
                splitChild(node, index);

            if (index < node.getNumOfKeys() && key.compareTo(node.getKeys().get(index)) > 0)
                index++;
            else if ((index < node.getNumOfKeys() && key.compareTo(node.getKeys().get(index)) == 0))
                return null;

            return insertNonFull(node.getChildren().get(index), key);
        }
    }
}
