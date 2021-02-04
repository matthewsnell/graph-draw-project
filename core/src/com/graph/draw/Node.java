package com.graph.draw;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;
import java.util.HashMap;

class Node {
    private int id;
    private float x;
    private float y;
    private HashMap<Integer, Connection> connections = new HashMap<>();
    private HashMap<Integer, Connection> tempConnections = new HashMap<>();
    private ShapeRenderer sr;
    private boolean isInPath = false;
    private boolean isSelected = false;
    private boolean isStart  = false;
    private boolean isEnd = false;
    private int tempLabel;
    private int permLabel;
    private int stageNumber;
    private Stage stage;
    private Skin skin;

    Node(int id, float x, float y, ShapeRenderer shaperenderer, Stage stage, Skin skin) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.stage = stage;
        this.skin = skin;
        sr = shaperenderer;
        tempLabel = -1;
        permLabel = -1;
        stageNumber = -1;
    }


    void addConnection(Node toNode, ArrayList<Node> nodes) {
        connections.put(toNode.getId(), new Connection(nodes.get(id), toNode, sr, stage, skin));
    }

    void removeConnection(Node toNode) {
        connections.remove(toNode.getId());
    }

    void addTempConnection(Node toNode, ArrayList<Node> nodes) {
        tempConnections.put(toNode.getId(), new Connection(nodes.get(id), toNode, sr, stage, skin));
    }


    HashMap<Integer, Connection> getConnections() {
        return connections;
    }


    boolean hasConnection(Node toNode) {
        boolean exists = false;
        if (connections.get(toNode.getId()) != null) {
            exists = true;
        }
        return exists;
    }

    Connection getConnection(Node toNode) {
        return connections.get(toNode.getId());
    }

    void setConnectionsToTemp() {
        for (Connection con: connections.values()) {
            con.removeLabel();
        }
        connections = tempConnections;
    }

    void resetTempConnections() {
        tempConnections =  new HashMap<Integer, Connection>();
    }
    void drawConnections() {
        ArrayList<Connection> cons = new ArrayList<Connection>(connections.values());
        for (Connection vert : cons) {
            vert.draw();
        }
    }

    void setconnectionNotInPath() {
        for (Connection vert : connections.values()) {
            vert.setInPath(false);
        }
    }

    void setStart(boolean b) {
        isStart = b;
    }

    void setEnd(boolean b) {
        isEnd = b;
    }

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    void setTempLabel(int temp) {
        tempLabel = temp;
    }

    void setPermLabel(int perm) {
        permLabel = perm;
    }

    void setStageNumber(int stage) {
        stageNumber = stage;
    }

    void setX(int newX) {
        x = newX;
    }

    void setY(int newY) {
        y = newY;
    }

    void setId(int id) {
        this.id = id;
    }

    void setInPath (boolean b){
        isInPath = b;
    }

    void setSelected(boolean b) {
        isSelected = b;
    }

    int getTempLabel() {
        return tempLabel;
    }

    int getPermLabel() {
        return permLabel;
    }

    int getStageNumber() {
        return stageNumber;
    }

    int getId() {
        return id;
    }

    int getDegree () {
        return connections.size();
    }

    boolean isInPath() {
        return isInPath;
    }
    void draw() {
        sr.begin(ShapeType.Filled);
        sr.setColor(Colours.brown);
        if (isInPath)  sr.setColor(Colours.green);
        if (isStart) sr.setColor(Colours.green);
        if (isEnd) sr.setColor(Colours.red);
        if (isSelected) sr.setColor(Colours.darkGrey);
        sr.circle(x, y, 8);
        sr.end();
    }
}