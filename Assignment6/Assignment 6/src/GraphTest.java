import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GraphTest {
    Graph<String, Integer> graph;
    @Test
    public void testAddNode() {
        graph = new Graph<>();
        // Make sure we can add nodes to the graph.
        assertTrue(graph.addNode("zxy", 30));
        assertTrue(graph.addNode("abc", 5));
        assertTrue(graph.addNode("pqr", 25));

        List<Vertex<String, Integer>> vertices = graph.getVertices();
        // Make sure vertices created correctly. We get them in sorted order.
        assertEquals(vertices.get(0).getName(), "abc");
        assertTrue(vertices.get(0).getData() == 5);
        assertEquals(vertices.get(1).getName(), "pqr");
        assertTrue(vertices.get(1).getData() == 25);
        assertEquals(vertices.get(2).getName(), "zxy");
        assertTrue(vertices.get(2).getData() == 30);

        // Try adding duplicate nodes to the graph.
        assertFalse(graph.addNode("abc", 40));
        assertFalse(graph.addNode("zxy", 100));
        // Make sure data hasn't changed for these nodes.
        List<Vertex<String, Integer>> verticesNoDup = graph.getVertices();
        assertTrue(vertices.get(0).getData() == 5);
        assertTrue(vertices.get(2).getData() == 30);

        // Add a new node with data null.
        assertTrue(graph.addNode("uvw", null));
        assertEquals(graph.getVertices().get(2).getName(), "uvw");
        assertNull(graph.getVertices().get(2).getData());
        // Add a duplicate, with data not null. We should get false indicating duplicate, but
        // data should be updated.
        assertFalse(graph.addNode("uvw", 50));
        assertTrue(graph.getVertices().get(2).getData() == 50);
    }

    @Test
    public void testAddNodes() {
        // Test adding multiple nodes at the same time.
        graph = new Graph<>();
        String[] names = new String[] {"zxy", "abc", "pqr"};
        Integer[] data = new Integer[] {30, 5, 25};

        assertTrue(graph.addNodes(names, data));
        List<Vertex<String, Integer>> vertices = graph.getVertices();
        assertTrue(vertices.size() == 3);

        // Make sure vertices created correctly. We get them in sorted order.
        assertEquals(vertices.get(0).getName(), "abc");
        assertTrue(vertices.get(0).getData() == 5);
        assertEquals(vertices.get(1).getName(), "pqr");
        assertTrue(vertices.get(1).getData() == 25);
        assertEquals(vertices.get(2).getName(), "zxy");
        assertTrue(vertices.get(2).getData() == 30);
    }

    @Test
    public void testAddEdge() {
        // Add 2 nodes.
        graph = new Graph<>();
        String[] names = new String[] {"A", "B"};
        Integer[] data = new Integer[] {30, 5};
        assertTrue(graph.addNodes(names, data));
        // Add an edge between those nodes.
        assertTrue(graph.addEdge("A", "B"));
        // Try adding a duplicate edge.
        assertFalse(graph.addEdge("A", "B"));
        // Try add a duplicate edge, this time reversing the order.
        assertFalse(graph.addEdge("B", "A"));

        // Try adding two edges with a node not already in graph. It should create the new node each time.
        assertTrue(graph.addEdge("C", "A"));
        assertTrue(graph.addEdge("B", "D"));
        List<Vertex<String, Integer>> vertices = graph.getVertices();
        assertTrue(vertices.size() == 4);
        assertEquals(vertices.get(0).getName(), "A");
        assertEquals(vertices.get(1).getName(), "B");
        assertEquals(vertices.get(2).getName(), "C");
        assertEquals(vertices.get(3).getName(), "D");
        assertTrue(vertices.get(0).getData() == 30);
        assertTrue(vertices.get(1).getData() == 5);
        // The two newly created vertices are expected to have data as null.
        assertNull(vertices.get(2).getData());
        assertNull(vertices.get(3).getData());
    }

    @Test
    public void testAddEdges() {
        graph = new Graph<>();
        String[] names = new String[] {"A", "B"};
        Integer[] data = new Integer[] {30, 5};
        assertTrue(graph.addNodes(names, data));

        ArrayList<String> aNeighbors = new ArrayList<>();
        aNeighbors.add("D");
        aNeighbors.add("E");
        aNeighbors.add("B");
        assertTrue(graph.addEdges("A", aNeighbors));
        // Make sure the edges exist now, by trying to add duplicate edges.
        assertFalse(graph.addEdge("B", "A"));
        assertFalse(graph.addEdge("A", "E"));
        assertFalse(graph.addEdge("D", "A"));
        // Add new edges with one existing and one new edge.
        aNeighbors.clear();
        aNeighbors.add("E");
        aNeighbors.add("C");
        assertFalse(graph.addEdges("A", aNeighbors));
        // Make sure edge to C got added.
        assertFalse(graph.addEdge("A", "C"));
        List<Vertex<String, Integer>> vertices = graph.getVertices();
        assertTrue(vertices.size() == 5);
        assertEquals(vertices.get(4).getName(), "E");
    }

    @Test
    public void testRemoveNode() {
        createSimpleGraph();

        // Try removing node B.
        assertTrue(graph.removeNode("B"));
        List<Vertex<String, Integer>> vertices = graph.getVertices();
        assertTrue(vertices.size() == 4);
        // Make sure B does not show up in the list of vertices.
        assertEquals(vertices.get(0).getName(), "A");
        assertEquals(vertices.get(1).getName(), "C");
        assertEquals(vertices.get(2).getName(), "D");
        assertEquals(vertices.get(3).getName(), "E");

        // Make sure B is removed from all the edges too which refer to B.
        checkForDeletedNeighbor("A", "B");
        checkForDeletedNeighbor("C", "B");
        checkForDeletedNeighbor("D", "B");
        checkForDeletedNeighbor("E", "B");

        // Try to delete a non existent node.
        assertFalse(graph.removeNode("F"));
        assertTrue(vertices.size() == 4);
    }

    // Checks whether delName appears in the neighbors list of nodeName.
    private void checkForDeletedNeighbor(String nodeName, String delName) {
        List<Vertex<String, Integer>> neighbors = graph.getNeighborsForNode(nodeName);
        for (Vertex<String, Integer> vertex : neighbors) {
            assertNotEquals(vertex.getName(), delName);
        }
    }

    private void createSimpleGraph() {
        graph = new Graph<>();
        String[] names = new String[] {"A", "B", "C"};
        Integer[] data = new Integer[] {30, 5, 25};
        ArrayList<String> aNeighbors = new ArrayList<>();
        ArrayList<String> bNeighbors = new ArrayList<>();
        ArrayList<String> cNeighbors = new ArrayList<>();
        aNeighbors.add("C");
        aNeighbors.add("D");
        aNeighbors.add("E");
        bNeighbors.add("D");
        bNeighbors.add("A");
        cNeighbors.add("E");
        cNeighbors.add("D");
        cNeighbors.add("B");
        graph.addEdges("A", aNeighbors);
        graph.addEdges("B", bNeighbors);
        graph.addEdges("C", cNeighbors);
    }

    @Test
    public void testRemoveNodes() {
        createSimpleGraph();

        // Try removing two nodes A and B.
        ArrayList<String> removeList = new ArrayList<>();
        removeList.add("B");
        removeList.add("A");
        assertTrue(graph.removeNodes(removeList));
        List<Vertex<String, Integer>> vertices = graph.getVertices();
        assertTrue(vertices.size() == 3);
        // Make sure A and B do not show up in the list of vertices.
        assertEquals(vertices.get(0).getName(), "C");
        assertEquals(vertices.get(1).getName(), "D");
        assertEquals(vertices.get(2).getName(), "E");

        // Make sure A and B have been removed from all the edges too which refer to them.
        checkForDeletedNeighbor("C", "B");
        checkForDeletedNeighbor("D", "B");
        checkForDeletedNeighbor("E", "B");
        checkForDeletedNeighbor("C", "A");
        checkForDeletedNeighbor("D", "A");
        checkForDeletedNeighbor("E", "A");
    }

    // Test depth first search. This is covered in detail in the demo program.
    @Test
    public void testDFS() {
        createSimpleGraph();
        graph.addEdge("B", "F");
        graph.addEdge("C", "F");
        String[] dfsPath = graph.DFS("A", "F");
        String[] expectedPath = new String[] {"A", "B", "C", "F"};
        assertArrayEquals(dfsPath, expectedPath);

        // Test a non-existent path
        dfsPath = graph.DFS("B", "N");
        assertTrue(dfsPath.length == 0);
    }

    // Test Breadth first search. This is covered in detail in the demo program.
    @Test
    public void testBFS() {
        createSimpleGraph();
        graph.addEdge("B", "F");
        graph.addEdge("C", "F");
        String[] bfsPath = graph.BFS("A", "F");
        String[] expectedPath = new String[] {"A", "B", "F"};
        assertArrayEquals(bfsPath, expectedPath);

        // Test a non-existent path
        bfsPath = graph.BFS("B", "N");
        assertTrue(bfsPath.length == 0);
    }

}
