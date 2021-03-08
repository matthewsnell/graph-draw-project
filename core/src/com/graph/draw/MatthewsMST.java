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

    static void removeGreatest(ArrayList<Connection> connections, ArrayList<Connection> actualConnections, ArrayList<Node> nodes, boolean visualise) {
        int time = (int) (2500/(Math.pow(connections.size(), 0.6)));
        boolean weighted = false;
        Connection largest = largestConnection(connections);
        weighted = largest.hasLabel();
        largest.setInPath(true);
        if (visualise) {
            try {
            Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Node conStart = largest.getStart();
        Node conEnd = largest.getEnd();
        int length = largest.getLength();
        connections.remove(largest);
        connections.remove(largest.getEnd().getConnection(largest.getStart()));
        actualConnections.remove(largest);
        actualConnections.remove(largest.getEnd().getConnection(largest.getStart()));
        largest.getStart().removeConnection(largest.getEnd());
        largest.getEnd().removeConnection(largest.getStart());
        if (GraphFunctions.DFS(nodes).size() < nodes.size()) {
            conStart.addConnection(conEnd, nodes);
            Connection startEndCon = conStart.getConnection(conEnd);
            startEndCon.setLength(length);
            startEndCon.setGreen(true);
            if (weighted) startEndCon.addLabel(startEndCon.getLength());

            if (visualise) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            startEndCon.setGreen(false);
            conEnd.addConnection(conStart, nodes);
            Connection endStartCon = conEnd.getConnection(conStart);
            endStartCon.setLength(length);
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

