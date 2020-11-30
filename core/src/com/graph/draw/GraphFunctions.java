package com.graph.draw;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class GraphFunctions {

    static Queue<Node> DFS(ArrayList<Node> nodes) {
        Queue<Node> discovered = new LinkedList<Node>();
        Stack<Node> parentNode = new Stack<>();
        Node currentNode = nodes.get(0);
        parentNode.push(currentNode);
        discovered.add(currentNode);
        while (parentNode.size() > 0) {
            currentNode = parentNode.peek();
            ArrayList<Integer> currentNodeCons = new ArrayList<>(currentNode.getConnections().keySet());
            boolean allChildrenDiscovered = true;
            for (Integer con: currentNodeCons) {
                if (!discovered.contains(nodes.get(con))) {
                    allChildrenDiscovered = false;
                    parentNode.push(nodes.get(con));
                    discovered.add(nodes.get(con));
                    break;
                }
            }
            if (allChildrenDiscovered) {
                parentNode.pop();
            }
        }
        return discovered;
    }

    static boolean isConnected(ArrayList<Node> nodes) {
        boolean isConnected = false;
        if (DFS(nodes).size() < nodes.size()) {
            isConnected = true;
        }
        return isConnected;
    }
}
