package com.graph.draw;

import java.util.ArrayList;
import java.util.HashMap;

abstract class Dijkstra {

    // Check if all nodes have a perm label
    private static boolean isComplete(ArrayList<Node> nodes) {
        boolean complete = true;
        for (Node node : nodes) {
            if (node.getPermLabel() == -1) {
                complete = false;
                break;
            }
        }
        return complete;
    }

    // setup first node for dijkstra's
    private static void setupStart(Node start) {
        start.setTempLabel(0);
        start.setInPath(true);
    }


    // update temp labels for all nodes connected to currentId
    private static void addTempLabels(ArrayList<Node> nodes, Node start, boolean visualise) {
        int currentId = start.getId();
        Node currentNode = nodes.get(currentId);
        int time = (int) (5000/(Math.pow(nodes.size(), 1)));

        if (visualise) {
            currentNode.setPink(true);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HashMap<Integer, Connection> connections  = currentNode.getConnections();

        currentNode.setPermLabel(currentNode.getTempLabel());
        for (int conId : connections.keySet()) {
            int connectionLength = connections.get(conId).getLength();
            int connectionTemp = nodes.get(conId).getTempLabel();
            Connection currentCon = currentNode.getConnection(nodes.get(conId));
            if (visualise) {
                currentCon.setGreen(true);
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (connectionLength + currentNode.getPermLabel() < connectionTemp || connectionTemp < 0) {
                nodes.get(conId).setTempLabel(connectionLength + currentNode.getPermLabel());

            } else {
                if (visualise) {
                    currentCon.setGreen(false);
                }
            }

        }
        currentNode.setPink(false);

    }

    // Find the lowest temp label on the graph
    private static Node getLowestNode(ArrayList<Node> nodes, boolean visualise) {
        int lowest = 999999999;
        int lowestId = -1;
        for (Node currentNode : nodes) {
            if (currentNode.getTempLabel() < lowest && currentNode.getTempLabel() != -1 && currentNode.getPermLabel() == -1) {
                lowest = currentNode.getTempLabel();
                lowestId = currentNode.getId();
            }
        }
        return nodes.get(lowestId);
    }

    // Find shortest path by interpreting perm labels
    private static void findPath(ArrayList<Node> nodes, Node startNode, Node endNode, boolean visualise) {
        int time = (int) (1000/(Math.pow(nodes.size(), 1)));
        while (endNode.getId() != startNode.getId()) {
            HashMap<Integer, Connection> endNodeCons = endNode.getConnections();

            if (visualise) {
                try {
                    Thread.sleep(time*2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int conId : endNodeCons.keySet()) {
                Connection currentCon = endNode.getConnection(nodes.get(conId));
                currentCon.setGreen(false);
                if (visualise) {
                    currentCon.setInPath(true);
                    try {
                        Thread.sleep(time*4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (endNode.getPermLabel() - endNodeCons.get(conId).getLength() == nodes.get(conId).getPermLabel()) {
                    endNodeCons.get(conId).setInPath(true);
                    nodes.get(conId).getConnections().get(endNode.getId()).setInPath(true);
                    nodes.get(conId).setInPath(true);
                    endNode = nodes.get(conId);
                    break;
                } else {
                    if (visualise) {
                        currentCon.setInPath(false);
                    }
                }
            }
        }
        if (visualise) {
            for (Node node: nodes) {
                for (Connection con: node.getConnections().values()) {
                    con.setGreen(false);
                }
            }
        }
    }

    static void run(ArrayList<Node> nodes, Node start, Node end, boolean visualise) {
        for (Node node : nodes) {
            node.setTempLabel(-1);
            node.setPermLabel(-1);
            node.setStageNumber(-1);
            node.setconnectionNotInPath();
            node.setInPath(false);
        }
        setupStart(start);
        addTempLabels(nodes, start, visualise);
        while (!isComplete(nodes)) {
            addTempLabels(nodes, getLowestNode(nodes, visualise), visualise);
        }
        end.setInPath(true);
        findPath(nodes, start, end, visualise);
    }
}