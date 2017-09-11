package com.soccer.screens;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.soccer.Soccer;
import com.soccer.model.MainModel;

public class MainScreen implements Screen {

    public MainModel mainModel;
    public Soccer app;
    public Stage stage;
    public Skin skin;
    private TextButton newsBtn, messagesBtn, calendarBtn, statisticsBtn, trainingBtn, stadionBtn;
    private TextButton teamsBtn, newMatchBtn, marketBtn, logOutBtn;

    private Label userNameLb;


    public final static float VIRTUAL_SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public final static float VIRTUAL_SCREEN_WIDTH = Gdx.graphics.getWidth();

    public MainScreen(Soccer app) {
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
        VerticalGroup verticalGroup = new VerticalGroup().space(10).pad(100).fill();
        verticalGroup.setBounds(0, 0, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);

        userNameLb = new Label(
                mainModel.getUser().getName() + ", ELO: " + mainModel.getUser().getEloPoint(), skin);

        newsBtn = new TextButton("News", skin);
        messagesBtn = new TextButton("Messages", skin);
        calendarBtn = new TextButton("Calendar", skin);
        statisticsBtn = new TextButton("Statistics", skin);
        stadionBtn = new TextButton("Stadium", skin);
        teamsBtn = new TextButton("Teams", skin);
        newMatchBtn = new TextButton("New match", skin);
        trainingBtn = new TextButton("Training", skin);
        marketBtn = new TextButton("Market", skin);
        logOutBtn = new TextButton("Log Out", skin);

        teamsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.teamChooserScreen.setMainModel(mainModel);
                app.teamChooserScreen.setNextScreenName("editor");
                app.setScreen(app.teamChooserScreen);

            }
        });

        messagesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.messageScreen.setMainModel(mainModel);
                app.setScreen(app.messageScreen);

            }
        });

        calendarBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.calendarScreen.setMainModel(mainModel);
                app.setScreen(app.calendarScreen);

            }
        });

        newMatchBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.opponentChooserScreen.setMainModel(mainModel);
                app.setScreen(app.opponentChooserScreen);

            }
        });

        logOutBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.teamChooserScreen.setMainModel(null);
                app.setScreen(app.loginScreen);

            }
        });

        marketBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.marketScreen.setMainModel(mainModel);
                app.setScreen(app.marketScreen);

            }
        });


        Table table = new Table();

        table.add(userNameLb).colspan(3).left();
        table.row();

        table.defaults().space(10).fillX();
        table.add(newsBtn);
        table.add(messagesBtn);
        table.add(calendarBtn);
        table.row();
        table.add(statisticsBtn);
        table.add(stadionBtn);
        table.add(teamsBtn);
        table.row();
        table.add(newMatchBtn);
        table.add(trainingBtn);
        table.add(marketBtn);
        table.row();
        table.add(logOutBtn);

        table.setFillParent(true);
        stage.addActor(table);
    }

    public MainModel getMainModel() {
        return mainModel;
    }

    public void setMainModel(MainModel mainModel) {
        this.mainModel = mainModel;
    }
}
