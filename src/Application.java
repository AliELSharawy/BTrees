import java.util.List;

public class Application {
    public static void main(String[] args) {
        BTree<Integer, String> ibTree = new BTree<>(2);

        ibTree.insert(5, "Hello");
        ibTree.insert(10, "Excel");
        ibTree.insert(53, "Word");
        ibTree.insert(21, "VS");
        ibTree.insert(2, "Hello");
        ibTree.insert(0, "Hello");
        ibTree.insert(1, "Hello");
        ibTree.insert(71, "Hello");
        ibTree.insert(6, "Hello");
        ibTree.insert(7, "Hello");
        ibTree.insert(8, "Hello");
        ibTree.insert(9, "Hello");
        ibTree.insert(100, "Hello");
        ibTree.insert(50, "Hello");
        ibTree.insert(3, "Hello");

        ibTree.traverse(ibTree.getRoot());
//        System.out.println(ibTree.search(10));
//        System.out.println(ibTree.search(1600));
//        ibTree.insert(50, "Hey");

        System.out.println("================= AFTER DELETION =================");

        ibTree.delete(5);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(10);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(53);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(21);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(2);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(0);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(1);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        System.out.println("DELETING 71");
        ibTree.delete(71);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        System.out.println("DELETING 6");
        ibTree.delete(6);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(7);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(8);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(9);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(100);
        ibTree.traverse(ibTree.getRoot());
        System.out.println("================= =================");
        ibTree.delete(50);
        ibTree.traverse(ibTree.getRoot());
        ibTree.delete(3);
        ibTree.traverse(ibTree.getRoot());

        String filePath = "Wikipedia Data Sample\\wiki_11";
        SearchEngine searchEngine = new SearchEngine(2);
        searchEngine.indexWebPage(filePath);
        print(searchEngine.searchByMultipleWordWithRanking("ship is red"), "ship is red");
        print(searchEngine.searchByMultipleWordWithRanking("Donald"), "Donald");

        System.out.println(ibTree.search(50));
    }

    public static void print(List<ISearchResult> res, String w) {
        System.out.println(w);
        if (res == null) {
            System.out.println("not found");
            return;
        }
        for (ISearchResult re : res) {
            System.out.println(re.getId() + "->" + re.getRank());
        }
        System.out.println("");
    }
}