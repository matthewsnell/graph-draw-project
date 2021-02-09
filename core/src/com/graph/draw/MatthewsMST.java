package com.graph.draw;

import java.util.*;

public abstract class MatthewsMST {
    static void run(ArrayList<Node> nodes, boolean visualise) {
        ArrayList<Connection> connections = new ArrayList<>();
        ArrayList<Connection> actualConnections = new ArrayList<>();
        for (Node node : nodes) {
            connections.addAll(node.getConnections().values());
            actualConnections.addAll(node.getConnections().values());
        }
        removeGreatest(connections, actualConnections, nodes, visualise);

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

    static void removeGreatest(ArrayList<Connection> connections, ArrayList<Connection> actualConnections, ArrayList<Node> nodes, boolean visualise) {
        int time = (int) (2500/(Math.pow(connections.size(), 0.6)));
        Connection largest = largestConnection(connections);
        largest.setInPath(true);
        if (visualise) {
            try {
            Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Node conStart = largest.getStart();
        Node conEnd = largest.getEnd();
        int length = largest.getLength();
        connections.remove(connections.indexOf(largest));
        connections.remove(connections.indexOf(largest.getEnd().getConnection(largest.getStart())));
        actualConnections.remove(actualConnections.indexOf(largest));
        actualConnections.remove(actualConnections.indexOf(largest.getEnd().getConnection(largest.getStart())));
        largest.getStart().removeConnection(largest.getEnd());
        largest.getEnd().removeConnection(largest.getStart());
        if (DFS(nodes) < nodes.size()) {
            conStart.addConnection(conEnd, nodes);
            conStart.getConnection(conEnd).setLength(length);
            conStart.getConnection(conEnd).setGreen(true);
            if (visualise) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            conStart.getConnection(conEnd).setGreen(false);
            conEnd.addConnection(conStart, nodes);
            conEnd.getConnection(conStart).setLength(length);
            actualConnections.add(conStart.getConnection(conEnd));
            actualConnections.add(conEnd.getConnection(conStart));
        }

        if (actualConnections.size() >= nodes.size()*2){
            removeGreatest(connections, actualConnections, nodes, visualise);
        } else {
            for (Connection con: actualConnections) {
                con.setGreen(true);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Connection con: actualConnections) {
                con.setGreen(false);
            }
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
