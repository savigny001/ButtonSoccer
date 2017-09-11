package com.soccer.screens;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.soccer.Soccer;
import com.soccer.model.MainModel;
import com.user.ButtonKit;
import com.user.ButtonTeam;
import com.user.KitColor;
import com.user.KitType;
import com.user.TeamSaver;

public class KitEditorScreen implements Screen {

    public ButtonTeam team;
    public KitType kitType;
    public MainModel mainModel;
    public Soccer app;
    public Stage stage;
    public Skin skin;
//    private TextButton newsBtn, messagesBtn, calendarBtn, statisticsBtn, trainingBtn, stadionBtn;
//    private TextButton teamsBtn, newMatchBtn, marketBtn;
//
    private Label userNameLb;
    private TextButton backBtn, saveBtn;
    private List list;
    Slider sliderR, sliderG,sliderB;

    private ShapeRenderer shapeRenderer;
    private Array colors, colorList;
    private ButtonKit buttonKit;
    private Integer actualColorIndex;
    private Color actualColor;
    private String kitTypeText;


    public final static float VIRTUAL_SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public final static float VIRTUAL_SCREEN_WIDTH = Gdx.graphics.getWidth();

    public KitEditorScreen(Soccer app) {
        this.app = app;
        this.stage = new Stage();
        mainModel = new MainModel();



    }

    @Override
    public void show() {


        if (kitType == KitType.HOME)  buttonKit = team.getHomeF();
        if (kitType == KitType.HOME_GK) buttonKit = team.getHomeGK();
        if (kitType == KitType.AWAY) buttonKit = team.getAwayF();
        if (kitType == KitType.AWAY_GK) buttonKit = team.getAwayGK();

        if (kitType == KitType.HOME)  kitTypeText = "Home kit";
        if (kitType == KitType.HOME_GK) kitTypeText = "Home goalkeeper kit";
        if (kitType == KitType.AWAY) kitTypeText = "Away kit";
        if (kitType == KitType.AWAY_GK) kitTypeText = "Away goalkeeper kit";


        colors = new Array<Color>();

        for (KitColor kitColor : buttonKit.getKitColors()) {
            colors.add(new Color(kitColor.r /255f, kitColor.g /255f, kitColor.b /255f, 1));
        }

        colorList = new Array<String>();
        colorList.add("Outer ring");
        colorList.add("Middle ring");
        colorList.add("Inner ring");
        colorList.add("Inner circle");

        actualColorIndex = 0;
        actualColor = (Color) colors.get(actualColorIndex);


        this.stage = new Stage();
        shapeRenderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skins/default/uiskin.json"));
//        skin = new Skin(Gdx.files.internal("skins/glassy/glassy-ui.json"));
        createUI();
        stage.getCamera().position.set(VIRTUAL_SCREEN_WIDTH/2,VIRTUAL_SCREEN_HEIGHT/2,0);
        stage.getCamera().viewportWidth = VIRTUAL_SCREEN_WIDTH;
        stage.getCamera().viewportHeight = VIRTUAL_SCREEN_HEIGHT;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(125/255f, 162/255f, 119/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);


        app.batch.begin();
        app.batch.draw(app.background,0,0);
        app.batch.end();

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        shapeRenderer.setColor(actualColor);

        shapeRenderer.rect(VIRTUAL_SCREEN_WIDTH/2+50,VIRTUAL_SCREEN_HEIGHT/2-130,100,100);
        shapeRenderer.end();

        stage.draw();

    }

    public void update (float delta) {
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void createUI() {
//        VerticalGroup verticalGroup = new VerticalGroup().space(10).pad(100).fill();
//        verticalGroup.setBounds(0, 0, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);
//
        userNameLb = new Label(
                mainModel.getUser().getName() + ", ELO: " + mainModel.getUser().getEloPoint(), skin);
//
        backBtn = new TextButton("Go back", skin);
        saveBtn = new TextButton("Save", skin);

        sliderR = new Slider(0,255,1,false,skin);
        sliderG = new Slider(0,255,1,false,skin);
        sliderB = new Slider(0,255,1,false,skin);

        sliderR.setValue(actualColor.r *255f);
        sliderG.setValue(actualColor.g *255f);
        sliderB.setValue(actualColor.b *255f);


        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.teamEditorScreen);

            }
        });

        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                Json json = new Json();
                String kitJsonText = json.toJson(buttonKit);
                TeamSaver.saveKitData(buttonKit.getId(), kitJsonText);
                app.setScreen(app.teamEditorScreen);
            }
        });



        Table container = new Table();
        container.setFillParent(true);
//        container.setDebug(true);
        container.defaults().fillX().pad(10);


        list = new List(skin);
//        Array listItems = new Array();
//
//        for (Player player : team.getPlayers()) {
//            listItems.add(player);
//        }
//
        list.setItems(colorList);

        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                actualColorIndex = list.getSelectedIndex();
                actualColor = (Color) colors.get(actualColorIndex);
                sliderR.setValue(actualColor.r *255f);
                sliderG.setValue(actualColor.g *255f);
                sliderB.setValue(actualColor.b *255f);
            }
        });

        sliderR.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                actualColor.r = sliderR.getValue()/255f;
                buttonKit.getKitColors()[actualColorIndex].r = Math.round(sliderR.getValue());
            }
        });

        sliderG.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                actualColor.g = sliderG.getValue()/255f;
                buttonKit.getKitColors()[actualColorIndex].g = Math.round(sliderG.getValue());
            }
        });

        sliderB.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                actualColor.b = sliderB.getValue()/255f;
                buttonKit.getKitColors()[actualColorIndex].b = Math.round(sliderB.getValue());
            }
        });

        ScrollPane scrollPane = new ScrollPane(list, skin);

        userNameLb.setColor(30,50,10,1);

        container.add(userNameLb);
        container.row();
        container.add(new Label("Team name:", skin));
        container.add(new Label(team.getName(), skin));
        container.row();
        container.add(new Label(kitTypeText, skin));
        container.row();
        container.add(scrollPane);
        container.row();
        container.add(sliderR);
        container.row();
        container.add(sliderG);
        container.row();
        container.add(sliderB);
        container.row();

        container.add(saveBtn);
        container.add(backBtn);

        stage.addActor(container);
    }

    public MainModel getMainModel() {
        return mainModel;
    }

    public void setMainModel(MainModel mainModel) {
        this.mainModel = mainModel;
    }

    public ButtonTeam getTeam() {
        return team;
    }

    public void setTeam(ButtonTeam team) {
        this.team = team;
    }

    public KitType getKitType() {
        return kitType;
    }

    public void setKitType(KitType kitType) {
        this.kitType = kitType;
    }
}
