package com.graph.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.util.HashMap;
import java.util.Scanner;

class WeightedGraph extends Graph {
    Scanner sc = new Scanner(System.in);
    SpriteBatch batch;
    BitmapFont font;
    Stage stage;
    Label weight;
    Label.LabelStyle weightStyle;
    Skin skin;
    HashMap<Connection, Label> labels;
    WeightedGraph(ShapeRenderer sr, Stage stage, Skin skin) {
        super(sr);
        this.stage = stage;
        this.skin = skin;
        labels = new HashMap<>();
    }

    @Override
    void removeConnection(Node startNode, Node selectedNode) {
        labels.get(startNode.getConnection(selectedNode)).remove();
        labels.get(selectedNode.getConnection(startNode)).remove();
        labels.remove(startNode.getConnection(selectedNode));
        labels.remove(selectedNode.getConnection(startNode));
        super.removeConnection(startNode, selectedNode);
    }

    // Prevents auto connection which uses lengths as weights
    @Override
    void autoConnect() {
    }

    @Override
    void addConnection(Node startNode, Node selectedNode) {
        super.addConnection(startNode, selectedNode);
        System.out.println("please enter connection weight:");
        int weight = sc.nextInt();
        startNode.getConnection(selectedNode).setLength(weight);
        selectedNode.getConnection(startNode).setLength(weight);
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
                    labels.get(con).remove();
                    labels.get(con.getEnd().getConnection(currentLockedNode)).remove();
                    labels.remove(con);
                    labels.remove(con.getEnd().getConnection(currentLockedNode));

                }
            }
        }
    }

    void DrawConnectionLengths() {
        Label.LabelStyle weightStyle = new Label.LabelStyle();
        weightStyle.font = skin.getFont("default-font");
        weightStyle.fontColor = Colours.darkGrey;
        for (Node node: nodes) {
            for (Connection con : node.getConnections().values()) {
                int xMid = (int) (node.getX() + con.getEnd().getX()) / 2;
                int yMid = (int) ((node.getY() + con.getEnd().getY()) / 2);
                String length = String.valueOf(con.getLength());
                System.out.println(labels.get(con));
                if (labels.get(con) == null) {
                    labels.put(con, new Label(length, weightStyle));
                    stage.addActor(labels.get(con));
                    labels.get(con).setPosition(xMid, yMid);
                }
            }
        }
    }

    @Override
    void draw() {
        super.draw();
        DrawConnectionLengths();
    }

}
