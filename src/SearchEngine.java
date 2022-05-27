import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SearchEngine implements ISearchEngine {

    private final BTree<String, LinkedHashMap<Integer, Integer>> webSiteDocuments;

    public SearchEngine(int minDegree) {
        webSiteDocuments = new BTree<>(minDegree);
    }

    @Override
    public void indexWebPage(String filePath) {
        LinkedHashMap<Integer, String> indexedDoc = new LinkedHashMap<>();
        File inputFile = new File(filePath);
        try {
            parseXML(inputFile, indexedDoc);
        } catch (Exception e) {
            System.out.println("error");
        }
        // insert id & doc to b tree
        indexedDoc.forEach((id, doc) -> {
            String[] split = splitStr(doc);
            for (String s : split) {
                if (!Objects.equals(s, "")) {
                    if (webSiteDocuments.search(s.toLowerCase()) == null) {
                        webSiteDocuments.insert(s.toLowerCase(), new LinkedHashMap<>());
                    }
                    LinkedHashMap<Integer, Integer> wordMap = webSiteDocuments.search(s.toLowerCase());
                    try {
                        if (!wordMap.containsKey(id)) {
                            wordMap.put(id, 1);
                        } else
                            wordMap.put(id, wordMap.get(id) + 1);
                    } catch (Exception e) {
                        // Do nothing.
                    }
                }
            }
        });
    }

    @Override
    public void indexDirectory(String directoryPath) {
        List<File> allFiles = new ArrayList<>();
        readAllFiles(directoryPath, allFiles);
        for (File file : allFiles) {
            indexWebPage(file.getAbsolutePath());
        }
    }

    @Override
    public void deleteWebPage(String filePath) {
        LinkedHashMap<Integer, String> indexedDelete = new LinkedHashMap<>();
        File deleteFile = new File(filePath);
        try {
            parseXML(deleteFile, indexedDelete);
        } catch (Exception e) {
            System.out.println("error");
        }
        // delete from b tree
        indexedDelete.forEach((id, doc) -> {
            String[] split = splitStr(doc);
            for (String s : split) {
                LinkedHashMap<Integer, Integer> wordMap = webSiteDocuments.search(s.toLowerCase());
                if (wordMap != null && wordMap.containsKey(id)) {
                    wordMap.remove(id);
                    if (wordMap.size() == 0)
                        webSiteDocuments.delete(s.toLowerCase());
                }
            }
        });
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        List<ISearchResult> wordRankedId = new ArrayList<>();
        LinkedHashMap<Integer, Integer> wordMap = webSiteDocuments.search(word.toLowerCase());
        if (wordMap == null) return null;
        wordMap.forEach((id, freq) -> {
            SearchResult searchResult = new SearchResult(id.toString(), freq);
            wordRankedId.add(searchResult);
        });
        return wordRankedId;
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        String[] sentenceWords = splitStr(sentence.toLowerCase());
        List<ISearchResult> sentenceRankedID = new ArrayList<>();
        LinkedHashMap<Integer, Integer> wordsIdMap = webSiteDocuments.search(sentenceWords[0]);
        if (wordsIdMap == null) return null;

        for (int i = 1; i < sentenceWords.length; i++) {
            LinkedHashMap<Integer, Integer> nextWordMap = (LinkedHashMap<Integer, Integer>) webSiteDocuments.search(sentenceWords[i]).clone();

            ((LinkedHashMap<Integer, Integer>) nextWordMap.clone()).forEach((id, freq) -> {
                if (wordsIdMap.containsKey(id))
                    wordsIdMap.put(id, Math.min(freq, wordsIdMap.get(id)));
                else
                    nextWordMap.remove(id);
            });

            ((LinkedHashMap<Integer, Integer>) wordsIdMap.clone()).forEach((id, rank) -> {
                if (!nextWordMap.containsKey(id))
                    wordsIdMap.remove(id);
            });

        }

        wordsIdMap.forEach((id, rank) -> sentenceRankedID.add(new SearchResult(id.toString(), rank)));
        return sentenceRankedID;
    }

    public void parseXML(File inputFile, HashMap<Integer, String> map) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xmlContent = builder.parse(inputFile);
        xmlContent.getDocumentElement().normalize();
        NodeList nList = xmlContent.getElementsByTagName("doc");
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                map.put(Integer.parseInt(eElement.getAttribute("id")), eElement.getTextContent());
            }
        }
    }

    public void readAllFiles(String directoryPath, List<File> allFiles) {
        Queue<File> directoryQueue = new LinkedList<>();
        directoryQueue.add(new File(directoryPath));
        while (!directoryQueue.isEmpty()) {
            File file = directoryQueue.poll();
            if (file.isDirectory()) {
                if (file.listFiles() != null)
                    directoryQueue.addAll(List.of(file.listFiles()));
            } else if (file.isFile()) {
                allFiles.add(file);
            }
        }
    }

    // split to space and line (enter)
    public String[] splitStr(String text) {
        return text.split("[\n ]");
    }


}
