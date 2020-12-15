package com.graph.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.Set;

class Graph {
    private Node conStartNode;
    private Node pathStartNode = null;
    private Node pathEndNode = null;
    private int nodeSelectCount;
    private boolean autoRunDijkstra;
    private boolean visualise;
    Node currentLockedNode;
    ShapeRenderer sr;
    ArrayList<Node> nodes;
    Stage stage;
    Skin skin;

    Graph(ShapeRenderer sr, Stage stage, Skin skin) {
        nodes = new ArrayList<Node>();
        this.sr = sr;
        this.stage = stage;
        this.skin = skin;
    }

    protected boolean isInBounds(float x, float y) {
        boolean isValid = false;
        if (x > 320 && x < 1720 && y < 920 && y > 90)
            isValid = true;
        return  isValid;
    }

    void addNode() {
        float x = Gdx.input.getX();
        float y = 1000 - Gdx.input.getY();
        if (isInBounds(x, y)) {
            nodes.add(new Node(nodes.size(), x, y, sr, stage, skin));
            nodeSelectCount = 0;
            if (nodes.size() > 1) {
                conStartNode.setSelected(false);
            } else {
                conStartNode = nodes.get(0);
            }
        }
    }

    void addConnection(Node startNode, Node selectedNode) {
        startNode.addConnection(selectedNode, nodes);
        selectedNode.addConnection(startNode, nodes);
    }

    void removeConnection(Node startNode, Node selectedNode) {
        startNode.getConnection(selectedNode).removeLabel();
        selectedNode.getConnection(startNode).removeLabel();
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
                nodeToDelete.getConnection(nodes.get(con)).removeLabel();
                nodes.get(con).getConnection(nodeToDelete).removeLabel();
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
                        node.getConnections().put(key -1, node.getConnections().get(key -1));

//                        node.addTempConnection(nodes.get(key - 1), nodes);
//                    } else {
//                        node.addTempConnection(nodes.get(key), nodes);
                    }
                }
//                node.setConnectionsToTemp();
//                node.resetTempConnections();
            }
        }
    }

    void moveNode() {
        float x = Gdx.input.getX();
        float y = 1000 - Gdx.input.getY();
        if (isInBounds(x, y)) {
            if (currentLockedNode != null) {
                currentLockedNode.setX(Gdx.input.getX());
                currentLockedNode.setY(1000 - Gdx.input.getY());
                for (Connection con : currentLockedNode.getConnections().values()) {
                    con.calcLength();
                    con.getEnd().getConnection(currentLockedNode).setLength(con.getLength());
                }
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
        int cons = 0;

        for (Node node : nodes) {
            node.drawConnections();
            cons += node.getConnections().size();

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

    void autoConnectNonPlanar() {
        for (Node node : nodes) {
            int shortestDistance = 9999999;
            Node closest = null;
            for (Node loopNode : nodes) {
                if (loopNode != node) {
                    if (getDistanceBetweenNodes(node, loopNode) < shortestDistance) {
                        if (!node.hasConnection(loopNode)) {
                                closest = loopNode;
                                shortestDistance = getDistanceBetweenNodes(node, loopNode);
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

    void setVisualise(boolean visualise) {
        this.visualise = visualise;
    }

    void keyListeners(Node nodeUnderMouse, boolean disableListeners) {
        if (!disableListeners) {
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                if (nodeUnderMouse == null) {
                    addNode();
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) && !Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                resetCurrentLockedNode();
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (nodeUnderMouse == null) {
                    resetCurrentLockedNode();
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) && (!Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                    && !Gdx.input.isKeyJustPressed(Input.Keys.M))) {
                clearPath();
            }

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (nodeUnderMouse == currentLockedNode || nodeUnderMouse == null) {
                    moveNode();
                    if (isAutoRunDijkstra()) runDijkstras();
                }
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                setCurrentLockedNode(nodeUnderMouse);
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (nodeUnderMouse == null) {
                    addNode();
                }
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                manageConnection(nodeUnderMouse);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                setPathStartNode(nodeUnderMouse);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                setPathEndNode(nodeUnderMouse);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                runDijkstras();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
                deleteNode(nodeUnderMouse);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                autoConnect();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // do something important here, asynchronously to the rendering thread
                        MatthewsMST.run(nodes, visualise);
                        // post a Runnable to the rendering thread that processes the result
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
                            }
                        });
                    }
                }).start();
            }

            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                autoConnectNonPlanar();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                for (int i = 0; i < 5000; i++) {
                    float x = (int)(Math.random() * 1920);
                    float y = (int)(Math.random() * 1000);
                    boolean valid = true;
                    for (Node node: nodes) {
                        if (Math.pow(x - node.getX(),2)+ Math.pow(y - node.getY(),2) < 500) {
                            valid = false;
                        }
                    }
                    if (isInBounds(x, y) && valid) {
                        nodes.add(new Node(nodes.size(), x, y, sr, stage, skin));
                        nodeSelectCount = 0;
                        if (nodes.size() > 1) {
                            conStartNode.setSelected(false);
                        } else {
                            conStartNode = nodes.get(0);
                        }
                    }

                }
            }
        }

        void clear() {
        }
}