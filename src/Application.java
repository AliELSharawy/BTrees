import java.util.List;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        // This is the section that tests insertions in a B-Tree.
        // Specify the minimum degree below.
        int minimumDegree = 2;

        BTree<Integer, String> ibTree = new BTree<>(minimumDegree);

        System.out.println("\n================= TESTING INSERTION =================");
        ibTree.insert(5, "Hello");
        ibTree.insert(10, "Excel");
        ibTree.insert(53, "Word");
        ibTree.insert(21, "VS");
        ibTree.insert(2, "Mr Nobody");
        ibTree.insert(0, "Mrs Nobody");
        ibTree.insert(1, "Someone");
        ibTree.insert(71, "Another one");
        ibTree.insert(6, "This is Six");
        ibTree.insert(7, "Playground");

        // Trying to insert 71 again invokes an error message,
        // but the application continues execution.
        ibTree.insert(71, "Hey");

        ibTree.insert(8, "Testing");
        ibTree.insert(9, "Insertion");
        ibTree.insert(100, "B-Tree");
        ibTree.insert(50, "Last but not least");
        ibTree.insert(3, "Finally");

        ibTree.traverse();  // Print the tree.

        // Testing search on the same tree.
        System.out.println("\n================= TESTING SEARCH =================");
        System.out.println(ibTree.search(10));      // A key is present.
        System.out.println(ibTree.search(1600));    // A key is not present.


        System.out.println("\n================= TESTING DELETION =================");

        // Testing deletion of a non-existing key.
        System.out.println("\n================= DELETING 80 (NOT PRESENT) =================");
        System.out.println(ibTree.delete(80) ? "80 is found and deleted." : "80 is not found.");

        // Testing deletion. This case covers (if not all) most of the deletion cases.
        System.out.println("\n================= DELETING 5 =================");
        ibTree.delete(5);
        ibTree.traverse();      // Prints the tree.
        System.out.println("\n================= DELETING 10 =================");
        ibTree.delete(10);
        ibTree.traverse();
        System.out.println("\n================= DELETING 53 =================");
        ibTree.delete(53);
        ibTree.traverse();
        System.out.println("\n================= DELETING 21 =================");
        ibTree.delete(21);
        ibTree.traverse();
        System.out.println("\n================= DELETING 02 =================");
        ibTree.delete(2);
        ibTree.traverse();
        System.out.println("\n================= DELETING 00 =================");
        ibTree.delete(0);
        ibTree.traverse();
        System.out.println("\n================= DELETING 01 =================");
        ibTree.delete(1);
        ibTree.traverse();
        System.out.println("\n================= DELETING 71 =================");
        ibTree.delete(71);
        ibTree.traverse();
        System.out.println("\n================= DELETING 06 =================");
        ibTree.delete(6);
        ibTree.traverse();
        System.out.println("\n================= DELETING 07 =================");
        ibTree.delete(7);
        ibTree.traverse();
        System.out.println("\n================= DELETING 08 =================");
        ibTree.delete(8);
        ibTree.traverse();
        System.out.println("\n================= DELETING 09 =================");
        ibTree.delete(9);
        ibTree.traverse();
        System.out.println("\n================= DELETING 100 =================");
        ibTree.delete(100);
        ibTree.traverse();
        System.out.println("\n================= DELETING 50 =================");
        ibTree.delete(50);
        ibTree.traverse();
        System.out.println("\n================= DELETING 03 =================");
        ibTree.delete(3);
        ibTree.traverse();

        // Testing the search engine.
        System.out.println("\n================= TESTING SEARCH ENGINE =================");
        String filePath = "Wikipedia Data Sample\\wiki_11";
        SearchEngine searchEngine = new SearchEngine(2);
        searchEngine.indexWebPage(filePath);
        print(searchEngine.searchByMultipleWordWithRanking("ship is red"), "ship is red");
        print(searchEngine.searchByMultipleWordWithRanking("Donald"), "Donald");
    }

    /**
     * A function that helps in search engine.
     */
    public static void print(List<ISearchResult> res, String w) {
        System.out.println(w);
        if (res == null) {
            System.out.println("not found");
            return;
        }
        for (ISearchResult re : res) {
            System.out.println(re.getId() + "->" + re.getRank());
        }
        System.out.println();
    }
}