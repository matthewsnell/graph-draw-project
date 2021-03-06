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

public class Graph {
    private Node conStartNode;
    private Node pathStartNode = null;
    private Node pathEndNode = null;
    private int nodeSelectCount;
    private boolean autoRunDijkstra = false;
    private boolean visualise;
    private int shortestPath = -1;
    private int mstWeight = -1;
    Node currentLockedNode;
    ShapeRenderer sr;
    ArrayList<Node> nodes;
    Stage stage;
    Skin skin;

    Graph(ShapeRenderer sr, Stage stage, Skin skin) {
        nodes = new ArrayList<>();
        this.sr = sr;
        this.stage = stage;
        this.skin = skin;
    }

    protected boolean isInBounds(float x, float y) {
        boolean isValid = false;
        if (x > 320 && x < 1720 && y < 850 && y > 90) // area for drawing graphs
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

    // Used when loading graph from json format
    void addManualNode(int id, float x, float y) {
        nodes.add(new Node(id, x, y, sr, stage, skin));
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
        // start and back nodes both removed
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
                }
            }
        }
    }

    void deleteNode(Node nodeToDelete) {
        if (nodeToDelete != null &&
                (!autoRunDijkstra || (nodeToDelete != pathEndNode && nodeToDelete != pathStartNode))) {
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

            // Decrement IDs above deleted node's
            for (Node node : nodes) {
                Set<Integer> keySet = node.getConnections().keySet();
                for (int key : keySet) {
                    if (key > nodeToDelete.getId()) {
                        // Temp connections prevent Concurrent Modification Error
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
        setPathEndNode(null);
        setPathStartNode(null);
        shortestPath = -1;
    }

    void setPathStartNode(Node node) {
        if (pathStartNode != null) pathStartNode.setStart(false);
        pathStartNode = node;
        if (node != null) {
            pathStartNode.setStart(true);
        }
    }

    void setPathEndNode(Node node) {
        if (pathEndNode != null) pathEndNode.setEnd(false);
        pathEndNode = node;
        if (node != null) {
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
        // Graph must be connected with a valid start and end
        if (GraphFunctions.DFS(nodes).size() == nodes.size() && pathStartNode != null && pathEndNode != null) {
            if (visualise) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Dijkstra.run(nodes, pathStartNode, pathEndNode, visualise);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                shortestPath = pathEndNode.getPermLabel();
                            }
                        });
                    }
                }).start();
            } else {
                Dijkstra.run(nodes, pathStartNode, pathEndNode, false);
                shortestPath = pathEndNode.getPermLabel();
            }
            autoRunDijkstra = !visualise;
        } else {
            autoRunDijkstra = false;
            clearPath(); }
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

    Integer getShortestPath() {
        return shortestPath;
    }

    Integer getMSTWeight() {
        return mstWeight;
    }
    
    boolean isEulerian() {
        boolean eulerian = true;
        int oddDegrees = 0;
        for (Node node : nodes) {
            if (node.getDegree() % 2 != 0) {
                eulerian = false;
                break;
            }
        }
        return eulerian;
    }

    boolean isSemiEulerian() {
        boolean semiEulerian = true;
        int oddDegrees = 0;
        for (Node node : nodes) {
            if (node.getDegree() % 2 != 0) {
                if (oddDegrees > 2) {
                    semiEulerian = false;
                    break;
                } else {
                    oddDegrees++ ;
                }
            }
        }
        return  semiEulerian;
    }



    void keyListeners(Node nodeUnderMouse, boolean disableListeners) {
        if (!disableListeners) {
            if (autoRunDijkstra) {
                runDijkstras();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                if (nodeUnderMouse == null) {
                    addNode();
                }
            }
            if ((Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
                    Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
                    && !Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                resetCurrentLockedNode();
                mstWeight = -1;
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (nodeUnderMouse == null) {
                    resetCurrentLockedNode();
                }
            }

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (nodeUnderMouse == currentLockedNode || nodeUnderMouse == null) {
                    moveNode();
                    if (isAutoRunDijkstra()) {
                        runDijkstras();
                    } else {
                        clearPath();
                    }
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
                if (autoRunDijkstra) {
                    setAutoRunDijkstra(false);
                    clearPath();
                } else {
                    runDijkstras();
                }
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
                                ArrayList<Connection> connections = new ArrayList<>();
                                for (Node node : nodes) {
                                    connections.addAll(node.getConnections().values());
                                }
                                int totalWeight = 0;

                                for (Connection con : connections) {
                                    totalWeight += con.getLength();
                                }

                                mstWeight = totalWeight / 2;
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
            if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                clear();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                NearestNeighbour.run(nodes, nodes.get(0));
            }
        }

        void clear() {
            nodes = new ArrayList<>();
        }

}