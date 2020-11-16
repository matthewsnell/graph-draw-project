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

    static Integer DFS(ArrayList<Node> nodes) {
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
        return discovered.size();


    }

    static void removeGreatest(ArrayList<Connection> connections, ArrayList<Node> nodes) {
        Connection largest = largestConnection(connections);
        Node conStart = largest.getStart();
        Node conEnd = largest.getEnd();
        connections.remove(connections.indexOf(largest));
        largest.getStart().removeConnection(largest.getEnd());
        largest.getEnd().removeConnection(largest.getStart());
        if (DFS(nodes) < nodes.size()) {

            System.out.println("add back");
        }
        System.out.println(connections.size());
        System.out.println(connections);
        System.out.println(nodes.size());
        if (connections.size() > nodes.size() *2){
            removeGreatest(connections, nodes);
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
