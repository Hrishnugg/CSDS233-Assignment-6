import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// Demo program to illustrate Graph functionality, especially loading from file, DFS and BFS.
public class DemoGraph {
    public static void main(String[] args) {
        System.out.println("Illustration of graph functionality!");

        // Construct a graph which is not connected - has two disjoint connected sections.
        System.out.println("Creating the graph by adding nodes and edges");
        Graph<String, Integer> myGraph = new Graph<>();
        myGraph.addNode("A", 1);
        myGraph.addNode("B", 2);
        myGraph.addEdge("D", "A");
        myGraph.addEdge("A", "B");
        myGraph.addEdge("B", "C");
        myGraph.addEdge("D", "C");
        myGraph.addEdge("C", "E");
        myGraph.addEdge("D", "E");
        myGraph.addEdge("E", "F");
        myGraph.addEdges("H", new ArrayList<>(Arrays.asList("F", "G")));
        myGraph.addEdge("F", "G");
        // The following nodes are not connected with the earlier set.
        myGraph.addEdge("I", "J");
        myGraph.addEdge("J", "L");
        myGraph.addEdge("K", "L");
        myGraph.addEdge("J", "K");

        // Print the graph.
        System.out.println("Printing the graph");
        myGraph.printGraph();
        // Do BFS to find a path from B to H.
        String[] path = myGraph.BFS("B", "H");
        System.out.println("BFS path from B to H");
        printPath(path);
        // Do DFS from to find a path from B to H.
        path = myGraph.DFS("B", "H");
        System.out.println("DFS path from B to H");
        printPath(path);

        // Try to find a path between two vertices in the disjoint portions.
        path = myGraph.BFS("K", "D");
        System.out.println("BFS path from K to D");
        printPath(path);
        // Try DFS for the same set of vertices. We should see empty path as in the BFS case.
        path = myGraph.BFS("K", "D");
        System.out.println("DFS path from K to D");
        printPath(path);

        // Demonstrate read the graph from a file.
        // Create a graph file first.
        System.out.println("Creating a graph file (./my_demo_graph.txt) for demo");
        ArrayList<String> graphText = new ArrayList<>();
        graphText.add("P S R Q");
        graphText.add("Q Z");
        graphText.add("Z X Y");
        graphText.add("R N");
        graphText.add("N Y O");
        graphText.add("O M N K");
        String graphFileName = "./my_demo_graph.txt";
        // If we could not create file, return.
        if (!writeGraphFile(graphText, graphFileName)) {
            return;
        }
        System.out.println("Reading the graph from file we created");
        Graph<String, Integer> fileGraph;
        try {
            fileGraph = Graph.read(graphFileName);
        } catch (IOException e) {
            System.err.println("Error reading graph file " + e);
            return;
        }
        // Print the graph we read from file.
        System.out.println("Printing the graph we read from file");
        fileGraph.printGraph();

        // Do BFS to find a path from Q to K.
        path = fileGraph.BFS("Q", "K");
        System.out.println("BFS path from Q to K");
        printPath(path);
        // Do DFS from to find a path from B to H.
        path = fileGraph.DFS("Q", "K");
        System.out.println("DFS path from Q to K");
        printPath(path);
        // Do BFS to find a path from Q to K.
        path = fileGraph.BFS("X", "N");
        System.out.println("BFS path from X to N");
        printPath(path);
        // Do DFS from to find a path from X to N.
        path = fileGraph.DFS("X", "N");
        System.out.println("DFS path from X to N");
        printPath(path);
    }

    static void printPath(String[] path) {
        for (String node : path) {
            System.out.print(node + " ");
        }
        System.out.println();
    }

    static boolean writeGraphFile(ArrayList<String> graphText, String filename) {
        try {
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (String graphLine : graphText) {
                bufferedWriter.write(graphLine);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            System.err.println("Error creating file " + e);
            return false;
        }
        return true;
    }
}