import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class WordLadders {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please specify the word graph filename as command line argument!");
        }
        System.out.println("Reading word graph from file " + args[0]);
        Graph<Integer, String> wordGraph;
        try {
            wordGraph = readWordGraph(args[0]);
        } catch (IOException e) {
            System.err.println("Error reading file " + args[0] + ": " + e);
            return;
        }
        HashMap<String, Integer> wordToNodeMap = buildWordToNodeMap(wordGraph);
        // Continue getting user input for start and end words till user chooses
        // not to.
        while (true) {
            // Get user input: a start word and end word.
            String[] userWords = getTwoWordsFromUser();
            Integer startNode, endNode;
            // If either start word or end word is not found in graph, continue to
            // next user input. If both were found, find a path between the nodes
            // using BFS and DFS (for small graphs only).
            if ((startNode = wordToNodeMap.get(userWords[0])) == null) {
                System.out.println("Start node not found in the graph");
            } else if ((endNode = wordToNodeMap.get(userWords[1])) == null) {
                System.out.println("End node not found in the graph");
            } else {
                // Perform Breadth-First search.
                Integer[] bfsPath = wordGraph.BFS(startNode, endNode);
                // Display the path.
                System.out.println("BFS path:");
                for (int node : bfsPath) {
                    System.out.print(" " + wordGraph.findVertex(node).getData());
                }
                System.out.println();
                // If the Graph is small enough do DFS also. Do not do DFS for large graphs.
                if (wordToNodeMap.size() < 1000) {
                    Integer[] dfsPath = wordGraph.DFS(startNode, endNode);
                    // Display the path.
                    System.out.println("DFS path:");
                    for (int node : dfsPath) {
                        System.out.print(" " + wordGraph.findVertex(node).getData());
                    }
                    System.out.println();
                }
            }
            // Get the user input on whether to continue.
            System.out.println("Continue to next word set (y/n)?");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();
            if (!input.equals("y")) {
                break;
            }
        }
    }

    public static HashMap<String, Integer> buildWordToNodeMap(Graph<Integer, String> graph) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (Vertex<Integer, String> vertex : graph.getVertices()) {
            hashMap.put(vertex.getData(), vertex.getName());
        }
        return hashMap;
    }

    public static String[] getTwoWordsFromUser() {
        System.out.println("Please give a start word and an end word");
        Scanner scanner = new Scanner(System.in);
        String word1 = scanner.next();
        String word2 = scanner.next();
        return new String[] {word1, word2};
    }

    public static Graph<Integer, String> readWordGraph(String filename) throws IOException {
        Graph<Integer, String> graph = new Graph<>();
        // Open the file and start reading.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Get each line from the file.
            while ((line = br.readLine()) != null) {
                // Split the line using space as the delimiter.
                String[] nodeNames = line.split("\\s+");
                if (nodeNames.length > 0) {
                    // Get the start vertex.
                    int node = Integer.parseInt(nodeNames[0]);
                    String nodeData = null;
                    if (nodeNames.length > 1) {
                        nodeData = nodeNames[1];
                    }
                    graph.addNode(node, nodeData);
                    ArrayList<Integer> neighbors = new ArrayList<>(nodeNames.length - 2);
                    // Add all edges.
                    for (int i = 2; i < nodeNames.length; i++) {
                        neighbors.add(Integer.parseInt(nodeNames[i]));
                    }
                    // Add all the edges. This will create node also, if it does not already exist.
                    graph.addEdges(node, neighbors);
                }
            }
        }
        return graph;
    }
}
