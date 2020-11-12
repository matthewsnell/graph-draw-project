package com.graph.draw;

import java.util.*;

public abstract class MatthewsMST {
    static void run(ArrayList<Node> nodes) {
        ArrayList<Connection> connections = new ArrayList<>();
        for (Node node : nodes) {
            for (Connection con : node.getConnections().values()) {
                connections.add(con);
            }
        }

        removeGreatest(connections, nodes);

    }

    static void DFS(ArrayList<Node> nodes) {
        Queue<Node> discovered = new LinkedList<Node>();
        Stack<Node> parentNode = new Stack<>();
        Node currentNode = nodes.get(0);
        ArrayList<Integer> currentNodeCons = new ArrayList<>(currentNode.getConnections().keySet());

    }

    static void removeGreatest(ArrayList<Connection> connections, ArrayList<Node> nodes) {
        DFS(nodes);
        Connection largest = largestConnection(connections);
        if (largest.getEnd().getConnections().size() > 1 && largest.getStart().getConnections().size() > 1) {
            largest.getStart().removeConnection(largest.getEnd());
            largest.getEnd().removeConnection(largest.getStart());
            System.out.println(connections.size());
            System.out.println(nodes.size());
            if (connections.size() > nodes.size()*2) {
                removeGreatest(connections, nodes);
            }
        }
        else {
            connections.remove(connections.indexOf(largest));
        }
    }
    static Connection largestConnection(ArrayList<Connection> connections) {
        Connection biggest = connections.get(0);
        for (Connection con: connections) {
            if (con.getLength() > biggest.getLength()) {
                biggest = con;
            }
        }
        return biggest;
    }
}
