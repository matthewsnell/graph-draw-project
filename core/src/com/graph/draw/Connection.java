package com.graph.draw;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Connection {
    Node start;
    Node end;
    ShapeRenderer sr;
    int length;
    boolean isInPath = false;

    Connection(Node startNd, Node endNd, ShapeRenderer shaperenderer) {
        start = startNd;
        end = endNd;
        sr = shaperenderer;
        calcLength();
    }

    void calcLength() {
        int xDistance = (int) (end.getX() - start.getX());
        int yDistance = (int) (end.getY() - start.getY());

        if (xDistance < 0) { xDistance *= -1; }
        if (yDistance < 0) { yDistance *= -1; }

        length = (int) (Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2)));
    }

    int getLength() {
        return length;
    }

    Node getEnd() {
        return end;
    }

    void setInPath(boolean b) {
        isInPath = b;
    }

    void setLength(Integer leng) {
        length = leng;
    }

    void draw() {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Colours.darkGrey);
        if (isInPath) { sr.setColor(Color.GREEN);}
        sr.line(start.getX(), start.getY(), end.getX(), end.getY());
        sr.end();
    }

}