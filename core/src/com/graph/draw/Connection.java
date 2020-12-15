package com.graph.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

class Connection {
    private Node start;
    private Node end;
    private ShapeRenderer sr;
    private int length;
    private boolean isInPath = false;
    private boolean isGreen = false;
    Label label = null;
    Skin skin;
    Stage stage;

    Connection(Node startNd, Node endNd, ShapeRenderer shaperenderer, Stage stage, Skin skin) {
        start = startNd;
        end = endNd;
        sr = shaperenderer;
        this.skin = skin;
        this.stage = stage;
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

    Node getStart() {
        return start;
    }
    void setInPath(boolean b) {
        isInPath = b;
    }

    void setGreen(boolean b) {
        isGreen = b;
    }

    void setLength(Integer leng) {
        length = leng;
    }

    void setStart(Node start) {
        this.start = start;
    }

    void setEnd(Node end) {
        this.end = end;
    }

    void addLabel(Integer weight) {
        Label.LabelStyle weightStyle = new Label.LabelStyle();
        weightStyle.font = skin.getFont("arial-small");
        weightStyle.fontColor = Colours.darkGrey;
        label = new Label(weight.toString(), weightStyle);
        stage.addActor(label);
        label.setZIndex(1);
        setLabelPosition();
    }

    void setLabelPosition() {
        int xMid = (int) (start.getX() + end.getX()) / 2;
        int yMid = (int) ((start.getY() + end.getY()) / 2);
        label.setPosition(xMid, yMid);
    }

    void removeLabel() {
        if (!(label == null)) {
            label.remove();
        }
    }

    void draw() {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Colours.darkGrey);
        if (isInPath) {
            sr.setColor(Color.PINK);
            sr.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), 3);

        }
        if (isGreen) {
            sr.setColor(Colours.green);
            sr.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), 3);
        }
        sr.line(start.getX(), start.getY(), end.getX(), end.getY());
        sr.end();
    }

}