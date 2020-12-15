package com.graph.draw;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Main extends ApplicationAdapter {
	ShapeRenderer sr;
	Graph graph;
	int screenHeight = 1000;
	Stage stage;
	Skin skin;
	boolean isGraphLocked;
	TextButton cancelClearBtn;
	TextButton resetBtn;
	TextButton loadBtn;
	TextButton saveBtn;
	Image sideBar;
	Button weightedToggle;
	Button directionalToggle;
	Image overlay;
	Image clearMessageBox;
	TextButton clearAcceptBtn;
	Label weightedLabel;
	Label directionalLabel;
	Label titleLabel;
	Image sidePanel1;
	Label pathFindingLabel;
	Button visualiseToggle;
	Label visualiseLabel;

	@Override
	public void create (){
		isGraphLocked = false;
		sr= new ShapeRenderer();
		stage = new Stage(new ScreenViewport());
		skin = new Skin();
		Gdx.input.setInputProcessor(stage);
		graph = new Graph(sr, stage, skin);
		skin.add("red-btn", new Texture(Gdx.files.internal("btn-red.png")));
		skin.add("default-font", new BitmapFont(Gdx.files.internal("Arial-Medium.fnt")));
		skin.add("swiss-font", new BitmapFont(Gdx.files.internal("Swiss.fnt")));
		skin.add("grey-btn", new Texture(Gdx.files.internal("btn-grey.png")));
		skin.add("toggle-off", new Texture(Gdx.files.internal("toggle-switch-red.png")));
		skin.add("toggle-on", new Texture(Gdx.files.internal("toggle-switch-green.png")));
		skin.add("arial-font", new BitmapFont(Gdx.files.internal("Arial.fnt")));
		skin.add("arial-bold-large", new BitmapFont(Gdx.files.internal("Arial-Bold-Medium.fnt")));
		skin.add("arial-med", new BitmapFont(Gdx.files.internal("Arial-Medium-Large.fnt")));
		skin.add("arial-small", new BitmapFont(Gdx.files.internal("Arial-Small.fnt")));

		Label.LabelStyle subHeadingLableStyle = new Label.LabelStyle();
		subHeadingLableStyle.font = skin.getFont("default-font");
		subHeadingLableStyle.fontColor = Colours.red;

		Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
		titleLabelStyle.font = skin.getFont("arial-med");
		titleLabelStyle.fontColor = Colours.darkGrey;

		TextButton.TextButtonStyle greyBtnStyle = new TextButton.TextButtonStyle();
		greyBtnStyle.up = skin.newDrawable("grey-btn");
		greyBtnStyle.font = skin.getFont("default-font");
		greyBtnStyle.fontColor = Colours.darkGrey;


		TextButton.TextButtonStyle redBtnStyle = new TextButton.TextButtonStyle();
		redBtnStyle.up = skin.newDrawable("red-btn");
		redBtnStyle.font = skin.getFont("default-font");
		redBtnStyle.fontColor = Colours.grey;

		Button.ButtonStyle toggleStyle = new Button.ButtonStyle();
		toggleStyle.up = skin.newDrawable("toggle-off");
		toggleStyle.checked = skin.newDrawable("toggle-on");

		Label.LabelStyle textStyle = new Label.LabelStyle();
		textStyle.font = skin.getFont("default-font");
		textStyle.fontColor = Colours.darkGrey;

		resetBtn = new TextButton("Reset", redBtnStyle);
		loadBtn = new TextButton("Load", greyBtnStyle);
		saveBtn = new TextButton("Save", greyBtnStyle);
		sideBar = new Image(new Texture(Gdx.files.internal("side-bar-grey.png")));
		weightedToggle = new Button(toggleStyle);
		weightedLabel = new Label("Weighted", textStyle);
		overlay = new Image(new Texture(Gdx.files.internal("bg-overlay-grey.png")));
		clearMessageBox = new Image(new Texture(Gdx.files.internal("clear-message.png")));
		clearAcceptBtn = new TextButton("Clear", redBtnStyle);
		cancelClearBtn = new TextButton("Cancel", greyBtnStyle);
		directionalLabel = new Label("Directional", textStyle);
		directionalToggle = new Button(toggleStyle);
		titleLabel = new Label("Graph Mode", titleLabelStyle);
		sidePanel1 = new Image(new Texture(Gdx.files.internal("panel.png")));
		pathFindingLabel = new Label("Pathfinding", subHeadingLableStyle);
		visualiseToggle = new Button(toggleStyle);
		visualiseLabel = new Label("Visualise", textStyle);

		stage.addActor(sideBar);
		stage.addActor(loadBtn);
		stage.addActor(saveBtn);
		stage.addActor(resetBtn);
		stage.addActor(weightedToggle);
		stage.addActor(directionalLabel);
		stage.addActor(directionalToggle);
		stage.addActor(weightedLabel);
		stage.addActor(titleLabel);
		stage.addActor(sidePanel1);
		stage.addActor(pathFindingLabel);
		stage.addActor(visualiseLabel);
		stage.addActor(visualiseToggle);
		stage.addActor(overlay);
		stage.addActor(clearMessageBox);
		stage.addActor(clearAcceptBtn);
		stage.addActor(cancelClearBtn);

		resetBtn.setPosition(1630,920);
		saveBtn.setPosition(1630, 20);
		loadBtn.setPosition(1460, 20);
		clearAcceptBtn.setPosition(910, 460);
		cancelClearBtn.setPosition(740, 460);
		weightedToggle.setPosition(860, 920);
		weightedLabel.setPosition(780, 945);
		directionalToggle.setPosition(650, 920);
		directionalLabel.setPosition(570, 945);
		titleLabel.setPosition(10, 940);
		sidePanel1.setPosition(-13,630);
		pathFindingLabel.setPosition(10, 600);
		visualiseLabel.setPosition(990, 945);
		visualiseToggle.setPosition(1060, 920);


		overlay.setVisible(false);
		cancelClearBtn.setVisible(false);
		clearAcceptBtn.setVisible(false);
		clearMessageBox.setVisible(false);


		visualiseToggle.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent changeEvent, Actor actor) {
				if (visualiseToggle.isChecked()) {
					graph.setVisualise(true);
					System.out.println("check");
				} else {
					graph.setVisualise(false);
					System.out.println("uncheck");
				}
			}
		});

		resetBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showClearWarning();
				isGraphLocked = true;
			}
		});

		cancelClearBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hideClearWarning();
			}
		});

		clearAcceptBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				hideClearWarning();
				graph.clear();
				if (weightedToggle.isChecked()) {
					graph = new WeightedGraph(sr, stage, skin);
				} else {
					graph = new Graph(sr, stage, skin);
				}
			}
		});

		weightedToggle.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (weightedToggle.isChecked()) {
					graph = new WeightedGraph(sr, stage, skin);
				} else {
					graph = new Graph(sr, stage, skin);
				}
			}
		});

	}

	void showClearWarning() {
		overlay.setVisible(true);
		cancelClearBtn.setVisible(true);
		clearAcceptBtn.setVisible(true);
		clearMessageBox.setVisible(true);
		isGraphLocked = true;
	}

	void hideClearWarning() {
		overlay.setVisible(false);
		cancelClearBtn.setVisible(false);
		clearAcceptBtn.setVisible(false);
		clearMessageBox.setVisible(false);
		isGraphLocked = false;
	}

	Node getNodeUnderMouse() {
		Node nodeToSelect = null;
		for (Node node : graph.getNodes()) {
			if ((Math.pow(Gdx.input.getX() - node.getX(), 2) + Math.pow(screenHeight - Gdx.input.getY() - node.getY(), 2) < 800)) {
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
		Gdx.gl.glHint(GL20.GL_GENERATE_MIPMAP_HINT, GL20.GL_NICEST);
		graph.keyListeners(getNodeUnderMouse(), isGraphLocked);
		graph.draw();
		stage.act();
		stage.draw();
		System.out.println(stage.getActors());
	}



	@Override
	public void dispose() {
		sr.dispose();
		stage.dispose();
		skin.dispose();
	}
}
