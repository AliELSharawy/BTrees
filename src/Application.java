public class Application {
    public static void main(String[] args) {
        BTree<Integer, String> ibTree = new BTree<>(3);

        ibTree.insert(5, "Hello");
        ibTree.insert(10, "Hello");
        ibTree.insert(53, "Hello");
        ibTree.insert(21, "Hello");
        ibTree.insert(2, "Hello");
//        ibTree.insert(0, "Hello");
//        ibTree.insert(1, "Hello");
//        ibTree.insert(71, "Hello");
//        ibTree.insert(6, "Hello");
//        ibTree.insert(7, "Hello");
//        ibTree.insert(8, "Hello");
//
//        ibTree.traverse(ibTree.getRoot());
//
//        ibTree.insert(9, "Hello");
//        ibTree.insert(100, "Hello");
//        ibTree.insert(50, "Hello");
//        ibTree.insert(3, "Hello");

        ibTree.traverse(ibTree.getRoot());
//        System.out.println(ibTree.search(10));
    }
}