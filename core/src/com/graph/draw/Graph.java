package com.graph.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Set;
import java.util.Random;
import java.lang.Math;

public class Graph {
    ArrayList<Node> nodes;
    Node conStartNode;
    Node pathStartNode = null;
    Node pathEndNode = null;
    Node currentLockedNode;
    ShapeRenderer sr;
    int nodeSelectCount;
    boolean autoRunDijkstra;
    Random rand = new Random();


    Graph(ShapeRenderer sr) {
        nodes = new ArrayList<Node>();
        this.sr = sr;
    }

    void addRandomNode() {
        int xChord = rand.nextInt(1760) + 20;
        int yChord = rand.nextInt(950) + 20;
        boolean newChords = false;

        for (Node node : nodes) {
            if ((Math.pow(xChord - node.getX(), 2) + Math.pow(yChord - node.getY(), 2)) < 600) {
                newChords = true;
            }
        }

        if (newChords != true) {
            nodes.add(new Node(nodes.size(), xChord, yChord, sr));
        }
        nodeSelectCount = 0;
        if (nodes.size() > 1) {
            conStartNode.setSelected(false);
        } else {
            conStartNode = nodes.get(0);
        }
    }

    void addNode() {
        nodes.add(new Node(nodes.size(), Gdx.input.getX(), 1000 - Gdx.input.getY(), sr));
        nodeSelectCount = 0;
        if (nodes.size() > 1) {
            conStartNode.setSelected(false);
        } else {
            conStartNode = nodes.get(0);
        }
    }

    void addConnection(Node startNode, Node selectedNode) {
        startNode.addConnection(selectedNode, nodes);
        selectedNode.addConnection(startNode, nodes);
    }

    void removeConnection(Node startNode, Node selectedNode) {
        startNode.removeConnection(selectedNode);
        selectedNode.removeConnection(startNode);
    }

    // enables connections to be added and removed between nodes
    void manageConnection(Node nodeUnderMouse) {
        if (nodeUnderMouse != null) {
            if (nodeSelectCount == 0) {
                conStartNode = nodeUnderMouse;
                nodeSelectCount++;
                conStartNode.setSelected(true);
            } else {
                if (conStartNode != nodeUnderMouse) {
                    if (conStartNode.hasConnection(nodeUnderMouse)) {
                        removeConnection(conStartNode, nodeUnderMouse);
                    } else {
                        addConnection(conStartNode, nodeUnderMouse);
                    }
                    nodeSelectCount = 0;
                    conStartNode.setSelected(false);
                    if (autoRunDijkstra) runDijkstras();
                }
            }
        }
    }

    void deleteNode(Node nodeToDelete) {
        if (nodeToDelete != null) {
            for (int con : nodeToDelete.getConnections().keySet()) {
                nodes.get(con).removeConnection(nodeToDelete);
            }
            nodes.remove(nodeToDelete.getId());

            for (Node node : nodes) {
                if (node.getId() > nodeToDelete.getId()) {
                    node.setId(node.getId() - 1);
                }
            }

            for (Node node : nodes) {
                Set<Integer> keySet = node.getConnections().keySet();
                for (int key : keySet) {
                    if (key > nodeToDelete.getId()) {
                        node.addTempConnection(nodes.get(key - 1), nodes);
                    } else {
                        node.addTempConnection(nodes.get(key), nodes);
                    }
                }
                node.setConnectionsToTemp();
                node.resetTempConnections();
            }
        }
    }

    void moveNode() {
        if (currentLockedNode != null) {
            currentLockedNode.setX(Gdx.input.getX());
            currentLockedNode.setY(1000 - Gdx.input.getY());
            for (Connection con : currentLockedNode.getConnections().values()) {
                con.calcLength();
                con.getEnd().getConnection(currentLockedNode).setLength(con.getLength());
            }
        }
    }

    // removes green colouring from nodes and connections
    void clearPath() {
        for (Node node : nodes) {
            node.setInPath(false);
            for (Connection con : node.getConnections().values()) {
                con.setInPath(false);
            }
        }
        autoRunDijkstra = false;
    }

    void setPathStartNode(Node node) {
        if (node != null) {
            if (pathStartNode != null) pathStartNode.setStart(false);
            pathStartNode = node;
            pathStartNode.setStart(true);
        }
    }

    void setPathEndNode(Node node) {
        if (node != null) {
            if (pathEndNode != null) pathEndNode.setEnd(false);
            pathEndNode = node;
            pathEndNode.setEnd(true);
        }
    }

    void resetCurrentLockedNode() {
        if (currentLockedNode != null) {
            currentLockedNode.setSelected(false);
            currentLockedNode = null;
        }
    }

    void setCurrentLockedNode(Node node) {
        if (node != null) {
            resetCurrentLockedNode();
            currentLockedNode = node;
            currentLockedNode.setSelected(true);
        } else {
            resetCurrentLockedNode();
        }
    }

    void draw() {
        for (Node node : nodes) {
            node.drawConnections();

        }
        for (Node node : nodes) {
            node.draw();
        }
    }

    void runDijkstras() {
        autoRunDijkstra = true;
        Dijkstra.run(nodes, pathStartNode, pathEndNode);
    }

    boolean doesOverlapExisting(Node startNode, Node toNode) {
        boolean doesOverlapLine = false;
        boolean doesOverLapNode = false;
        Vector2 startVec = new Vector2(startNode.getX(), toNode.getY());
        Vector2 endVec = new Vector2(toNode.getX(), toNode.getY());
        for (Node node : nodes) {
            if (startNode != node && toNode != node) {
                for (Connection con : node.getConnections().values()) {
                    if (con.getEnd() != startNode && con.getEnd() != toNode) {
                        if (Intersector.intersectSegments(startNode.getX(), startNode.getY(), toNode.getX(), toNode.getY(),
                                node.getX(), node.getY(), con.getEnd().getX(), con.getEnd().getY(),
                                null)) {
                            doesOverlapLine = true;
                        }
                    }
                }
                Vector2 nodeVec = new Vector2(node.getX(), node.getY());
                if (Intersector.intersectSegmentCircle(startVec, endVec, nodeVec, 500)) {
                    doesOverLapNode = true;
                }
            }
        }
        return doesOverlapLine || doesOverLapNode;
    }


    void autoConnect() {
        for (Node node : nodes) {
            int shortestDistance = 9999999;
            Node closest = null;
            for (Node loopNode : nodes) {
                if (loopNode != node) {
                    if (getDistanceBetweenNodes(node, loopNode) < shortestDistance) {
                        if (!node.hasConnection(loopNode)) {
                            if (!doesOverlapExisting(node, loopNode)) {
                                closest = loopNode;
                                shortestDistance = getDistanceBetweenNodes(node, loopNode);
                            }
                        }
                    }
                }
            }
            if (closest != null) {
                node.addConnection(closest, nodes);
                closest.addConnection(node, nodes);
            }
        }
    }

    void randomConnect() {
        for (Node node : nodes) {
            node.addConnection(nodes.get(rand.nextInt(nodes.size() -1)) , nodes);
        }
    }

    void checkOverlap(Node firstStart, Node firstEnd, Node secStart, Node secEnd) {
        System.out.println(Intersector.intersectSegments(firstStart.getX(), firstStart.getY(), firstEnd.getX(), firstEnd.getY(), secStart.getX(), secStart.getY(), secEnd.getX(), secEnd.getY(), null));
    }

    ArrayList<Node> getNodes() {
        return nodes;
    }

    boolean isAutoRunDijkstra() {
        return autoRunDijkstra;
    }

    void setAutoRunDijkstra(boolean b) {
        autoRunDijkstra = b;
    }

    int getDistanceBetweenNodes(Node startNode, Node toNode) {
        int xDistance = (int) (toNode.getX() - startNode.getX());
        int yDistance = (int) (toNode.getY() - startNode.getY());

        if (xDistance < 0) { xDistance *= -1; }
        if (yDistance < 0) { yDistance *= -1; }

        return (int) (Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2)));
    }
}