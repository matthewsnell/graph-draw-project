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
	Label pathfindinglabel;
	Button visualiseToggle;
	Label visualiseLabel;
	TextButton topAlertMessage;
	TextButton stopDijkstrasBtn;
	TextButton bottomAlertMessage;

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
		skin.add("yellow-btn", new Texture(Gdx.files.internal("btn-yellow.png")));
		skin.add("txt-bg-box", new Texture(Gdx.files.internal("text-background-box.png")));

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

		TextButton.TextButtonStyle alertBoxStyle = new TextButton.TextButtonStyle();
		alertBoxStyle.up = skin.newDrawable("txt-bg-box");
		alertBoxStyle.font = skin.getFont("default-font");
		alertBoxStyle.fontColor = Colours.red;

		Button.ButtonStyle toggleStyle = new Button.ButtonStyle();
		toggleStyle.up = skin.newDrawable("toggle-off");
		toggleStyle.checked = skin.newDrawable("toggle-on");

		Label.LabelStyle textStyle = new Label.LabelStyle();
		textStyle.font = skin.getFont("default-font");
		textStyle.fontColor = Colours.darkGrey;

		resetBtn = new TextButton("Reset", greyBtnStyle);
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
		pathfindinglabel = new Label("Pathfinding", subHeadingLableStyle);
		visualiseToggle = new Button(toggleStyle);
		visualiseLabel = new Label("Visualise", textStyle);
		topAlertMessage = new TextButton("This is the top alert", alertBoxStyle);
		stopDijkstrasBtn = new TextButton("Stop", redBtnStyle);
		bottomAlertMessage = new TextButton("This is the bottom alert", alertBoxStyle);

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
		stage.addActor(pathfindinglabel);
		stage.addActor(visualiseLabel);
		stage.addActor(visualiseToggle);
		stage.addActor(topAlertMessage);
		stage.addActor(stopDijkstrasBtn);
		stage.addActor(bottomAlertMessage);

		stage.addActor(overlay);
		stage.addActor(clearMessageBox);
		stage.addActor(clearAcceptBtn);
		stage.addActor(cancelClearBtn);


		resetBtn.setPosition(1630,920);
		saveBtn.setPosition(1630, 20);
		loadBtn.setPosition(1460, 20);
		clearAcceptBtn.setPosition(910, 460);
		cancelClearBtn.setPosition(740, 460);
		weightedToggle.setPosition(660, 920);
		weightedLabel.setPosition(580, 945);
		directionalToggle.setPosition(450, 920);
		directionalLabel.setPosition(370, 945);
		titleLabel.setPosition(10, 940);
		sidePanel1.setPosition(-13,630);
		pathfindinglabel.setPosition(10, 600);
		visualiseLabel.setPosition(790, 945);
		visualiseToggle.setPosition(860, 920);
		topAlertMessage.setPosition(1050, 931);
		stopDijkstrasBtn.setPosition(1400,920);
		bottomAlertMessage.setPosition(700, 30);

		overlay.setVisible(false);
		cancelClearBtn.setVisible(false);
		clearAcceptBtn.setVisible(false);
		clearMessageBox.setVisible(false);
		topAlertMessage.setVisible(false);
		bottomAlertMessage.setVisible(false);
		pathfindinglabel.setVisible(false);
		directionalToggle.setVisible(false);
		directionalLabel.setVisible(false);

		pathfindinglabel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent changeEvent, Actor actor) {
				graph.runDijkstras();
			}
		});

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
				graph.setVisualise(visualiseToggle.isChecked());
			}
		});

		weightedToggle.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (weightedToggle.isChecked()) {
					graph = new WeightedGraph(sr, stage, skin);
				} else {
					graph.clear();
					graph = new Graph(sr, stage, skin);
				}
				graph.setVisualise(visualiseToggle.isChecked());
			}
		});

		stopDijkstrasBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				graph.setAutoRunDijkstra(false);
				graph.clearPath();
			}
		});

		saveBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				FileOperations.write(graph);
			}
		});

		loadBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				FileOperations.read(graph);
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

	void setBottomAlertMessage() {
		if (graph.getShortestPath() > 0) {
			bottomAlertMessage.setText("Shortest Path: " + graph.getShortestPath());
			bottomAlertMessage.setVisible(true);
		} else if (graph.getMSTWeight() > 0) {
			bottomAlertMessage.setText("MST Weight: " + graph.getMSTWeight());
			bottomAlertMessage.setVisible(true);
		} else {
			bottomAlertMessage.setVisible(false);
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(Colours.lightGrey.r, Colours.lightGrey.g, Colours.lightGrey.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glHint(GL20.GL_GENERATE_MIPMAP_HINT, GL20.GL_NICEST);
		graph.keyListeners(getNodeUnderMouse(), isGraphLocked);
		stopDijkstrasBtn.setVisible(graph.isAutoRunDijkstra());
		setBottomAlertMessage();
		graph.draw();
		stage.act();
		stage.draw();

	}




	@Override
	public void dispose() {
		sr.dispose();
		stage.dispose();
		skin.dispose();
	}
}
