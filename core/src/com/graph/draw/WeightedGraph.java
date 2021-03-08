package com.graph.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

class WeightedGraph extends Graph implements Input.TextInputListener {
    Stage stage;
    Skin skin;
    String dialogInputValue = "";
    boolean isdialogInputCancelled = false;

    WeightedGraph(ShapeRenderer sr, Stage stage, Skin skin) {
        super(sr, stage, skin);
        this.stage = stage;
        this.skin = skin;
    }

    @Override
    public void input(String text) {
        System.out.println(text);
    }

    @Override
    public void canceled() {

    }

    @Override
    void removeConnection(Node startNode, Node selectedNode) {
        startNode.getConnection(selectedNode).removeLabel();
        selectedNode.getConnection(startNode).removeLabel();
        super.removeConnection(startNode, selectedNode);
    }

    // Prevents auto connection which uses lengths as weights
    @Override
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
                addRndWeightConnection(closest, node);
            }
        }
    }


    @Override
    void addConnection(Node startNode, Node selectedNode) {
        InputListener listener = new InputListener(this);
        Gdx.input.getTextInput(listener, "Connection Length", "", "Length");
        Integer weight = -1;
        while (dialogInputValue == "" && !isdialogInputCancelled) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            weight = Integer.valueOf(dialogInputValue);
        } catch (NumberFormatException e) {

        }

        if (dialogInputValue != "" && !isdialogInputCancelled && weight != -1) {
            super.addConnection(startNode, selectedNode);
            startNode.getConnection(selectedNode).setLength(weight);
            selectedNode.getConnection(startNode).setLength(weight);
            startNode.getConnection(selectedNode).addLabel(weight);
            selectedNode.getConnection(startNode).addLabel(weight);
        }

        isdialogInputCancelled = false;
        dialogInputValue = "";
    }

    void addRndWeightConnection(Node startNode, Node selectedNode) {
        super.addConnection(startNode, selectedNode);
        int weight = (int) (Math.random() * 100);
        startNode.getConnection(selectedNode).setLength(weight);
        selectedNode.getConnection(startNode).setLength(weight);
        startNode.getConnection(selectedNode).addLabel(weight);
        selectedNode.getConnection(startNode).addLabel(weight);
    }

    @Override
    void deleteNode(Node nodeToDelete) {
        super.deleteNode(nodeToDelete);
        ArrayList<Connection> connections = new ArrayList<>();
        for (Node node : nodes) {
            connections.addAll(node.getConnections().values());
        }

        for (Connection con : connections) {
            con.addLabel(con.getLength());
        }
    }

    // Prevents updating weight with length after node is moved
    @Override
    void moveNode() {
        int x = Gdx.input.getX();
        int y = 1000 - Gdx.input.getY();
        if (isInBounds(x, y)) {
            if (currentLockedNode != null) {
                currentLockedNode.setX(x);
                currentLockedNode.setY(y);
                for (Connection con : currentLockedNode.getConnections().values()) {
                    con.setLabelPosition();
                    con.getEnd().getConnection(con.getStart()).setLabelPosition();
                }
            }
        }
    }

    void DrawConnectionLengths() {
        Label.LabelStyle weightStyle = new Label.LabelStyle();
        weightStyle.font = skin.getFont("arial-small");
        weightStyle.fontColor = Colours.darkGrey;
        }

    void setWeight (int weight, Node startNode, Node endNode) {
        startNode.getConnection(endNode).setLength(weight);
        endNode.getConnection(startNode).setLength(weight);
    }

    void setDialogInputValue(String inputValue) {
        dialogInputValue = inputValue;
    }

    void setIsdialogInputCancelled(boolean isCancelled) {
        isdialogInputCancelled = isCancelled;
    }

    @Override
    void draw() {
        super.draw();
        DrawConnectionLengths();
    }

    @Override
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
                addRndWeightConnection(node, closest);
            }
        }
    }

    @Override
    void clear() {
        for (Node node: nodes) {
            for (Connection con: node.getConnections().values()) {
                con.removeLabel();

            }
        }
    }
}
