package com.graph.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Scanner;

public class WeightedGraph extends Graph {
    Scanner sc = new Scanner(System.in);
    SpriteBatch batch;
    BitmapFont font;
    WeightedGraph(ShapeRenderer sr, SpriteBatch batch, BitmapFont font) {
        super(sr);
        this.batch = batch;
        this.font = font;
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
        if (currentLockedNode != null) {
            currentLockedNode.setX(Gdx.input.getX());
            currentLockedNode.setY(1000 - Gdx.input.getY());
        }
    }

    void DrawConnectionLengths() {
        batch.begin();
        font.getData().setScale((float) 1.5, (float) 1.5);
        for (Node node: nodes) {
            for (Connection con : node.getConnections().values()) {
                int xMid = (int) (node.getX() + con.getEnd().getX()) / 2;
                int yMid = (int) ((node.getY() + con.getEnd().getY()) / 2);
                String length = String.valueOf(con.getLength());
                font.draw(batch, length, xMid, yMid);
            }
        }
        batch.end();
    }

    @Override
    void draw() {
        super.draw();
        DrawConnectionLengths();
    }

}
