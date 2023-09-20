package main;

import main.model.Graph;
import main.model.NodeWithDistance;

import java.io.*;
import java.util.*;

public class Solution {

    private Graph graph;

    public Solution(String fileName) {
        graph = new Graph();
        readFile(fileName);
    }

    public Graph getGraph() {
        return graph;

    }

    // Read the input file
    public void readFile(String fileName) {
        try (InputStream inputStream = Solution.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    if ((line = reader.readLine()) != null) {
                        populateGraph(line);
                    }
                }
            } else {
                System.out.println("File not found in the classpath: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Populate the Graph
    public void populateGraph(String line) {
        String[] edges = line.split(",");
        for (String edge : edges) {
            edge = edge.trim();
            Character source = edge.charAt(0);
            Character destination = edge.charAt(1);
            int weight = Character.getNumericValue(edge.charAt(2));
            graph.addEdge(source, destination, weight);
        }
    }

    public void averageLatency(List<Character> input) {
        try {
            System.out.println(calculatePathWeight(input));
        } catch (NullPointerException ex) {
            System.out.println("NO SUCH TRACE");
        }
    }

    public int calculatePathWeight(List<Character> input) {
        Map<Character, Map<Character, Integer>> map = graph.getGraph();
        int weight = 0;
        for (int i = 0; i < input.size() - 1; i++) {
            weight = weight + map.get(input.get(i)).get(input.get(i + 1));
        }
        return weight;
    }

    public void countTracesWithMaxHops(char startNode, char endNode, int maxHops) {
        System.out.println(dfsMaxHops(startNode, endNode, 0, maxHops));
    }

    private int dfsMaxHops(char currentNode, char endNode, int currentHops, int maxHops) {
        if (currentNode == endNode && currentHops > 0 && currentHops <= maxHops) {
            return 1;
        }

        if (currentHops >= maxHops) {
            return 0;
        }

        int count = 0;

        Map<Character, Integer> neighbors = graph.getGraph().get(currentNode);
        if (neighbors != null) {
            for (char neighbor : neighbors.keySet()) {
                count += dfsMaxHops(neighbor, endNode, currentHops + 1, maxHops);
            }
        }

        return count;
    }

    public void countTracesWithNHops(char startNode, char endNode, int n) {
        System.out.println(dfsNHops(startNode, endNode, n, 0));
    }

    private int dfsNHops(char currentNode, char endNode, int n, int currentHops) {
        if (currentHops == n) {
            // If we've reached the desired number of hops, check if we're at the end node
            return currentNode == endNode ? 1 : 0;
        }

        int count = 0;
        Map<Character, Integer> neighbors = graph.getGraph().get(currentNode);

        if (neighbors != null) {
            for (char neighbor : neighbors.keySet()) {
                // Recursively explore neighbors with increased hop count
                count += dfsNHops(neighbor, endNode, n, currentHops + 1);
            }
        }

        return count;
    }

    //dijkstra algorithm implementation to find the shortest path from source to destination
    public int shortestTraceLengthBetweenNodes(char startNode, char endNode) {
        if (startNode == endNode) {
            return sameNodeShortestPath(startNode);
        }

        Map<Character, Integer> distances = new HashMap<>();
        PriorityQueue<NodeWithDistance> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        // Initialize distances
        for (char node : graph.getGraph().keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(startNode, 0);

        queue.offer(new NodeWithDistance(startNode, 0));
        while (!queue.isEmpty()) {
            NodeWithDistance currentNode = queue.poll();

            if (currentNode.node == endNode) {
                return distances.get(endNode);
            }
            if (currentNode.distance > distances.get(currentNode.node)) {
                continue;
            }

            Map<Character, Integer> neighbors = graph.getGraph().get(currentNode.node);
            if (neighbors != null) {
                for (char neighbor : neighbors.keySet()) {
                    int newDistance = currentNode.distance + neighbors.get(neighbor);
                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        queue.offer(new NodeWithDistance(neighbor, newDistance));
                    }
                }
            }
        }
        // If there is no path from startNode to endNode
        return -1;
    }

    // DFS implementation to find the shortest path when the start and end node are same
    public int sameNodeShortestPath(Character startNode) {
        // find all the paths from the node which end back on the same node. Due to
        // the check to avoid revisiting same node this result won't have last leg of the path i.e *-B
        List<List<Character>> allPaths = findAllRoutes(startNode);

        // add the last leg manually
        List<Integer> latency = new ArrayList<>();
        for (List<Character> paths : allPaths) {
            paths.add(startNode);
            // calculate average latency of each path
            latency.add(calculatePathWeight(paths));
        }
        // sort the result
        Collections.sort(latency);
        // return the smallest one
        return latency.get(0);
    }

    //Helper method
    private List<List<Character>> findAllRoutes(Character startNode) {
        List<List<Character>> routes = new ArrayList<>();
        Set<Character> visited = new HashSet<>();
        Stack<Character> stack = new Stack<>();

        dfs(startNode, startNode, visited, stack, routes);

        return routes;
    }

    // Helper method
    private void dfs(
            Character startNode, Character currentNode,
            Set<Character> visited, Stack<Character> stack,
            List<List<Character>> routes
    ) {
        visited.add(currentNode);
        stack.push(currentNode);

        for (Map.Entry<Character, Integer> neighbor : graph.getGraph().get(currentNode).entrySet()) {
            Character nextNode = neighbor.getKey();
            if (nextNode.equals(startNode) && stack.size() > 1) {
                List<Character> route = new ArrayList<>(stack);
                routes.add(route);
            } else if (!visited.contains(nextNode)) {
                dfs(startNode, nextNode, visited, stack, routes);
            }
        }

        visited.remove(currentNode);
        stack.pop();
    }


    public static void main(String[] args) {
        String inputFile = "input.txt";
        Solution solution = new Solution(inputFile);
        solution.averageLatency(Arrays.asList('A', 'B', 'C'));
        solution.averageLatency(Arrays.asList('A', 'D'));
        solution.averageLatency(Arrays.asList('A', 'D', 'C'));
        solution.averageLatency(Arrays.asList('A', 'E', 'B', 'C', 'D'));
        solution.averageLatency(Arrays.asList('A', 'E', 'D'));
        solution.countTracesWithMaxHops('C', 'C', 3);
        solution.countTracesWithNHops('A', 'C', 4);
        System.out.println(solution.shortestTraceLengthBetweenNodes('A', 'C'));
        System.out.println(solution.shortestTraceLengthBetweenNodes('B', 'B'));
    }
}