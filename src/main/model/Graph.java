package main.model;

import java.util.HashMap;
import java.util.Map;

public class Graph {
    private Map<Character, Map<Character, Integer>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void addNode(Character node) {
        adjacencyList.put(node, new HashMap<>());
    }

    public void addEdge(Character source, Character destination, Integer weight) {
        // Ensure the source node exists in the adjacency list
        adjacencyList.computeIfAbsent(source, k -> new HashMap<>());

        // Add the edge to the source node
        adjacencyList.get(source).put(destination, weight);
    }


    public Map<Character, Map<Character, Integer>> getGraph(){
        return adjacencyList;
    }

}
