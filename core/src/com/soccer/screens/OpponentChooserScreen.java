package com.soccer.screens;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.soccer.Soccer;
import com.soccer.model.MainModel;
import com.user.ButtonTeam;
import com.user.DateHandler;
import com.user.MatchRequest;
import com.user.MessageCenter;
import com.user.User;

import java.util.Date;

public class OpponentChooserScreen implements Screen {

    public MainModel mainModel;
    public Soccer app;
    public Stage stage;
    public Skin skin;
//    private TextButton newsBtn, messagesBtn, calendarBtn, statisticsBtn, trainingBtn, stadionBtn;
//    private TextButton teamsBtn, newMatchBtn, marketBtn;
//
    private Label userNameLb;
    private TextButton backBtn, okBtn;
    private List list;


    public final static float VIRTUAL_SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public final static float VIRTUAL_SCREEN_WIDTH = Gdx.graphics.getWidth();

    public OpponentChooserScreen(Soccer app) {
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
        userNameLb = new Label(
                mainModel.getUser().getName() + ", ELO: " + mainModel.getUser().getEloPoint(), skin);
//
        backBtn = new TextButton("Go back", skin);
        okBtn = new TextButton("Ok", skin);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.mainScreen);
            }
        });

        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                Date date = new Date();
                date.setTime(date.getTime()+180000);
                String dateString = DateHandler.convertDateToStringForSQL(date);
                User chosenUser = (User)list.getSelected();

                MatchRequest matchRequest =
                        new MatchRequest(mainModel.getUser().getId(), chosenUser.getId(),
                                mainModel.getUser().getId(), dateString);
                MessageCenter.sendMatchRequest(matchRequest);


            }
        });



        Table container = new Table();
        container.setFillParent(true);
//        container.setDebug(true);
        container.defaults().fillX().pad(10);


        list = new List(skin);



        Array listItems = new Array();

        for (User actUser : mainModel.getUsers()) {

            if (mainModel.getUser().getId() != actUser.getId()) listItems.add(actUser);

        }

        list.setItems(listItems);

//        list.addListener(new ChangeListener() {
//             @Override
//             public void changed(ChangeEvent event, Actor actor) {
//                 System.out.println(list.getSelected());
//
//             }
//         });

        ScrollPane scrollPane = new ScrollPane(list, skin);

        container.add(userNameLb);
        container.row();
        container.add(new Label("Choose opponent", skin));
        container.row();
        container.add(scrollPane);
        container.row();
        container.add(okBtn);
        container.row();
        container.add(backBtn);


        stage.addActor(container);
    }

    public MainModel getMainModel() {
        return mainModel;
    }

    public void setMainModel(MainModel mainModel) {
        this.mainModel = mainModel;
    }
}
