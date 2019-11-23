import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

import java.util.*;

/**
 *  A weighted graph.
 *  @author eduhmc
 */
public class Graph {

    /** Adjacency lists by vertex number. */
    private LinkedList<Edge>[] adjLists;
    /** Number of vertices in me. */
    private int vertexCount;

    /** A graph with NUMVERTICES vertices and no edges. */
    @SuppressWarnings("unchecked")
    public Graph(int numVertices) {
        adjLists = (LinkedList<Edge>[]) new LinkedList[numVertices];
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
        }
        vertexCount = numVertices;
    }

    /** Add to the graph a directed edge from vertex V1 to vertex V2,
     *  with weight EDGEWEIGHT. If the edge already exists, replaces
     *  the weight of the current edge EDGEWEIGHT. */
    public void addEdge(int v1, int v2, int edgeWeight) {
        if (!isAdjacent(v1, v2)) {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            v1Neighbors.add(new Edge(v1, v2, edgeWeight));
        } else {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            for (Edge e : v1Neighbors) {
                if (e.to() == v2) {
                    e.edgeWeight = edgeWeight;
                }
            }
        }
    }

    /** Add to the graph an undirected edge from vertex V1 to vertex V2,
     *  with weight EDGEWEIGHT. If the edge already exists, replaces
     *  the weight of the current edge EDGEWEIGHT. */
    public void addUndirectedEdge(int v1, int v2, int edgeWeight) {
        addEdge(v1, v2, edgeWeight);
        addEdge(v2, v1, edgeWeight);
    }

    /** Returns true iff there is an edge from vertex FROM to vertex TO. */
    public boolean isAdjacent(int from, int to) {
        for (Edge e : adjLists[from]) {
            if (e.to() == to) {
                return true;
            }
        }
        return false;
    }

    /** Returns a list of all the neighboring vertices u
     *  such that the edge (VERTEX, u) exists in this graph. */
    public List<Integer> neighbors(int vertex) {
        ArrayList<Integer> neighbors = new ArrayList<>();
        for (Edge e : adjLists[vertex]) {
            neighbors.add(e.to());
        }
        return neighbors;
    }

    /** Runs Dijkstra's algorithm starting from vertex STARTVERTEX and returns
     *  an integer array consisting of the shortest distances
     *  from STARTVERTEX to all other vertices. */
    public int[] dijkstras(int startVertex) {
        // TODO: Your code here!
        int[] weightList = new int[vertexCount];
        boolean[] visited = new boolean[vertexCount];

        List<Integer> currList = new ArrayList<Integer>();
        List<Integer> nextList = new ArrayList<Integer>();

        Arrays.fill(weightList, Integer.MAX_VALUE);
        Arrays.fill(visited, false);
        currList.add(startVertex);
        weightList[startVertex] = 0;
        visited[startVertex] = true;


        for (int i = 0; i < vertexCount; i += 1) {
            ArrayList<Integer> newCurrList = new ArrayList<Integer>();
            int[] distance = new int[currList.size()];
            Stack<Integer> currListStack = new Stack<>();
            currListStack.addAll(currList);

            for (int num = 0; num < currList.size(); num += 1) {
                distance[num] = weightList[num];
            }

            Arrays.sort(distance);

            while (!currListStack.empty()) {
                int currV = currListStack.pop();
                for (int indDist = 0; indDist < distance.length; indDist += 1) {
                    if (distance[indDist] == weightList[currV]) {
                        distance[indDist] = currV;
                    }
                }
            }

            for (int i2 = 0; i2 < distance.length; i2 += 1) {
                newCurrList.add(distance[i2]);
            }

            currList = newCurrList;
            for (int currVertex: currList) {
                for (int neighbours: neighbors(currVertex)) {
                    if (!visited[neighbours]) {
                        weightList[neighbours] = Math.min(weightList[neighbours],
                                weightList[currVertex] + getEdge(currVertex, neighbours).info());
                        if (!currList.contains(neighbours)) {
                            nextList.add(neighbours);
                        }
                    }
                }
                visited[currVertex] = true;
            }
            currList = nextList;
            nextList = new ArrayList<Integer>();
        }
        // TODO: Your code here!
        return weightList;
    }

    /** Returns the edge (V1, V2). (ou may find this helpful to implement!) */
    private Edge getEdge(int v1, int v2) {
        return null;
    }

    /** Represents an edge in this graph. */
    private class Edge {

        /** End points of this edge. */
        private int from, to;
        /** Weight label of this edge. */
        private int edgeWeight;

        /** The edge (V0, V1) with weight WEIGHT. */
        Edge(int v0, int v1, int weight) {
            this.from = v0;
            this.to = v1;
            this.edgeWeight = weight;
        }

        /** Return neighbor vertex along this edge. */
        public int to() {
            return to;
        }

        /** Return weight of this edge. */
        public int info() {
            return edgeWeight;
        }

        @Override
        public String toString() {
            return "(" + from + "," + to + ",dist=" + edgeWeight + ")";
        }

    }

    /** Tests of Graph. */
    public static void main(String[] unused) {
        // Put some tests here!

        Graph g1 = new Graph(5);
        g1.addEdge(0, 1, 1);
        g1.addEdge(0, 2, 1);
        g1.addEdge(0, 4, 1);
        g1.addEdge(1, 2, 1);
        g1.addEdge(2, 0, 1);
        g1.addEdge(2, 3, 1);
        g1.addEdge(4, 3, 1);

        Graph g2 = new Graph(5);
        g2.addEdge(0, 1, 1);
        g2.addEdge(0, 2, 1);
        g2.addEdge(0, 4, 1);
        g2.addEdge(1, 2, 1);
        g2.addEdge(2, 3, 1);
        g2.addEdge(4, 3, 1);
    }
}
