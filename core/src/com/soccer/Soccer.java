package com.soccer;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.soccer.screens.CalendarScreen;
import com.soccer.screens.KitEditorScreen;
import com.soccer.screens.LoginScreen;
import com.soccer.screens.MainScreen;
import com.soccer.screens.MarketScreen;
import com.soccer.screens.MessageScreen;
import com.soccer.screens.OpponentChooserScreen;
import com.soccer.screens.PlayerEditorScreen;
import com.soccer.screens.TeamChooserScreen;
import com.soccer.screens.TeamEditorScreen;

public class Soccer extends Game {

    public OrthographicCamera camera;
    public SpriteBatch batch;
    public LoginScreen loginScreen;
    public MainScreen mainScreen;

    public TeamChooserScreen teamChooserScreen;
    public TeamEditorScreen teamEditorScreen;
    public PlayerEditorScreen playerEditorScreen;
    public KitEditorScreen kitEditorScreen;
    public OpponentChooserScreen opponentChooserScreen;
    public MessageScreen messageScreen;
    public CalendarScreen calendarScreen;
    public StadiumScreen stadiumScreen;
    public MarketScreen marketScreen;

    public Texture background;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 420);
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("g22.jpg"));


        loginScreen = new LoginScreen(this);
        mainScreen = new MainScreen(this);
        teamChooserScreen = new TeamChooserScreen(this);
        teamEditorScreen = new TeamEditorScreen(this);
        playerEditorScreen = new PlayerEditorScreen(this);
        kitEditorScreen = new KitEditorScreen(this);
        opponentChooserScreen = new OpponentChooserScreen(this);
        messageScreen = new MessageScreen(this);
        calendarScreen = new CalendarScreen(this);
        marketScreen = new MarketScreen(this);
//        stadiumScreen = new StadiumScreen(this);

        this.setScreen(loginScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        loginScreen.dispose();
        mainScreen.dispose();
        teamChooserScreen.dispose();
        teamEditorScreen.dispose();
        playerEditorScreen.dispose();
        kitEditorScreen.dispose();
        opponentChooserScreen.dispose();
        messageScreen.dispose();
        calendarScreen.dispose();
        marketScreen.dispose();
//        stadiumScreen.dispose();
    }
}
