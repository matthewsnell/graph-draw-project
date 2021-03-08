package com.graph.draw;

import com.badlogic.gdx.Input;

public class InputListener implements Input.TextInputListener {
    WeightedGraph graph;

    InputListener(WeightedGraph graph) {
        this.graph = graph;
    }

    @Override
    public void input(String text) {
        graph.setDialogInputValue(text);
    }

    @Override
    public void canceled() {
        graph.setIsdialogInputCancelled(true);
    }
}
