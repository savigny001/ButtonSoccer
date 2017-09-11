package com.soccer.screens;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.server.LoginClient;
import com.soccer.Soccer;
import com.soccer.model.MainModel;
import com.user.User;
import com.user.UserLoader;

import java.util.List;

public class LoginScreen implements Screen {

    public Soccer app;
    private LoginClient loginClient;
    public Stage stage;
    public Skin skin;
    private TextButton loginBtn;
    private Label usernameLb, passwordLb;
    private TextField usernameFd, passwordFd;

    public final static float VIRTUAL_SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public final static float VIRTUAL_SCREEN_WIDTH = Gdx.graphics.getWidth();

    public LoginScreen(Soccer app) {
        this.app = app;
        this.stage = new Stage();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skins/default/uiskin.json"));
//        skin = new Skin(Gdx.files.internal("skins/clean-crispy/clean-crispy-ui.json"));
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
        VerticalGroup verticalGroup = new VerticalGroup().space(3).pad(5).fill();
        verticalGroup.setBounds(0, 0, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);

        usernameLb = new Label("Username", skin);
        passwordLb = new Label("Password", skin);
        usernameFd = new TextField("Zoli", skin);
        passwordFd = new TextField("123456", skin);

        loginBtn = new TextButton("Login", skin);

        verticalGroup.addActor(usernameLb);
        verticalGroup.addActor(usernameFd);
        verticalGroup.addActor(passwordLb);
        verticalGroup.addActor(passwordFd);
        verticalGroup.addActor(loginBtn);

        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ((usernameFd.getText().length()>0) &&
                    (passwordFd.getText().length()>0)) {

//                    Integer userId =
//                        PasswordCheck.isCorrectPassword(usernameFd.getText(), passwordFd.getText());

                    loginClient = new LoginClient(10000, 10000);
                    loginClient.setLoginScreen(app.loginScreen);
//                    loginClient.connect();
                    loginClient.sendLoginRequest(usernameFd.getText(), passwordFd.getText());
                }
            }
        });

        stage.addActor(verticalGroup);
    }

    public void sendLoginResult(Integer userId) {
        System.out.println("userID:" + userId);

        if (userId != -1) {
            createMainModel(userId);
        }
    }

    private void createMainModel(Integer userId) {
        loginClient.sendUserLoaderRequest(userId);
    }

    public void sendUserResult(User user, List<User> users) {
        MainModel mainModel = new MainModel();
        mainModel.setUsers(users);
        mainModel.setUser(user);
        app.mainScreen.setMainModel(mainModel);

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {

                app.setScreen(app.mainScreen);
            }
        });


    }


}
