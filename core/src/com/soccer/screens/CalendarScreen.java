package com.soccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.server.LoginClient;
import com.soccer.Soccer;
import com.soccer.StadiumScreen;
import com.soccer.model.MainModel;
import com.user.BookedMatch;
import com.user.ButtonTeam;
import com.user.MatchRequest;
import com.user.MessageCenter;
import com.user.MessageType;
import com.user.UserRole;

import java.util.ArrayList;

public class CalendarScreen implements Screen {

    public MainModel mainModel;
    public java.util.List<BookedMatch> bookedMatches;
    public Soccer app;
    public Stage stage;
    public Skin skin;
    Array listItems;
//    private TextButton newsBtn, messagesBtn, calendarBtn, statisticsBtn, trainingBtn, stadionBtn;
//    private TextButton teamsBtn, newMatchBtn, marketBtn;
//
    private Label userNameLb;
    private TextButton backBtn, okBtn;
    private List list;


    public final static float VIRTUAL_SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public final static float VIRTUAL_SCREEN_WIDTH = Gdx.graphics.getWidth();

    public CalendarScreen(Soccer app) {
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
        okBtn = new TextButton("Go to the stadium", skin);

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.mainScreen);
            }
        });

        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BookedMatch match = (BookedMatch) list.getSelected();
                mainModel.setMatch(match);

                String match_id = match.getTeamAId() + "_" + match.getTeamBId() + "_" + match.getDateString();
                mainModel.setMatch_id(match_id);

                app.stadiumScreen = new StadiumScreen(app);


                if (match.getTeamAId().equals(mainModel.getUser().getId())) {
                    app.stadiumScreen.setUserA(mainModel.getUser());
                    app.stadiumScreen.setUserRole(UserRole.USER_A);
                    app.teamChooserScreen.setMainModel(mainModel);
                    app.teamChooserScreen.setNextScreenName("stadium");
                    app.setScreen(app.teamChooserScreen);
                } else if (match.getTeamBId().equals(mainModel.getUser().getId())) {
                    app.stadiumScreen.setUserRole(UserRole.USER_B);
                    app.stadiumScreen.setUserB(mainModel.getUser());
                    app.teamChooserScreen.setMainModel(mainModel);
                    app.teamChooserScreen.setNextScreenName("stadium");
                    app.setScreen(app.teamChooserScreen);
                } else {
                    app.stadiumScreen.setUserRole(UserRole.SPECTATOR);
                    app.stadiumScreen.setMainModel(mainModel);
                    app.stadiumScreen.getSpectators().add(mainModel.getUser());
                    app.setScreen(app.stadiumScreen);
                }


            }
        });



        Table container = new Table();
        container.setFillParent(true);
//        container.setDebug(true);
        container.defaults().fillX().pad(10);


        list = new List(skin);



        listItems = new Array();

//        java.util.List<BookedMatch> bookedMatches = MessageCenter.getBookedMatches(mainModel.getUser());


        bookedMatches = new ArrayList<BookedMatch>();

        LoginClient loginClient = new LoginClient(10000, 10000);
        loginClient.setCalendarScreen(app.calendarScreen);
        loginClient.sendMessageRequest(mainModel.getUser().getId(), MessageType.ALL_BOOKED_MATCHES);

        //innen

        list.setItems(listItems);

        if (bookedMatches.size() == 0) {
             okBtn.setTouchable(Touchable.disabled);
        }

        ScrollPane scrollPane = new ScrollPane(list, skin);

        container.add(userNameLb);
        container.row();
        container.add(new Label("Choose a team", skin));
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

    public void sendAllBookedMatches(java.util.List<BookedMatch> bookedMatches) {
        System.out.println("REFRESH CALENDAR");

        for (BookedMatch bookedMatch : bookedMatches) {
            String matchText = mainModel.getUserById(bookedMatch.getTeamAId())
                    + " - " + mainModel.getUserById(bookedMatch.getTeamBId())
                    + " match at the stadium of " + mainModel.getUserById(bookedMatch.getWhosStadionId()) + " ("
                    + bookedMatch.getDateString().substring(0,16) + ")";
            bookedMatch.setMatchText(matchText);
            listItems.add(bookedMatch);
        }

        list.setItems(listItems);

        if (bookedMatches.size() == 0) {
            okBtn.setTouchable(Touchable.disabled);
        } else okBtn.setTouchable(Touchable.enabled);

    }

}
