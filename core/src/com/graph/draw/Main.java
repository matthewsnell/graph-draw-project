package com.graph.draw;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Main extends ApplicationAdapter {
	ShapeRenderer sr;
	Graph graph;
	int screenHeight = 1000;
	@Override
	public void create (){
		sr= new ShapeRenderer();
		graph = new Graph(sr);
	}

	Node getNodeUnderMouse() {
		Node nodeToSelect = null;
		for (Node node : graph.getNodes()) {
			if ((Math.pow(Gdx.input.getX() - node.getX(), 2) + Math.pow(screenHeight - Gdx.input.getY() - node.getY(), 2) < 400)) {
				nodeToSelect = node;
				break;
			}
		}
		return nodeToSelect;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(Colours.lightGrey.r, Colours.lightGrey.g, Colours.lightGrey.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		if (Gdx.input.isKeyJustPressed(Keys.K)) {
			graph.randomConnect();
		}

		if (Gdx.input.isKeyJustPressed(Keys.R)) {
			for (int i=0; i<500; i++) {

				graph.addRandomNode();

			}
		}

		if (Gdx.input.isKeyJustPressed(Keys.ANY_KEY) && !Gdx.input.isKeyJustPressed(Keys.M)) {
			graph.resetCurrentLockedNode();
		}

		if (Gdx.input.isKeyJustPressed(Keys.ANY_KEY) && (!Gdx.input.isKeyJustPressed(Keys.SPACE)
				&& !Gdx.input.isKeyJustPressed(Keys.M))) {
			graph.clearPath();
		}

		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			graph.moveNode();
			if (graph.isAutoRunDijkstra()) graph.runDijkstras();
		}
		if (Gdx.input.isKeyJustPressed(Keys.M)) {
			graph.setCurrentLockedNode(getNodeUnderMouse());
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
			graph.addNode();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
			graph.manageConnection(getNodeUnderMouse());
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
			graph.setPathStartNode(getNodeUnderMouse());
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			graph.setPathEndNode(getNodeUnderMouse());
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			graph.runDijkstras();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
			graph.deleteNode(getNodeUnderMouse());
		}
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			graph.autoConnect();
		}
		graph.draw();
	}
}