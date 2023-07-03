// Represents the name and data associated with a Graph node.
// This is a separate class to avoid exposing internals of Graph node
// and adjacency list externally in public methods.
public class Vertex<K, V> {
    private K name;
    private V data;

    public Vertex(K name, V data) {
        this.name = name;
        this.data = data;
    }

    public K getName() {
        return name;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }
}
