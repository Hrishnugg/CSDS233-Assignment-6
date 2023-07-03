import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// Implements an undirected, unweighted graph.
public class Graph<K extends Comparable<? super K>, V> {

    // Represents a node in the graph.
    private class Node {
        // Name and data associated with the graph node.
        Vertex<K, V> vertex;

        // List of nodes which are connected to this node via a direct edge.
        private ArrayList<Node> neighbors;

        // Visited marker, used in Depth first and Breadth first searches to avoid
        // processing already visited nodes.
        private boolean visited;

        // Used to maintain the link to the parent node, during DFS or BFS.
        private Node searchParent;

        // Constructs an empty graph.
        public Node(K name, V data) {
            vertex = new Vertex<>(name, data);
            neighbors = new ArrayList<>();
            visited = false;
            searchParent = null;
        }

        // Getters and Setters.
        public K getName() {
            return vertex.getName();
        }

        public V getData() {
            return vertex.getData();
        }

        public void setData(V data) {
            vertex.setData(data);
        }

        public ArrayList<Node> getNeighbors() {
            return neighbors;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public Node getSearchParent() {
            return searchParent;
        }

        public void setSearchParent(Node searchParent) {
            this.searchParent = searchParent;
        }
    }

    // Class to hold a value and a boolean indicating success.
    // Used to return both of these together from a method.
    private class Tuple<T> {
        private T value;
        private boolean success;

        public Tuple(T value, boolean success) {
            this.value = value;
            this.success = success;
        }

        public T getValue() {
            return value;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    // Queue of nodes for use in Breadth First Search.
    private class NodeQueue {
        // list to store elements in the queue.
        private LinkedList<Node> nodes;

        NodeQueue() {
           nodes = new LinkedList<>();
        }

        // Add node to the end of the queue.
        public void enqueue(Node node) {
            nodes.addLast(node);
        }

        // Removed and returns node from the front of the queue.
        public Node dequeue() {
            if (nodes.isEmpty()) {
                return null;
            }
            return nodes.removeFirst();
        }

        // Returns whether the queue is empty.
        public boolean isEmpty() {
            return nodes.isEmpty();
        }
    }

    // Adjacency List representation of the graph.
    private ArrayList<Node> adjList;

    // Constructs an empty graph.
    public Graph() {
        adjList = new ArrayList<>();
    }

    // Adds a new node with name and data to the graph. If duplicate found returns false.
    // In case of duplicate, sets the data associated with name, if there was no data (meaning null)
    // previously set for that node.
    public boolean addNode(K name, V data) {
        // Insert the new node into the graph. Return whether no duplicate was found (a new node was created).
        return addNodeInternal(name, data).isSuccess();
    }

    // Returns a tuple containing the newly added or already present node, and a boolean
    // indicating whether a node was newly added.
    private Tuple<Node> addNodeInternal(K name, V data) {
        // Find the position of the node in the graph if it already exists (duplicate) or
        // get the position to insert the new node. We maintain the vertices in sorted order.
        // Though it is not a requirement for graph functionality, sorted order enables us to
        // search efficiently, and print the graph in the required sorted way.
        // We use binary search.
        Tuple<Integer> tup = findNodePosition(name);
        // If found, we have a duplicate.
        if (tup.isSuccess()) {
            Node node = adjList.get(tup.getValue());
            // Set node data if it was null.
            if (node.getData() == null) {
                node.setData(data);
            }
            // Return the node index, and false to indicate duplicate - no insertion done.
            return new Tuple<Node>(node, false);
        }
        // Add the new node at index after moving the following elements by one position to make room.
        Node newNode = new Node(name, data);
        adjList.add(tup.getValue(), newNode);
        return new Tuple<Node>(newNode, true);
    }

    // Finds the position of the specified node in adjacency list of nodes.
    // Returns the index of the node in the ArrayList and boolean true if node was found.
    // Else returns the index at which node needs to be inserted to maintain sorted order,
    // and boolean false indicating node was not found.
    // Uses binary search.
    private Tuple<Integer> findNodePosition(K name) {
        int left = 0;
        int right = adjList.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int comp = adjList.get(mid).getName().compareTo(name);
            // if we found a match, return the index of the node.
            if (comp == 0) {
                return new Tuple<Integer>(mid, true);
            }
            if (comp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        // We did not find the node, return the position to insert the new node (name)
        // so that sorted order is maintained.
        return new Tuple<Integer>(left, false);
    }

    // Adds all elements in the given array of vertices to the graph, skipping duplicates. Returns false if
    // duplicates were encountered during addition, else returns true.
    public boolean addNodes(K[] names, V[] data) throws IllegalArgumentException {
        if (names.length != data.length) {
            throw new IllegalArgumentException("Name and data arrays should be of equal length");
        }
        // If any duplicate found, return value is set to false. But we don't return immediately, and continue to
        // try to add the rest of the nodes given.
        boolean no_dups = true;
        for (int i = 0; i < names.length; i++) {
            if (!addNode(names[i], data[i])) {
                no_dups = false;
            }
        }
        return no_dups;
    }

    // Adds an edge in one direction from fromNode to toNode. Returns false if duplicate is found.
    private boolean addDirectedEdge(Node fromNode, Node toNode) {
        // Check for a loop.
        if (fromNode.getName().equals(toNode.getName())) {
            return false;
        }
        int index = 0;
        while (index < fromNode.getNeighbors().size()) {
            Node curNode = fromNode.getNeighbors().get(index);
            int comp = curNode.getName().compareTo(toNode.getName());
            if (comp == 0) {
                // Duplicate edge found.
                return false;
            }
            // We found the position to insert the new node.
            if (comp > 0) break;
            index++;
        }
        // Add the new node at index after moving the following elements by one position to make room.
        fromNode.getNeighbors().add(index, toNode);
        return true;
    }

    // Adds an undirected edge between "from" and "to".
    public boolean addEdge(K from, K to) {
        // Add Nodes from and to if not present.
        Tuple<Node> fromTuple = addNodeInternal(from, null);
        Tuple<Node> toTuple = addNodeInternal(to, null);
        // Add two edges, from -> to and to -> from.
        if (!addDirectedEdge(fromTuple.getValue(), toTuple.getValue())) {
            return false;
        }
        return addDirectedEdge(toTuple.getValue(), fromTuple.getValue());
    }

    // Adds undirected edges between "from" and all nodes in "toList".
    public boolean addEdges(K from, List<K> toList) {
        // Tuple fromTuple = addNodeInternal(from, null);
        boolean result = true;
        // If any duplicate edge is found return result is set to false. But we don't return immediately,
        // continue to try and add the remaining edges given.
        for (K toName : toList) {
            if (!addEdge(from, toName)) {
                result = false;
            }
        }
        return result;
    }

    // Removes the given node from the graph, and returns true if successful.
    // Returns false if given node is not found in the graph.
    public boolean removeNode(K name) {
        Tuple<Integer> tup = findNodePosition(name);
        // If node not found in the graph, return.
        if (!tup.isSuccess()) {
            return false;
        }
        // Found the node, remove it from the list of vertices.
        Node curNode = adjList.get(tup.getValue());
        int index = tup.getValue();
        adjList.remove(index);
        // Now we need to go to each of the neighbors of this node, and remove current node from
        // the list of neighbors of those nodes (so that no one is referencing this and graph is consistent).
        for (Node neighbor : curNode.getNeighbors()) {
            int edgeIndex = 0;
            while (edgeIndex < neighbor.getNeighbors().size()) {
                // Remove the current node from the neighbors list of neighbor node.
                if (neighbor.getNeighbors().get(edgeIndex) == curNode) {
                    neighbor.getNeighbors().remove(edgeIndex);
                    break;
                }
                edgeIndex++;
            }
        }
        return true;
    }

    // Removes all nodes in nodeList from the graph.
    // Returns false if we did not find any of the given nodes in the graph, but we
    // scan all in the given list, and attempt to remove all.
    public boolean removeNodes(List<K> nodeList) {
        boolean result = true;
        // If we don't succeed in removing a node in the given list, we set return result to false.
        // But we continue to scan the list, and try to remove all remaining nodes.
        for (K name : nodeList) {
            if (!removeNode(name)) {
                result = false;
            }
        }
        return result;
    }

    // Prints the graph in adjacency list form.
    public void printGraph() {
        for (Node node : adjList) {
            System.out.print(node.getName() );
            for (Node neighbor : node.getNeighbors()) {
                System.out.print(" " + neighbor.getName());
            }
            System.out.println();
        }
    }

    // Constructs a graph from the given text file (filename).
    public static <V> Graph<String, V> read(String filename) throws IOException {
        Graph<String, V> graph = new Graph<>();
        // Open the file and start reading.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Get each line from the file.
            while ((line = br.readLine()) != null) {
                // Split the line using space as the delimiter.
                String[] nodeNames = line.split("\\s+");
                if (nodeNames.length > 0) {
                    // Get the start vertex.
                    String node = nodeNames[0];
                    ArrayList<String> neighbors = new ArrayList<>(nodeNames.length - 1);
                    // Add all edges.
                    for (int i = 1; i < nodeNames.length; i++) {
                        neighbors.add(nodeNames[i]);
                    }
                    // Add all the edges. This will create node also, if it does not already exist.
                    graph.addEdges(node, neighbors);
                }
            }
        }
        return graph;
    }

    // Clears the visited marker and parent pointer for all nodes in the graph, so that we are ready for
    // doing another DFS or BFS pass.
    private void clearSearchMarkers() {
        for (Node node : adjList) {
            node.setVisited(false);
            node.setSearchParent(null);
        }
    }

    // Finds a node in the graph, given name.
    // Since the nodes are kept sorted, we use binary search for efficiency.
    private Node findNode(K name) {
        Tuple<Integer> tup = findNodePosition(name);
        // Check if we found the required node.
        if (tup.isSuccess()) {
            return adjList.get(tup.getValue());
        }
        // Graph.Node not found.
        return null;
    }

    // Recursive Depth First Search.
    private boolean DfsInternal(Node start, Node dest) {
        start.setVisited(true);
        for (Node neighbor : start.getNeighbors()) {
            // Check if we reached the destination.
            if (neighbor == dest) {
                neighbor.setSearchParent(start);
                // Return as we reached the destination.
                return true;
            }
            // If neighbor not visited, recurse depth first looking for dest.
            if (!neighbor.isVisited()) {
                // Maintain the parent link to construct the path in the end.
                neighbor.setSearchParent(start);
                if (DfsInternal(neighbor, dest)) {
                    // Return as we reached the destination.
                    return true;
                }
            }
        }
        return false;
    }

    // Finds a path between node from and node to using Depth First search, as an array of node names.
    // Returns 0 length array if no path exists.
    public K[] DFS(K from, K to) {
        // Clear visited markers and parent pointers on each node.
        clearSearchMarkers();
        Node fromNode = findNode(from);
        Node toNode = findNode(to);
        // If either from node or to node are not found in the graph, return empty array.
        if (fromNode == null || toNode == null) {
            return (K[]) Array.newInstance(from.getClass(), 0);
        }
        // If from and to are the same, return an array with one node.
        if (fromNode == toNode) {
            K[] ret = (K[]) Array.newInstance(from.getClass(), 1);
            ret[0] = from;
            return ret;
        }
        // Do depth first search till we hit toNode. Return 0 length array if we did not find a path.
        if (!DfsInternal(fromNode, toNode)) {
            return (K[]) Array.newInstance(from.getClass(), 0);
        }
        return constructPath(from, toNode);
    }

    // Returns the path from source to destination (toNode) as an array of node names.
    private K[] constructPath(K from, Node toNode) {
        // Construct the path from destination to source.
        // Parent links are maintained with the node, during DFS and BFS.
        ArrayList<Node> pathList = new ArrayList<>();
        Node curNode = toNode;
        while (curNode != null) {
            pathList.add(curNode);
            curNode = curNode.getSearchParent();
        }
        K[] pathNames = (K[]) Array.newInstance(from.getClass(), pathList.size());
        // Copy from pathList in reverse order to get source to destination.
        int index = pathList.size() - 1;
        for (Node node : pathList) {
            pathNames[index] = node.getName();
            index--;
        }
        return pathNames;
    }

    // Finds a path between node from and node to using Breadth First search, as an array of node names.
    // Returns 0 length array if no path exists.
    public K[] BFS(K from, K to) {
        // Clear visited markers and parent pointers on each node.
        clearSearchMarkers();
        Node fromNode = findNode(from);
        Node toNode = findNode(to);
        // If either from node or to node are not found in the graph, return empty array.
        if (fromNode == null || toNode == null) {
            return (K[]) Array.newInstance(from.getClass(), 0);
        }
        // If from and to are the same, return an array with one node.
        if (fromNode == toNode) {
            K[] ret = (K[]) Array.newInstance(from.getClass(), 1);
            ret[0] = from;
            return ret;
        }
        NodeQueue queue = new NodeQueue();
        fromNode.setVisited(true);
        queue.enqueue(fromNode);
        boolean found = false;
        while(!found && !queue.isEmpty()) {
            Node v = queue.dequeue();
            for (Node neighbor : v.getNeighbors()) {
                // Check if we found the destination node.
                if (neighbor == toNode) {
                    neighbor.setSearchParent(v);
                    found = true;
                    break;
                }
                // Continue breadth first search, insert neighbor into the queue if not visited before.
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    // Maintain the parent link to construct the path in the end.
                    neighbor.setSearchParent(v);
                    queue.enqueue(neighbor);
                }
            }
        }
        if (!found) {
            return (K[]) Array.newInstance(from.getClass(), 0);
        }
        return constructPath(from, toNode);
    }

    // Get the list of vertices in the graph. Essentially copy of vertices in the adjacency list.
     List<Vertex<K, V>> getVertices() {
        ArrayList<Vertex<K, V>> vertexList = new ArrayList<>(adjList.size());
        for (Node node : adjList) {
            vertexList.add(new Vertex<>(node.getName(), node.getData()));
        }
        return vertexList;
    }

    // Finds a vertex in the graph given name. Useful to get the data associated with name.
     Vertex<K, V> findVertex(K name) {
        Node node = findNode(name);
        if (node == null) {
            return null;
        }
        return new Vertex<>(name, node.getData());
    }

    // Used for testing only.
     List<Vertex<K, V>> getNeighborsForNode(K name) {
        Node node = findNode(name);
        ArrayList<Vertex<K, V>> neighbors = new ArrayList<>();
        if (node == null) {
            // Return empty list.
            return neighbors;
        }
        for (Node neighbor : node.getNeighbors()) {
            neighbors.add(new Vertex<>(neighbor.getName(), neighbor.getData()));
        }
        return neighbors;
    }
}
