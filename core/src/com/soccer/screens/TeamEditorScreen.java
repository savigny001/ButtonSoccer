package com.soccer.screens;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.soccer.Soccer;
import com.soccer.model.MainModel;
import com.soccer.pojo.Player;
import com.soccer.pojo.Team;
import com.user.ButtonTeam;
import com.user.KitType;
import com.user.TeamSaver;

public class TeamEditorScreen implements Screen {

    public ButtonTeam team;
    public MainModel mainModel;
    public Soccer app;
    public Stage stage;
    public Skin skin;
//    private TextButton newsBtn, messagesBtn, calendarBtn, statisticsBtn, trainingBtn, stadionBtn;
//    private TextButton teamsBtn, newMatchBtn, marketBtn;
//
    private Label userNameLb;
    private TextButton backBtn, okBtn, homeBtn, awayBtn, homeGkBtn, awayGkBtn, saveBtn;
    private TextField teamNameFd;
    private List list;


    public final static float VIRTUAL_SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public final static float VIRTUAL_SCREEN_WIDTH = Gdx.graphics.getWidth();

    public TeamEditorScreen(Soccer app) {
        this.app = app;
        this.stage = new Stage();
        mainModel = new MainModel();
    }

    @Override
    public void show() {

        this.stage = new Stage();

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

        final Table container = new Table();
        userNameLb = new Label(
                mainModel.getUser().getName() + ", ELO: " + mainModel.getUser().getEloPoint(), skin);
//
        backBtn = new TextButton("Go back", skin);
        okBtn = new TextButton("Ok", skin);
        saveBtn = new TextButton("Save", skin);

        homeBtn = new TextButton("Home kit", skin);
        awayBtn = new TextButton("Away kit", skin);
        homeGkBtn = new TextButton("Home goalkeeper kit", skin);
        awayGkBtn = new TextButton("Away goalkeeper kit", skin);

        teamNameFd = new TextField(team.getName(), skin);


        homeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.kitEditorScreen.setMainModel(mainModel);
                app.kitEditorScreen.setTeam(team);
                app.kitEditorScreen.setKitType(KitType.HOME);
                app.setScreen(app.kitEditorScreen);
            }
        });

        awayBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.kitEditorScreen.setMainModel(mainModel);
                app.kitEditorScreen.setTeam(team);
                app.kitEditorScreen.setKitType(KitType.AWAY);
                app.setScreen(app.kitEditorScreen);
            }
        });

        homeGkBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.kitEditorScreen.setMainModel(mainModel);
                app.kitEditorScreen.setTeam(team);
                app.kitEditorScreen.setKitType(KitType.HOME_GK);
                app.setScreen(app.kitEditorScreen);
            }
        });

        awayGkBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.kitEditorScreen.setMainModel(mainModel);
                app.kitEditorScreen.setTeam(team);
                app.kitEditorScreen.setKitType(KitType.AWAY_GK);
                app.setScreen(app.kitEditorScreen);
            }
        });



        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.teamChooserScreen);
            }
        });

        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TeamSaver.saveTeamName(team.getId(), teamNameFd.getText());
                team.setName(teamNameFd.getText());
                app.setScreen(app.teamChooserScreen);
            }
        });


        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.playerEditorScreen.setMainModel(mainModel);
                app.playerEditorScreen.setPlayer((Player)list.getSelected());
                app.playerEditorScreen.setTeam(team);
                app.setScreen(app.playerEditorScreen);

            }
        });


        container.setFillParent(true);
//        container.setDebug(true);
        container.defaults().fillX().pad(10);



        list = new List(skin);
        Array listItems = new Array();

        for (Player player : team.getPlayers()) {
            listItems.add(player);
        }

        list.setItems(listItems);

        ScrollPane scrollPane = new ScrollPane(list, skin);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);



        container.add(userNameLb);
        container.row();
        container.add(new Label("Team name:", skin));
        container.add(teamNameFd);
        container.row();
        container.add(homeBtn);
        container.add(homeGkBtn);
        container.row();
        container.add(awayBtn);
        container.add(awayGkBtn);
        container.row();
        container.add(scrollPane).colspan(2);
        container.row();
        container.add(okBtn);
        container.add(backBtn);
        container.add(saveBtn);

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
}
