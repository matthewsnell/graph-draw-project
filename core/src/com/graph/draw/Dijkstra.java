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
    private static void addTempLabels(ArrayList<Node> nodes, Node start) {
        int currentId = start.getId();
        Node currentNode = nodes.get(currentId);
        HashMap<Integer, Connection> connections  = currentNode.getConnections();

        currentNode.setPermLabel(currentNode.getTempLabel());

        for (int conId : connections.keySet()) {
            int connectionLength = connections.get(conId).getLength();
            int connectionTemp = nodes.get(conId).getTempLabel();

            if (connectionLength + currentNode.getPermLabel() < connectionTemp || connectionTemp < 0) {
                nodes.get(conId).setTempLabel(connectionLength + currentNode.getPermLabel());
            }
        }

    }

    // Find the lowest temp label on the graph
    private static Node getLowestNode(ArrayList<Node> nodes) {
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
    private static void findPath(ArrayList<Node> nodes, Node startNode, Node endNode) {
        while (endNode.getId() != startNode.getId()) {
            HashMap<Integer, Connection> endNodeCons = endNode.getConnections();
            for (int conId : endNodeCons.keySet()) {
                // check if this
                if (endNode.getPermLabel() - endNodeCons.get(conId).getLength() == nodes.get(conId).getPermLabel()) {
                    endNodeCons.get(conId).setInPath(true);
                    nodes.get(conId).getConnections().get(endNode.getId()).setInPath(true);
                    nodes.get(conId).setInPath(true);
                    endNode = nodes.get(conId);
                    break;
                }
            }
        }
    }

    static void run(ArrayList<Node> nodes, Node start, Node end) {
        for (Node node : nodes) {
            node.setTempLabel(-1);
            node.setPermLabel(-1);
            node.setStageNumber(-1);
            node.setconnectionNotInPath();
            node.setInPath(false);
        }
        setupStart(start);
        addTempLabels(nodes, start);
        while (!isComplete(nodes)) {
            addTempLabels(nodes, getLowestNode(nodes));
        }
        end.setInPath(true);
        findPath(nodes, start, end);
    }
}