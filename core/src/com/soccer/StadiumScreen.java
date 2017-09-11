package com.soccer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.server.ConnectedUsers;
import com.server.LoginClient;
import com.server.SocClient;
import com.soccer.aiming.ClientAiming;
import com.soccer.aiming.ClientMoving;
import com.soccer.aiming.ClientPlaceing;
import com.soccer.model.MainModel;
import com.soccer.pojo.TeamSide;
import com.soccer.socevents.*;
import com.soccer.states.Action;
import com.soccer.states.ClientState;
import com.soccer.states.OutOfPlay;
import com.soccer.states.StateType;
import com.user.ButtonTeam;
import com.user.MatchReport;
import com.user.User;
import com.user.UserRole;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.PolySpatial;
import com.gushikustudios.rube.loader.RubeSceneLoader;
import com.gushikustudios.rube.loader.SimpleSpatial;
import com.gushikustudios.rube.loader.serializers.utils.RubeImage;
import com.soccer.aiming.Aiming;
import com.soccer.aiming.AimingType;
import com.soccer.controller.Controller;
import com.soccer.model.Model;
import com.soccer.pojo.*;
import com.soccer.situations.KickOff;
import com.soccer.states.AimingState;
import com.soccer.states.Placeing;
import com.soccer.states.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class StadiumScreen extends ApplicationAdapter implements Screen, InputProcessor, GestureDetector.GestureListener, Observer {

	private ConnectedUsers connectedUsers;
	private long startTime, currentTime, duration;
	private long addedTime, matchTime, moveStartTime, timeFromMoveStart;
	private Boolean isStateSentToClients = false;
	private SocClient client;
	private UserRole userRole;
	private User userA, userB;
    private UserRole  whosTurn = UserRole.USER_A;
	private List<User> spectators;
	public MainModel mainModel;
	private Screen nextScreen;
	private ButtonTeam buttonTeam;
	public Soccer app;
	public Stage stage;
	public Skin skin;

	private FileHandle logFile;

	private Label userNameLb, scoreLb, stadiumLb;
	private Label connectedUsersLb;
	private String connectedUsersStartText = "";
	private TextButton backBtn, giveUpBtn;
	private List list;


	public final static float VIRTUAL_SCREEN_HEIGHT = Gdx.graphics.getHeight();
	public final static float VIRTUAL_SCREEN_WIDTH = Gdx.graphics.getWidth();



	private Array<SimpleSpatial> mSpatials; // used for rendering rube images
	private Array<PolySpatial> mPolySpatials;
	private Map<String, Texture> mTextureMap;
	private Map<Texture, TextureRegion> mTextureRegionMap;

	private static final Vector2 mTmp = new Vector2(); // shared by all objects
	private static final Vector2 mTmp3 = new Vector2(); // shared during polygon creation
	private SpriteBatch mBatch;
	private PolygonSpriteBatch mPolyBatch;
	private AssetManager mAssetManager;


	Controller controller;
	Model model;
//	Event event;
	State state;
	Team teamA, teamB, lastTouchTeam;
	Player lastTouchPlayer;
	SpriteBatch batch;
	Sprite blueSprite, redSprite, ballSprite, shadowSprite, innerShadowSprite;
	Sprite placeableSprite, aimableSprite, movableSprite;
	Texture img, probeTexture;
	World world;
	Body body, ball;
	ArrayList<Body> players;
	Body bodyEdgeSouth, bodyEdgeEast, bodyEdgeWest, bodyEdgeNorth, rightNetBody, leftNetBody;
	Body southEdgeSensor, eastEdgeSensor, westEdgeSensor, northEdgeSensor;
	Body westGoalSensor, eastGoalSensor;

	Body groundBody;
	FixtureDef fixtureDef;
	CircleShape shape;
	BodyDef bodyDef;
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;
	OrthographicCamera camera;
	ShapeRenderer shapeRenderer;
	BitmapFont font;
	float torque = 0.0f;
	float initialScale = 1;
	boolean drawSprite = true;
	final float PIXELS_TO_METERS = FieldData.PIXELS_TO_METERS;
	final float AIR_FRICTION = 10f;
	String message="";
	MouseJoint mouseJoint = null;
	Aiming aiming;
	List<SocEvent> socEvents = new ArrayList<SocEvent>();

	Boolean hasStopped = true;

//	Integer leftGoal = 0, rightGoal = 0;
	Boolean ballIsMoving = false, playersAreMoving = false;

	Body hitBody = null;
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			// if the hit point is inside the fixture of the body
			// we report it

//			System.out.println(testPoint.x + " " + testPoint.y);
//			System.out.println(fixture.getBody().getPosition());

			if (fixture.testPoint(testPoint.x, testPoint.y)) {
//				System.out.println("HIT");
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};


	public StadiumScreen(Soccer app) {
		this.app = app;
		this.stage = new Stage();
		mainModel = new MainModel();
		userA = null;
		userB = null;
		spectators = new ArrayList<User>();
	}


	/**
	 * Creates an array of SimpleSpatial objects from RubeImages.
	 *
	 */
	private void createSpatialsFromRubeImages(RubeScene scene)
	{

		Array<RubeImage> images = scene.getImages();
		if ((images != null) && (images.size > 0))
		{
			mSpatials = new Array<SimpleSpatial>();
			for (int i = 0; i < images.size; i++)
			{
				RubeImage image = images.get(i);
				mTmp.set(image.width*FieldData.PIXELS_TO_METERS, image.height*FieldData.PIXELS_TO_METERS);
				String textureFileName = /*"data/" +*/ image.file;
				Texture texture = mTextureMap.get(textureFileName);
				if (texture == null)
				{
					texture = new Texture(textureFileName);
					mTextureMap.put(textureFileName, texture);
				}
				SimpleSpatial spatial = new SimpleSpatial(texture, image.flip, image.body, image.color, mTmp, image.center,
						image.angleInRads * MathUtils.radiansToDegrees);

				mSpatials.add(spatial);
			}
		}
	}

	/**
	 * Creates an array of PolySpatials based on fixture information from the scene. Note that
	 * fixtures create aligned textures.
	 *
	 * @param scene
	 */
	private void createPolySpatialsFromRubeFixtures(RubeScene scene)
	{
		Array<Body> bodies = scene.getBodies();

		EarClippingTriangulator ect = new EarClippingTriangulator();

		if ((bodies != null) && (bodies.size > 0))
		{
			mPolySpatials = new Array<PolySpatial>();
			Vector2 bodyPos = new Vector2();
			// for each body in the scene...
			for (int i = 0; i < bodies.size; i++)
			{
				Body body = bodies.get(i);
				bodyPos.set(body.getPosition());

				float bodyAngle = body.getAngle()*MathUtils.radiansToDegrees;

				Array<Fixture> fixtures = body.getFixtureList();

				if ((fixtures != null) && (fixtures.size > 0))
				{
					// for each fixture on the body...
					for (int j = 0; j < fixtures.size; j++)
					{
						Fixture fixture = fixtures.get(j);

						String textureName = (String)scene.getCustom(fixture, "TextureMask", null);
						if (textureName != null)
						{
							String textureFileName = /*"data/" + */ textureName;
							Texture texture = mTextureMap.get(textureFileName);
							TextureRegion textureRegion = null;
							if (texture == null)
							{
								texture = new Texture(textureFileName);
								texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
								mTextureMap.put(textureFileName, texture);
								textureRegion = new TextureRegion(texture);
								mTextureRegionMap.put(texture, textureRegion);
							}
							else
							{
								textureRegion = mTextureRegionMap.get(texture);
							}

							// only handle polygons at this point -- no chain, edge, or circle fixtures.
							if (fixture.getType() == Shape.Type.Polygon)
							{
								PolygonShape shape = (PolygonShape) fixture.getShape();
								int vertexCount = shape.getVertexCount();
								float[] vertices = new float[vertexCount * 2];

								// static bodies are texture aligned and do not get drawn based off of the related body.
								if (body.getType() == BodyDef.BodyType.StaticBody)
								{
									for (int k = 0; k < vertexCount; k++)
									{

										shape.getVertex(k, mTmp);
										mTmp.rotate(bodyAngle);
										mTmp.add(bodyPos); // convert local coordinates to world coordinates to that textures are
										// aligned
										vertices[k * 2] = mTmp.x * PolySpatial.PIXELS_PER_METER;
										vertices[k * 2 + 1] = mTmp.y * PolySpatial.PIXELS_PER_METER;
									}

									short [] triangleIndices = ect.computeTriangles(vertices).toArray();
									PolygonRegion region = new PolygonRegion(textureRegion, vertices, triangleIndices);
									PolySpatial spatial = new PolySpatial(region, Color.WHITE);
									mPolySpatials.add(spatial);
								}
								else
								{
									// all other fixtures are aligned based on their associated body.
									for (int k = 0; k < vertexCount; k++)
									{
										shape.getVertex(k, mTmp);
										vertices[k * 2] = mTmp.x * PolySpatial.PIXELS_PER_METER;
										vertices[k * 2 + 1] = mTmp.y * PolySpatial.PIXELS_PER_METER;
									}
									short [] triangleIndices = ect.computeTriangles(vertices).toArray();
									PolygonRegion region = new PolygonRegion(textureRegion, vertices, triangleIndices);
									PolySpatial spatial = new PolySpatial(region, body, Color.WHITE);
									mPolySpatials.add(spatial);
								}
							}
							else if (fixture.getType() == Shape.Type.Circle)
							{
								CircleShape shape = (CircleShape)fixture.getShape();
								float radius = shape.getRadius();
								int vertexCount = (int)(12f * radius);
								float [] vertices = new float[vertexCount*2];
								if (body.getType() == BodyDef.BodyType.StaticBody)
								{
									mTmp3.set(shape.getPosition());
									for (int k = 0; k < vertexCount; k++)
									{
										// set the initial position
										mTmp.set(radius,0);
										// rotate it by 1/vertexCount * k
										mTmp.rotate(360f*k/vertexCount);
										// add it to the position.
										mTmp.add(mTmp3);
										mTmp.rotate(bodyAngle);
										mTmp.add(bodyPos); // convert local coordinates to world coordinates so that textures are aligned
										vertices[k*2] = mTmp.x*PolySpatial.PIXELS_PER_METER;
										vertices[k*2+1] = mTmp.y*PolySpatial.PIXELS_PER_METER;
									}
									short [] triangleIndices = ect.computeTriangles(vertices).toArray();
									PolygonRegion region = new PolygonRegion(textureRegion, vertices, triangleIndices);
									PolySpatial spatial = new PolySpatial(region, Color.WHITE);
									mPolySpatials.add(spatial);
								}
								else
								{
									mTmp3.set(shape.getPosition());
									for (int k = 0; k < vertexCount; k++)
									{
										// set the initial position
										mTmp.set(radius,0);
										// rotate it by 1/vertexCount * k
										mTmp.rotate(360f*k/vertexCount);
										// add it to the position.
										mTmp.add(mTmp3);
										vertices[k*2] = mTmp.x*PolySpatial.PIXELS_PER_METER;
										vertices[k*2+1] = mTmp.y*PolySpatial.PIXELS_PER_METER;
									}
									short [] triangleIndices = ect.computeTriangles(vertices).toArray();
									PolygonRegion region = new PolygonRegion(textureRegion, vertices, triangleIndices);
									PolySpatial spatial = new PolySpatial(region, body, Color.WHITE);
									mPolySpatials.add(spatial);
								}
							}
						}
					}
				}
			}
		}
	}



	public void loadTeams() {
		players = new ArrayList<Body>();
//		generateTeams();
//		generateTeamsKits();
	}

	public void loadTeamA () {
		generateTeamA();
		generateTeamAKits();
	}

	public void loadTeamB () {
		generateTeamB();
		generateTeamBKits();
	}

	public void loadOtherTeam(Integer teamId) {
		LoginClient loginClient = new LoginClient(10000, 10000);
		loginClient.setStadiumScreen(app.stadiumScreen);
		Gdx.app.log("StadiumScreen - LOGINCLIENT", "sendTeamLoaderRequest");
		loginClient.sendTeamLoaderRequest(teamId);
	}

	public void sendTeam(ButtonTeam buttonTeam) {
//		System.out.println(team.getName() + team.getPlayers().get(0).getFirstName());
		Gdx.app.log("StadiumScreen - LOGINCLIENT", "received TEAM after teamloaderRequest");
		final ButtonTeam buttonTeam1 = buttonTeam;

		if (connectedUsers.teamAId != null)
			if (buttonTeam.getId().equals(connectedUsers.teamAId)) {
				Gdx.app.log("StadiumScreen - LOGINCLIENT", "received TEAM_A");
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						// process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
						generateTeam(UserRole.USER_A, buttonTeam1);
						bothTeamsLoadedExam();
					}
				});
			}

		if (connectedUsers.teamBId != null)
			if (buttonTeam.getId().equals(connectedUsers.teamBId)){
				Gdx.app.log("StadiumScreen - LOGINCLIENT", "received TEAM_B");
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						// process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
						generateTeam(UserRole.USER_B, buttonTeam1);
						bothTeamsLoadedExam();
					}
				});
			}

	}

	public void generateTeam(UserRole teamUserRole, ButtonTeam buttonTeam) {
		Position[] positions = null;
		if (teamUserRole == UserRole.USER_A) {
			positions = new Position[]{
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 10, 0f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 450, 0f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 450, -150f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 450, 150f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 800, 0f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 800, -600f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 800, 600f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 1200, -700f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 1200, -200f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 1200, 200f),
					new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 1200, 700f)};
		}

		if (teamUserRole == UserRole.USER_B) {
			 positions = new Position[] {
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 10, 0f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 450, 0f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 450, -150f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 450, 150f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 800, 0f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 800, -600f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 800, 600f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 1200, -700f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 1200, -200f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 1200, 200f),
					new Position(FieldData.TOUCHLINE_LENGTH / 2 - 1200, 700f)};
		}

		Team myTeam = new Team();
		myTeam.setName(buttonTeam.getName());

		for (int i=0; i<11; i++) {
//			myTeam.getPlayers().add(new Player(buttonTeam.getPlayers().get(i).getFirstName(),
//					new Position(buttonTeam.getPlayers().get(i).position.x, buttonTeam.getPlayers().get(i).position.y),
//					buttonTeam.getPlayers().get(i).getPost()));

			myTeam.getPlayers().add(new Player(buttonTeam.getPlayers().get(i).getFirstName(),
					positions[i], buttonTeam.getPlayers().get(i).getPost()));

			myTeam.getPlayers().get(i).setId(buttonTeam.getPlayers().get(i).getId());
		}

		for (Player player : myTeam.getPlayers()) {
			player.setPhotoPath("C:/playerphotos/" + player.getId() + ".jpg");
			System.out.println(player.getPhotoPath());
		}


		Kit kit = new Kit();
		List<Color> colors = new ArrayList<Color>();

		for (int i=0; i < 4; i++) {
			colors.add(new Color(
					buttonTeam.getHomeF().getKitColors()[i].r /255f,
					buttonTeam.getHomeF().getKitColors()[i].g /255f,
					buttonTeam.getHomeF().getKitColors()[i].b /255f, 1));
		}

		kit.setColors(colors);
		myTeam.setHomeKit(kit);
		myTeam.setAwayKit(kit);

		kit = new Kit();
		colors = new ArrayList<Color>();
		for (int i=0; i < 4; i++) {
			colors.add(new Color(
					buttonTeam.getHomeGK().getKitColors()[i].r /255f,
					buttonTeam.getHomeGK().getKitColors()[i].g /255f,
					buttonTeam.getHomeGK().getKitColors()[i].b /255f, 1));
		}

		kit.setColors(colors);
		myTeam.setHomeGoalkeeperKit(kit);
		myTeam.setAwayGoalkeeperKit(kit);

		shape = new CircleShape();
		shape.setRadius(FieldData.PLAYER_RADIUS/ PIXELS_TO_METERS);

		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.1f;
		fixtureDef.restitution = 0.90f;

		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		for (Player player : myTeam.getPlayers()) {
			bodyDef.position.set(player.position.x/PIXELS_TO_METERS, player.position.y/PIXELS_TO_METERS);
			body = world.createBody(bodyDef);
			body.createFixture(fixtureDef);
			body.setUserData(player);
			player.setBody(body);
			player.setTeam(myTeam);
			players.add(body);
		}

		if (teamUserRole == UserRole.USER_A) {
			Gdx.app.log("StadiumScreen", "generate team for User_A");
			teamA = myTeam;
			teamA.setTeamSide(TeamSide.LEFT);
			Gdx.app.log("StadiumScreen", "generate kits for User_A");
			generateTeamAKits();
		} else {
			Gdx.app.log("StadiumScreen", "generate team for User_B");
			teamB = myTeam;
			teamB.setTeamSide(TeamSide.RIGHT);
			Gdx.app.log("StadiumScreen", "generate kits for User_B");
			generateTeamBKits();

		}
	}


	public void generateTeamA() {
		teamA = new Team();
		teamA.setTeamSide(TeamSide.LEFT);
		teamA.setName("FTC");
		teamA.getPlayers().add(new Player("Hajdu", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 10, 0f), Post.GOALKEEPER));
		teamA.getPlayers().add(new Player("Telek", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 450, 0f)));
		teamA.getPlayers().add(new Player("Simon", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 450, -150f)));
		teamA.getPlayers().add(new Player("Nyilas", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 450, 150f)));
		teamA.getPlayers().add(new Player("Lisztes", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 800, 0f)));
		teamA.getPlayers().add(new Player("Kuznyecov", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 800, -600f)));
		teamA.getPlayers().add(new Player("Zavadszky", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 800, 600f)));
		teamA.getPlayers().add(new Player("Albert", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 1200, -700f)));
		teamA.getPlayers().add(new Player("Vincze", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 1200, -200f)));
		teamA.getPlayers().add(new Player("Kopunovics", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 1200, 200f)));
		teamA.getPlayers().add(new Player("Kuntics", new Position(-FieldData.TOUCHLINE_LENGTH / 2 + 1200, 700f)));

		for (Player player : teamA.getPlayers()) {
			player.setPhotoPath("playerphotos/" + player.getFirstName() + ".jpg");
		}

		Kit kit = new Kit();
		List<Color> colors = new ArrayList<Color>();
		colors.add(new Color(250f / 255, 250f / 255, 250f / 255, 1));
		colors.add(new Color(250f / 255, 250f / 255, 250f / 255, 1));
		colors.add(new Color(38f / 255, 120f / 255, 31f / 255, 1));
		colors.add(new Color(38f / 255, 120f / 255, 31f / 255, 1));
		kit.setColors(colors);
		teamA.setHomeKit(kit);

		kit = new Kit();
		colors = new ArrayList<Color>();
		colors.add(new Color(0, 0, 0, 1));
		colors.add(new Color(0, 0, 0, 1));
		colors.add(new Color(0, 0, 0, 1));
		colors.add(new Color(250f / 255, 250f / 255, 250f / 255, 1));
		kit.setColors(colors);
		teamA.setHomeGoalkeeperKit(kit);

		shape = new CircleShape();
		shape.setRadius(FieldData.PLAYER_RADIUS/ PIXELS_TO_METERS);

		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
//		fixtureDef.density = 2.4f;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.1f;
//		fixtureDef.restitution = 0.2f;
		fixtureDef.restitution = 0.90f;

		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		for (Player player : teamA.getPlayers()) {
			bodyDef.position.set(player.position.x/PIXELS_TO_METERS, player.position.y/PIXELS_TO_METERS);
			body = world.createBody(bodyDef);
			body.createFixture(fixtureDef);
			body.setUserData(player);
			player.setBody(body);
			player.setTeam(teamA);
			players.add(body);
		}
	}

	public void generateTeamB() {
		teamB = new Team();
		teamB.setTeamSide(TeamSide.RIGHT);
		teamB.setName("MTK");
		teamB.getPlayers().add(new Player("Babos",new Position(FieldData.TOUCHLINE_LENGTH/2-10,0f), Post.GOALKEEPER));
		teamB.getPlayers().add(new Player("Lorincz",new Position(FieldData.TOUCHLINE_LENGTH/2-450,0f)));
		teamB.getPlayers().add(new Player("Csertoi",new Position(FieldData.TOUCHLINE_LENGTH/2-450,-150f)));
		teamB.getPlayers().add(new Player("Kuttor",new Position(FieldData.TOUCHLINE_LENGTH/2-450,150f)));
		teamB.getPlayers().add(new Player("Halmai",new Position(FieldData.TOUCHLINE_LENGTH/2-800,0f)));
		teamB.getPlayers().add(new Player("Szekeres",new Position(FieldData.TOUCHLINE_LENGTH/2-800,-600f)));
		teamB.getPlayers().add(new Player("Kenesei",new Position(FieldData.TOUCHLINE_LENGTH/2-800,600f)));
		teamB.getPlayers().add(new Player("Zimmermann",new Position(FieldData.TOUCHLINE_LENGTH/2-1200,-700f)));
		teamB.getPlayers().add(new Player("Orosz",new Position(FieldData.TOUCHLINE_LENGTH/2-1200,-200f)));
		teamB.getPlayers().add(new Player("Hamar",new Position(FieldData.TOUCHLINE_LENGTH/2-1200,200f)));
		teamB.getPlayers().add(new Player("Illes",new Position(FieldData.TOUCHLINE_LENGTH/2-1200,700f)));

		for (Player player : teamB.getPlayers()) {
			player.setPhotoPath("playerphotos/" + player.getFirstName() + ".png");
		}


		Kit kit = new Kit();
		List<Color> colors = new ArrayList<Color>();
		colors.add(new Color(35f/255,77f/255,198f/255,1));
		colors.add(new Color(35f/255,77f/255,198f/255,1));
		colors.add(new Color(250f/255,250f/255,250f/255,1));

		colors.add(new Color(0,0,220f/255,1));
		kit.setColors(colors);
		teamB.setAwayKit(kit);

		kit = new Kit();
		colors = new ArrayList<Color>();
		colors.add(new Color(0,0,0,1));
		colors.add(new Color(0,0,0,1));
		colors.add(new Color(0,0,200f/255,1));
		colors.add(new Color(0,0,0,1));
		kit.setColors(colors);
		teamB.setAwayGoalkeeperKit(kit);

		shape = new CircleShape();
		shape.setRadius(FieldData.PLAYER_RADIUS/ PIXELS_TO_METERS);

		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
//		fixtureDef.density = 2.4f;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.1f;
//		fixtureDef.restitution = 0.2f;
		fixtureDef.restitution = 0.90f;

		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		for (Player player : teamB.getPlayers()) {
			bodyDef.position.set(player.position.x/PIXELS_TO_METERS, player.position.y/PIXELS_TO_METERS);
			body = world.createBody(bodyDef);
			body.createFixture(fixtureDef);
			body.setUserData(player);
			player.setBody(body);
			player.setTeam(teamB);
			players.add(body);
		}
	}

	public void generateTeamAKits() {
		teamA.setHomeKit(generateTeamKit(teamA, teamA.getHomeKit()));
		teamA.setHomeGoalkeeperKit(generateTeamKit(teamA, teamA.getHomeGoalkeeperKit()));


		for (Player player : teamA.getPlayers()) {
			if (player.getPost().equals(Post.OUTFIELD_PLAYER))
				player.setSprite(generatePlayerKit(player, teamA.getHomeKit()));
			else
				player.setSprite(generatePlayerKit(player, teamA.getHomeGoalkeeperKit()));
		}
	}

	public void generateTeamBKits() {
		teamB.setAwayKit(generateTeamKit(teamB, teamB.getAwayKit()));
		teamB.setAwayGoalkeeperKit(generateTeamKit(teamB, teamB.getAwayGoalkeeperKit()));

		for (Player player : teamB.getPlayers()) {
			if (player.getPhotoPath() !="") {
				if (player.getPost().equals(Post.OUTFIELD_PLAYER))
					player.setSprite(generatePlayerKit(player, teamB.getAwayKit()));
				else
					player.setSprite(generatePlayerKit(player, teamB.getAwayGoalkeeperKit()));

			} else
				player.setSprite(teamB.getAwayKit().getSprite());
		}
	}

	private Kit generateTeamKit(Team team, Kit kit) {
		List<Color> sampleColors = new ArrayList<Color>();
		sampleColors.add(new Color(0,0,0,1));

		Texture newTexture = new Texture("playerphotos/blank.png");
		newTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		TextureData newTextureData = newTexture.getTextureData();
		newTextureData.prepare();
		Pixmap newPixmap = newTexture.getTextureData().consumePixmap();

		Pixmap.setBlending(Pixmap.Blending.None);

		//one
		Texture texture = new Texture("playerphotos/one.png"); // one_4
//		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		TextureData textureData = texture.getTextureData();
		textureData.prepare();
		Pixmap pixmap = texture.getTextureData().consumePixmap();

		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				Color color = new Color();
				Color.rgba8888ToColor(color, pixmap.getPixel(x, y));
//				System.out.println(color.r + " " + color.g + " "  + color.b + " " + color.a);
				Integer i=0;
				for (Color sampleColor : sampleColors) {
					if ((color.r == sampleColor.r) && (color.g == sampleColor.g) &&
							(color.b == sampleColor.b)) {
						newPixmap.setColor(new Color(kit.getColors().get(i).r, kit.getColors().get(i).g,
								kit.getColors().get(i).b, (color.a == 0 ? 0 : color.a  )  ));
						newPixmap.fillRectangle(x, y, 1, 1);
					}
				}
			}
		}

		//two
		texture = new Texture(("playerphotos/two.png"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		textureData = texture.getTextureData();
		textureData.prepare();
		pixmap = texture.getTextureData().consumePixmap();

		Pixmap.setBlending(Pixmap.Blending.SourceOver);

		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				Color color = new Color();
				Color.rgba8888ToColor(color, pixmap.getPixel(x, y));
//				System.out.println(color.r + " " + color.g + " "  + color.b + " " + color.a);
				Integer i=1;
				for (Color sampleColor : sampleColors) {
					if ((color.r == sampleColor.r) && (color.g == sampleColor.g) &&
							(color.b == sampleColor.b)) {
						newPixmap.setColor(new Color(kit.getColors().get(i).r, kit.getColors().get(i).g,
								kit.getColors().get(i).b, color.a));
						newPixmap.fillRectangle(x, y, 1, 1);
					}

				}
			}
		}

		//three
		texture = new Texture(("playerphotos/three.png"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		textureData = texture.getTextureData();
		textureData.prepare();
		pixmap = texture.getTextureData().consumePixmap();

		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				Color color = new Color();
				Color.rgba8888ToColor(color, pixmap.getPixel(x, y));
//				System.out.println(color.r + " " + color.g + " "  + color.b + " " + color.a);
				Integer i=2;
				for (Color sampleColor : sampleColors) {
					if ((color.r == sampleColor.r) && (color.g == sampleColor.g) &&
							(color.b == sampleColor.b)) {
						newPixmap.setColor(new Color(kit.getColors().get(i).r, kit.getColors().get(i).g,
								kit.getColors().get(i).b, color.a));
						newPixmap.fillRectangle(x, y, 1, 1);
					}

				}
			}
		}


		//four
		texture = new Texture(("playerphotos/photosample.png"));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		textureData = texture.getTextureData();
		textureData.prepare();
		pixmap = texture.getTextureData().consumePixmap();

		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				Color color = new Color();
				Color.rgba8888ToColor(color, pixmap.getPixel(x, y));
//				System.out.println(color.r + " " + color.g + " "  + color.b + " " + color.a);
				Integer i=3;
				for (Color sampleColor : sampleColors) {
					if ((color.r == sampleColor.r) && (color.g == sampleColor.g) &&
							(color.b == sampleColor.b)) {
						newPixmap.setColor(new Color(kit.getColors().get(i).r, kit.getColors().get(i).g,
								kit.getColors().get(i).b, color.a));
						newPixmap.fillRectangle(x, y, 1, 1);
					}

				}
			}
		}


		kit.setPixmap(newPixmap);
		newTexture = new Texture(newPixmap);
		newTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		Sprite sprite = new Sprite(newTexture);
		kit.setSprite(sprite);

		return  kit;
	}

	private Sprite generatePlayerKit (Player player, Kit kit) {
		List<Color> sampleColors = new ArrayList<Color>();
		sampleColors.add(new Color(0f,0f,0f,1));

		Pixmap kitPixmap = kit.getPixmap();

		Texture texture = new Texture("playerphotos/photosample.png");
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		TextureData textureData = texture.getTextureData();
		textureData.prepare();
		Pixmap pixmap = texture.getTextureData().consumePixmap();

		Sprite sprite = null;

		try {
			Texture photoTexture = new Texture(player.getPhotoPath());


		photoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		TextureData photoTextureData = photoTexture.getTextureData();
		photoTextureData.prepare();
		Pixmap photoPixmap = photoTexture.getTextureData().consumePixmap();

		Texture newTexture = new Texture("playerphotos/blank.png");
		newTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		TextureData newTextureData = newTexture.getTextureData();
		newTextureData.prepare();
		Pixmap newPixmap = newTexture.getTextureData().consumePixmap();

		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				Color color = new Color();
				Color.rgba8888ToColor(color, pixmap.getPixel(x, y));

				Color newColor = new Color();
				Color.rgba8888ToColor(newColor, photoPixmap.getPixel(x, y));

				Color kitColor = new Color();
				Color.rgba8888ToColor(kitColor, kitPixmap.getPixel(x, y));

//				System.out.println(color.r + " " + color.g + " "  + color.b + " " + color.a);
				Integer i=0;
				for (Color sampleColor : sampleColors) {
					Pixmap.setBlending(Pixmap.Blending.None);
					newPixmap.setColor(kitColor.r, kitColor.g, kitColor.b, kitColor.a);
					newPixmap.fillRectangle(x, y, 1, 1);
					Pixmap.setBlending(Pixmap.Blending.SourceOver);
					if ((color.r == sampleColor.r) && (color.g == sampleColor.g) &&
							(color.b == sampleColor.b)) {
						newPixmap.setColor(newColor.r, newColor.g, newColor.b, color.a);
						newPixmap.fillRectangle(x, y, 1, 1);
					}

				}
			}
		}

		newTexture = new Texture(newPixmap);
		newTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		sprite = new Sprite(newTexture);

		} catch (GdxRuntimeException e) {
			Texture newTexture = new Texture(kitPixmap);
			newTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			sprite = new Sprite(newTexture);

		}

		return  sprite;
	}

	public void generateFieldElements() {
		Gdx.app.log("StadiumScreen", "generate field elements");

		//ball
		shape = new CircleShape();
		shape.setRadius(FieldData.BALL_RADIUS/ PIXELS_TO_METERS);
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.0f;
		fixtureDef.restitution = 0.95f;
		fixtureDef.friction = 0.1f;
		bodyDef.position.set(0,0);
		ball = world.createBody(bodyDef);
		ball.createFixture(fixtureDef);
		shape.dispose();

		//edge

		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		bodyDef2.position.set(0,0);

		FixtureDef fixtureDef2 = new FixtureDef();
//		fixtureDef2.restitution =1.1f;

		float w = FieldData.TABLE_WIDTH/PIXELS_TO_METERS;
		float h = FieldData.TABLE_HEIGHT/PIXELS_TO_METERS- 2/PIXELS_TO_METERS;
		final EdgeShape southEdgeShape = new EdgeShape();
		southEdgeShape.set(-w/2,-h/2,w/2,-h/2);
		fixtureDef2.shape = southEdgeShape;
		bodyEdgeSouth = world.createBody(bodyDef2);
		bodyEdgeSouth.createFixture(fixtureDef2);
		southEdgeShape.dispose();

		w = FieldData.TABLE_WIDTH/PIXELS_TO_METERS;
		h = FieldData.TABLE_HEIGHT/PIXELS_TO_METERS- 2/PIXELS_TO_METERS;
		final EdgeShape northEdgeShape = new EdgeShape();
		northEdgeShape.set(-w/2,h/2,w/2,h/2);
		fixtureDef2.shape = northEdgeShape;
		bodyEdgeNorth = world.createBody(bodyDef2);
		bodyEdgeNorth.createFixture(fixtureDef2);
		northEdgeShape.dispose();

		w = FieldData.TABLE_WIDTH/PIXELS_TO_METERS- 2/PIXELS_TO_METERS;
		h = FieldData.TABLE_HEIGHT/PIXELS_TO_METERS;
		EdgeShape eastEdgeShape = new EdgeShape();
		eastEdgeShape.set(-w/2,-h/2,-w/2,h/2);
		fixtureDef2.shape = eastEdgeShape;
		bodyEdgeEast = world.createBody(bodyDef2);
		bodyEdgeEast.createFixture(fixtureDef2);
		eastEdgeShape.dispose();

		w = FieldData.TABLE_WIDTH/PIXELS_TO_METERS- 2/PIXELS_TO_METERS;
		h = FieldData.TABLE_HEIGHT/PIXELS_TO_METERS;
		EdgeShape westEdgeShape = new EdgeShape();
		westEdgeShape.set(w/2,-h/2,w/2,h/2);
		fixtureDef2.shape = westEdgeShape;
		bodyEdgeWest = world.createBody(bodyDef2);
		bodyEdgeWest.createFixture(fixtureDef2);
		westEdgeShape.dispose();

		//edge sensor

		FixtureDef sensorFixtureDef = new FixtureDef();
//		sensorFixtureDef.isSensor = true;
		EdgeShape sensorEdgeShape = new EdgeShape();

		w = (FieldData.TOUCHLINE_LENGTH + FieldData.BALL_RADIUS*2*2)/PIXELS_TO_METERS;
		h = (FieldData.GOALLINE_LENGTH + FieldData.BALL_RADIUS*2*2)/PIXELS_TO_METERS;
		sensorEdgeShape.set(-w/2,-h/2,w/2,-h/2);
		sensorFixtureDef.shape = sensorEdgeShape;
		southEdgeSensor = world.createBody(bodyDef2);
		southEdgeSensor.createFixture(sensorFixtureDef);

		sensorEdgeShape.set(-w/2,h/2,w/2,h/2);
		sensorFixtureDef.shape = sensorEdgeShape;
		northEdgeSensor = world.createBody(bodyDef2);
		northEdgeSensor.createFixture(sensorFixtureDef);

		w = (FieldData.TOUCHLINE_LENGTH + FieldData.BALL_RADIUS*2*2)/PIXELS_TO_METERS;
		h = (FieldData.GOALLINE_LENGTH + FieldData.BALL_RADIUS*2*2)/PIXELS_TO_METERS;
		sensorEdgeShape.set(-w/2,h/2,-w/2,-h/2);
		sensorFixtureDef.shape = sensorEdgeShape;
		westEdgeSensor = world.createBody(bodyDef2);
		westEdgeSensor.createFixture(sensorFixtureDef);

		sensorEdgeShape.set(w/2,h/2,w/2,-h/2);
		sensorFixtureDef.shape = sensorEdgeShape;
		eastEdgeSensor = world.createBody(bodyDef2);
		eastEdgeSensor.createFixture(sensorFixtureDef);


		//goal sensors
		w = (FieldData.TOUCHLINE_LENGTH + FieldData.BALL_RADIUS*2*2)/PIXELS_TO_METERS;
		h = (FieldData.NET_HEIGHT)/PIXELS_TO_METERS;
		sensorEdgeShape.set(-w/2 + 2/PIXELS_TO_METERS,h/2,-w/2 + 2/PIXELS_TO_METERS,-h/2);
		sensorFixtureDef.shape = sensorEdgeShape;
		westGoalSensor = world.createBody(bodyDef2);
		westGoalSensor.createFixture(sensorFixtureDef);

		sensorEdgeShape.set(w/2 - 2/PIXELS_TO_METERS,h/2,w/2 - 2/PIXELS_TO_METERS,-h/2);
		sensorFixtureDef.shape = sensorEdgeShape;
		eastGoalSensor = world.createBody(bodyDef2);
		eastGoalSensor.createFixture(sensorFixtureDef);

		sensorEdgeShape.dispose();


	}


	@Override
	public void show() {

		setLogger();

		if (userRole == UserRole.SPECTATOR) {
			Gdx.app.log("StadiumScreen", "set spectator client");
			client = new SocClient(mainModel.getMatch_id(), mainModel.getUser().getId(),
					userRole, -1);
		} else
			Gdx.app.log("StadiumScreen", "set player client");
			client = new SocClient(mainModel.getMatch_id(), mainModel.getUser().getId(),
					userRole, buttonTeam.getId());
		client.setStadiumScreen(this);
		Gdx.app.log("StadiumScreen", "connect client");
		client.connect();
		create();

	}

	public void createUI() {
		Gdx.app.log("StadiumScreen", "createUI");
		this.stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("skins/default/uiskin.json"));
//		skin = new Skin(Gdx.files.internal("skins/glassy/glassy-ui.json"));
		stage.getCamera().position.set(VIRTUAL_SCREEN_WIDTH/2,VIRTUAL_SCREEN_HEIGHT/2,0);
		stage.getCamera().viewportWidth = VIRTUAL_SCREEN_WIDTH;
		stage.getCamera().viewportHeight = VIRTUAL_SCREEN_HEIGHT;


		Table container = new Table();
		container.setFillParent(true);
		container.align(Align.topLeft);
//        container.setDebug(true);
		container.defaults().fillX().pad(3);

		userNameLb = new Label(mainModel.getUser().getName(), skin);
		connectedUsersLb = new Label(connectedUsersStartText, skin);
		stadiumLb = new Label("Stadium of " + mainModel.getUserById(mainModel.getMatch().getWhosStadionId()), skin);

		if ((teamA != null) && (teamB != null))
			scoreLb = new Label("" + mainModel.getUserById(mainModel.getMatch().getTeamAId()) +
				" - " + mainModel.getUserById(mainModel.getMatch().getTeamBId()) +
				" " + model.getTeamA().getScore() + " : " + model.getTeamB().getScore() , skin);
		else
			scoreLb = new Label("" + mainModel.getUserById(mainModel.getMatch().getTeamAId()) +
					" - " + mainModel.getUserById(mainModel.getMatch().getTeamBId()) +
					" 0 : 0", skin);

		giveUpBtn = new TextButton("Give up", skin);

		giveUpBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
			MatchReport matchReport = null;
			if ((teamA != null)&&(teamB != null)) {
				matchReport = new MatchReport(mainModel.getMatch(), teamA.getScore(), teamB.getScore());
				System.out.println(teamA.getScore() + "-" + teamB.getScore());
			}


//			Dialog dialog = new Dialog("Bye", skin);
//			dialog.show(stage);

			app.mainScreen.setMainModel(mainModel);
			app.stadiumScreen.dispose();
			client.disconnect();
			app.setScreen(app.mainScreen);
			}
		});

		container.add(giveUpBtn);
		container.add(scoreLb);
		container.row();
		container.add(userNameLb);
		container.row();
		container.add(stadiumLb);
		container.row();
		container.add(connectedUsersLb);


		stage.addActor(container);
	}


	public void setLogger() {

		if (userRole == UserRole.USER_A) logFile = Gdx.files.local("logA.txt");
		if (userRole == UserRole.USER_B) logFile = Gdx.files.local("logB.txt");
		if (userRole == UserRole.SPECTATOR) logFile = Gdx.files.local("log.txt");
		logFile.delete();


		//logFile.writeString(getUserA().getName() + " " + getUserB().getName(), true);
		//logFile.writeString("\n",true);


		Gdx.app.setApplicationLogger(new ApplicationLogger() {
			@Override
			public void log(String tag, String message) {
				String outputLogText = "";
				if (tag.equals("Situation")) outputLogText = tag + ": " + message;
				else if (tag.equals("in Situation")) outputLogText = "     " + tag + ": " + message;
				else outputLogText = "                 " + tag + ": " + message;
				System.out.println(outputLogText);
				logFile.writeString(outputLogText + "\n", true);
			}

			@Override
			public void log(String tag, String message, Throwable exception) {

			}

			@Override
			public void error(String tag, String message) {

			}

			@Override
			public void error(String tag, String message, Throwable exception) {

			}

			@Override
			public void debug(String tag, String message) {

			}

			@Override
			public void debug(String tag, String message, Throwable exception) {

			}
		});

	}


	public void generateSpritesFromFiles() {
		Gdx.app.log("StadiumScreen", "generateSpritesFromFiles");

		img = new Texture("blue_player_big.png");
		redSprite = new Sprite(img);
		img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		redSprite.setPosition(-redSprite.getWidth()/2,-redSprite.getHeight()/2);

		img = new Texture("ball_3.png");
		img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		ballSprite = new Sprite(img);

		img = new Texture("inout_shadow_3.png");
		img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shadowSprite = new Sprite(img);

		img = new Texture("aimable4.png");
		img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		aimableSprite = new Sprite(img);

		img = new Texture("placeable3.png");
		img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		placeableSprite = new Sprite(img);

		img = new Texture("movable.png");
		img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		movableSprite = new Sprite(img);
	}

	public void initialization() {


	}

	private void addContactListenerToWorld() {

		Gdx.app.log("StadiumScreen", "addContactListenerToWorld");

		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {

				//amikor nincs játékban a labda, vagyis játékon kívül gurul, vagy nem
				//nyomódott még be a space, akkor hogy viselkedjenek az objektumok egymással

				//a helyezés közben hogy viselkedjenek egymással az objektumok
				if ((state instanceof Placeing)) {
					if ((contact.getFixtureA().getBody() != bodyEdgeNorth) &&
							(contact.getFixtureA().getBody() != bodyEdgeEast) &&
							(contact.getFixtureA().getBody() != bodyEdgeWest) &&
							(contact.getFixtureA().getBody() != bodyEdgeSouth))
						contact.setEnabled(false);
				}

				if ((contact.getFixtureA().getBody() == eastGoalSensor) ||
						(contact.getFixtureA().getBody() == westGoalSensor) ||
						(contact.getFixtureB().getBody() == eastGoalSensor) ||
						(contact.getFixtureB().getBody() == westGoalSensor)){
					contact.setEnabled(false);
				}


				if ((contact.getFixtureA().getBody() == eastGoalSensor) ||
						(contact.getFixtureA().getBody() == westGoalSensor) ||
						(contact.getFixtureB().getBody() == eastGoalSensor) ||
						(contact.getFixtureB().getBody() == westGoalSensor)){

					//BEMENT A LABDA A BAL KAPUBA
					if ((contact.getFixtureA().getBody() == westGoalSensor) &&
							(contact.getFixtureB().getBody() == ball)) {

						Gdx.app.log("StadiumScreen", "SENSOR - BALL_PASSED_GOAL left");

						socEvents.add(new BallPassedGoal(lastTouchPlayer, teamB));
						socEventChanged();

					}

					//BEMENT A LABDA A JOBB KAPUBA - gól a bal csapatnak
					if ((contact.getFixtureA().getBody() == eastGoalSensor) &&
							(contact.getFixtureB().getBody() == ball)) {

						Gdx.app.log("StadiumScreen", "SENSOR - BALL_PASSED_GOAL right");

						socEvents.add(new BallPassedGoal(lastTouchPlayer, teamA));
						socEventChanged();

					}

				}

				//a célzott játékos előbb ér e egy ellenfél játékoshoz, mint a labdához

				if (!SocEventList.isContains(socEvents, PlayerTouchedBall.class)) {

					Body aimedPlayerBody = aiming.getPlayerBody();


					if ((contact.getFixtureA().getBody() == aimedPlayerBody) ||
							(contact.getFixtureB().getBody() == aimedPlayerBody)) {
						Player aimedPlayer = (Player) aimedPlayerBody.getUserData();
						for (Body player : players) {
							if ((contact.getFixtureA().getBody() == player) ||
									(contact.getFixtureB().getBody() == player)) {

								Player touchedPlayer = (Player) player.getUserData();

								if (!aimedPlayer.getTeam().equals(touchedPlayer.getTeam())) {

									float contactX = contact.getWorldManifold().getPoints()[0].x;
									float contactY = contact.getWorldManifold().getPoints()[0].y;

									Gdx.app.log("StadiumScreen", "SENSOR - Player Touched Opponent Player");

									socEvents.add(new PlayerTouchedPlayer(aimedPlayer, contactX, contactY));
									socEventChanged();

								}

							}
						}

					}

				}

				//a labda hozzáér valamihez
				if ((contact.getFixtureA().getBody() == ball) || (contact.getFixtureB().getBody() == ball)) {

					//megnézzük, hogy játékoshoz ért-e hozzá
					//melyik játékos/csapat ért utoljára a labdához

					for (Body body : players) {
						//az adott jákoshoz ért-e hozzá
						if ((contact.getFixtureA().getBody() == body) ||
							(contact.getFixtureB().getBody() == body)) {

							lastTouchPlayer = (Player) body.getUserData();

							Gdx.app.log("StadiumScreen", "SENSOR - Player Touched Ball");

							socEvents.add(new PlayerTouchedBall(lastTouchPlayer));
							socEventChanged();

							message = lastTouchPlayer.getFirstName();
//								System.out.println(lastTouchPlayer.getFirstName());
							if (teamA.getPlayers().contains(lastTouchPlayer))
								lastTouchTeam = teamA;
							if (teamB.getPlayers().contains(lastTouchPlayer))
								lastTouchTeam = teamB;
//								System.out.println(lastTouchTeam.getName());
						}
					}

					//ha elhagyja a labda a pályát

					//a toucline-nál hagyja el a pályát
					if ((contact.getFixtureA().getBody() == southEdgeSensor) ||
							(contact.getFixtureA().getBody() == northEdgeSensor)) {

						contact.setEnabled(false);


						float contactX = contact.getWorldManifold().getPoints()[0].x;
						float contactY = contact.getWorldManifold().getPoints()[0].y;

						if (contact.getFixtureA().getBody() == southEdgeSensor)
							contactY = -(FieldData.GOALLINE_LENGTH / 2) / PIXELS_TO_METERS;
						if (contact.getFixtureA().getBody() == northEdgeSensor)
							contactY = (FieldData.GOALLINE_LENGTH / 2) / PIXELS_TO_METERS;
//							System.out.println("Y:" + contactY);

						Gdx.app.log("StadiumScreen", "SENSOR - Ball passed Touchline");
						socEvents.add(new BallPassedTouchLine(lastTouchPlayer, contactX, contactY));
						socEventChanged();

					}

					//a bal goalline-nál
					if ((contact.getFixtureA().getBody() == eastEdgeSensor) ||
							(contact.getFixtureB().getBody() == eastEdgeSensor)) {
						contact.setEnabled(false);
						Gdx.app.log("StadiumScreen", "SENSOR - Ball passed Goalline-right");
						socEvents.add(new BallPassedGoalLine(lastTouchPlayer, GoalLineSide.EAST));
						socEventChanged();
					}

					//a jobb goalline-nál hagyja el a pályát
					if ((contact.getFixtureA().getBody() == westEdgeSensor) ||
							(contact.getFixtureB().getBody() == westEdgeSensor)) {
						contact.setEnabled(false);
						Gdx.app.log("StadiumScreen", "SENSOR - Ball passed Goalline-left");
						socEvents.add(new BallPassedGoalLine(lastTouchPlayer, GoalLineSide.WEST));
						socEventChanged();
					}

				}

			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

				if (state instanceof Placeing) {
					if ((contact.getFixtureA().getBody() != bodyEdgeNorth) &&
							(contact.getFixtureA().getBody() != bodyEdgeEast) &&
							(contact.getFixtureA().getBody() != bodyEdgeWest) &&
							(contact.getFixtureA().getBody() != bodyEdgeSouth))
						contact.setEnabled(false);
				}

				if ((contact.getFixtureA().getBody() == eastGoalSensor) ||
						(contact.getFixtureA().getBody() == westGoalSensor) ||
						(contact.getFixtureB().getBody() == eastGoalSensor) ||
						(contact.getFixtureB().getBody() == westGoalSensor)){
					contact.setEnabled(false);
				}

				if ((contact.getFixtureA().getBody() == southEdgeSensor) ||
						(contact.getFixtureA().getBody() == northEdgeSensor) ||
						(contact.getFixtureA().getBody() == eastEdgeSensor) ||
						(contact.getFixtureA().getBody() == westEdgeSensor) ||

						(contact.getFixtureB().getBody() == southEdgeSensor) ||
						(contact.getFixtureB().getBody() == northEdgeSensor) ||
						(contact.getFixtureB().getBody() == eastEdgeSensor) ||
						(contact.getFixtureB().getBody() == westEdgeSensor)) {

					contact.setEnabled(false);
				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
		});
	}


	@Override
	public void create() {


		generateSpritesFromFiles();

		Gdx.app.log("StadiumScreen", "load net with RubeSceneLoader");
		RubeSceneLoader loader = new RubeSceneLoader();
		RubeScene scene = loader.loadScene(Gdx.files.internal("net_v13.json"));

		matchTime = 0;
		players = new ArrayList<Body>();
		aiming = new Aiming();
		aiming.setAimingInProgress(false);
//		aiming.setAimingType(AimingType.POKE_AIMING);
		aiming.setAimingType(AimingType.LINE_AIMING);

		batch = new SpriteBatch();

		mTextureMap = new HashMap<String, Texture>();
		mTextureRegionMap = new HashMap<Texture, TextureRegion>();
		mBatch = new SpriteBatch();
		mPolyBatch = new PolygonSpriteBatch();
		createSpatialsFromRubeImages(scene);

		world = scene.getWorld();

		bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		teamA = null;
		teamB = null;
//		loadTeams();

//		state = new Placeing(teamB, teamB.getPlayers());

//		if (teamA != null) {
//			lastTouchPlayer = teamA.getPlayers().get(1);
//			lastTouchTeam = teamA;
//		} else {
			lastTouchPlayer = null;
			lastTouchTeam = null;
//		}



		model = new Model(teamA, teamB, new Ball());
		model.addObserver(this);


		createUI();

		generateFieldElements();

		InputMultiplexer im = new InputMultiplexer();
		GestureDetector gd = new GestureDetector(this);

		im.addProcessor(stage);
		im.addProcessor(gd);
		im.addProcessor(this);
		Gdx.input.setInputProcessor(im);

		debugRenderer = new Box2DDebugRenderer();

		font = new BitmapFont();
		font.setColor(Color.BLACK);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(w, w * h / w);
		camera.zoom = 3f;

		shapeRenderer = new ShapeRenderer();

		addContactListenerToWorld();
	}

	public void bothTeamsLoadedExam() {
		if ((teamA != null) && (teamB != null)) {
			Gdx.app.log("StadiumScreen", "bothTeamsLoaded = true");
			model.setTeamA(teamA);
			model.setTeamB(teamB);
			controller = new Controller(teamA, teamB, new KickOff(model, teamA, teamB, socEvents));
			state = controller.getState(socEvents);
			controller.setModel(model); //ez lehet nem is kell majd
			lastTouchPlayer = teamA.getPlayers().get(1);
			lastTouchTeam = teamA;
		}
	}

	@Override
	public void render(float delta) {

		if (connectedUsers != null) {



			if (connectedUsers.connectedUserAId != null)
				if (teamA == null) {
//				loadTeamA();
				if (userRole == UserRole.USER_A) {
					Gdx.app.log("StadiumScreen", "generateTeam - own team - team_A");
					generateTeam(userRole, buttonTeam);

				}
//					else loadOtherTeam(connectedUsers.teamAId);
				bothTeamsLoadedExam();
			}
			if (connectedUsers.connectedUserBId != null) if (teamB == null) {
//				loadTeamB();
				if (userRole == UserRole.USER_B) {
					Gdx.app.log("StadiumScreen", "generateTeam - own team - team_B");
					generateTeam(userRole, buttonTeam);
				}
//					else loadOtherTeam(connectedUsers.teamAId);
				bothTeamsLoadedExam();
			}
		}




//		System.out.println(whosTurn.toString());
		Double time = (double)matchTime/1000000000;
		scoreLb.setText("Time: " + time);
//		scoreLb.setText("" + mainModel.getUserById(mainModel.getMatch().getTeamAId()) +
//				" - " + mainModel.getUserById(mainModel.getMatch().getTeamBId()) +
//				" " + model.getTeamA().getScore() + " : " + model.getTeamB().getScore());


		if ((teamA != null) && (teamB != null))
//		if (userRole == whosTurn) {
		if (userRole == UserRole.USER_A) {
			//megkapjuk az új állapotot
			Gdx.app.log("StadiumScreen", "get STATE from controller");
			if ((teamA != null) && (teamB != null)) state = controller.getState(socEvents);

			//beállítjuk a whosTurn értékét
			if (state instanceof AimingState) {
				AimingState actAiming = (AimingState) state;

				if (actAiming.getTeam().getTeamSide() == TeamSide.LEFT)
					whosTurn = UserRole.USER_A;
				else whosTurn = UserRole.USER_B;
			}
			if (state instanceof Placeing) {
				Placeing actPlaceing = (Placeing)state;
				if (actPlaceing.getTeam().getTeamSide() == TeamSide.LEFT)
					whosTurn = UserRole.USER_A;
				else whosTurn = UserRole.USER_B;
			}

			if (!model.everythingStopped()) isStateSentToClients = false;

			if ((model.everythingStopped()) && (!isStateSentToClients)) {
				isStateSentToClients = true;
				//átküldjük a többi kliensnek
				ClientState clientState = state.getClientState();
				Gdx.app.log("StadiumScreen - CLIENT", "sendState");
				client.sendState(clientState);

			}
		}

		for (Body body : players) {
			Vector2 vector = body.getLinearVelocity();

			vector.x -= vector.x/20;
			vector.y -= vector.y/20;
			body.setLinearVelocity(vector);
			body.setAngularVelocity(body.getAngularVelocity()-body.getAngularVelocity()/20);
		}

		Vector2 vector = ball.getLinearVelocity();

		vector.x -= vector.x/20; //40
		vector.y -= vector.y/20; //40

		ball.setLinearVelocity(vector);
		ball.setAngularVelocity(ball.getAngularVelocity()-ball.getAngularVelocity()/20); //10

		float ballXabs = Math.abs(ball.getLinearVelocity().x);
		float ballYabs = Math.abs(ball.getLinearVelocity().y);
		Vector2 ballVectorAbs = new Vector2(ballXabs, ballXabs);

		//a labda kicsit gyorsabban álljon meg
		if ((ballXabs<0.2f) && (ballYabs<0.2f)) {ball.setLinearVelocity(0, 0);}

		//a játékosok kicsit gyorsabban álljanak meg
		for (Body player : players) if (player.getLinearVelocity().len()<0.3f) {
			player.setLinearVelocity(0,0);
		}


//		if (userRole == UserRole.USER_A)
			if (ball.getLinearVelocity().equals(new Vector2(0,0))) {
				model.getBall().setMoving(false);

				if (ballIsMoving)  {
					ballIsMoving = false;
					socEvents.add(new BallStopped());
					socEventChanged();

				}
			} else {
				ballIsMoving = true;
				model.getBall().setMoving(true);
			}

		//megállt-e az összes játékos
		Boolean isAllPlayerStopped = true;
		for (Body body : players) {
			if (!body.getLinearVelocity().equals(new Vector2(0,0))) isAllPlayerStopped = false;
		}

//		if (userRole == UserRole.USER_A)
			if (isAllPlayerStopped) {
				model.setPlayersMoving(false);
				if (!hasStopped) {
					timeFromMoveStart = System.nanoTime() - moveStartTime;
					matchTime += timeFromMoveStart;
					System.out.println("MATCHTIME: " + matchTime/1000000000);
					System.out.println("Timefromstart: " + timeFromMoveStart/1000000000);
					socEvents.add(new PlayersStopped());
					socEventChanged();
				}
				hasStopped = true;
			} else {
				hasStopped = false;
				model.setPlayersMoving(true);
			}


		if (SocEventList.isContains(socEvents, PlayersStopped.class, BallStopped.class)) {
			Vector2 ballPosition = ball.getPosition();
			for (Player player : teamA.getPlayers()) {
				Vector2 playerPosition = player.getBody().getPosition();
				player.setDistanceFromBall(ballPosition.dst(playerPosition));
			}
			for (Player player : teamB.getPlayers()) {
				Vector2 playerPosition = player.getBody().getPosition();
				player.setDistanceFromBall(ballPosition.dst(playerPosition));
			}
		}

		// Step the physics simulation forward at a rate of 60hz
//		world.step(1f/100f, 15, 1); !
//		world.step(1f/100f, 40, 1); !!
//		world.step(1f/120f, 40, 4);
//		world.step(1f/100f, 40, 3); //???
//		world.step(1f/100f, 6, 2);
//		world.step(1f/60f, 6, 2);

//		if (userRole == whosTurn) {
		if (userRole == UserRole.USER_A) {
			world.step(1f/100f, 40, 4); //!!!

			currentTime = System.nanoTime();
			duration = currentTime - startTime;

			if (duration/1000000>50)
			if ((ballIsMoving) || (model.isPlayersMoving())) {

				startTime = System.nanoTime();

				ClientMoving clientMoving = new ClientMoving();
				for (Player player : teamA.getPlayers()) {

					Position position = new Position();
					position.x = player.getBody().getPosition().x * FieldData.PIXELS_TO_METERS;
					position.y = player.getBody().getPosition().y * FieldData.PIXELS_TO_METERS;
					clientMoving.teamApositions.add(position);
				}

				for (Player player : teamB.getPlayers()) {
					Position position = new Position();
					position.x = player.getBody().getPosition().x * FieldData.PIXELS_TO_METERS;
					position.y = player.getBody().getPosition().y * FieldData.PIXELS_TO_METERS;
					clientMoving.teamBPositions.add(position);
				}

				if (ball != null) {
					clientMoving.ballPosition.x = ball.getPosition().x;
					clientMoving.ballPosition.y = ball.getPosition().y;

				} else {
					clientMoving.ballPosition.x = 0f;
					clientMoving.ballPosition.y = 0f;
				}

				clientMoving.ballPosition.x = ball.getPosition().x;
				clientMoving.ballPosition.y = ball.getPosition().y;

				Gdx.app.log("StadiumScreen - CLIENT", "sendMoving");
				client.sendMoving(clientMoving);

			}

		}

//		if (userRole != whosTurn) {
		if (userRole == UserRole.USER_B) {
			if (state instanceof Placeing) {
				Gdx.app.log("StadiumScreen", "user_B placeing?????");
				Placeing placeing = (Placeing)state;
				if (placeing.getTeam().getTeamSide() == TeamSide.RIGHT) {
					world.step(1f/100f, 40, 4); //!!!
				}
			}
		}


		renderField();
		renderAimingLine();

		batch.setProjectionMatrix(camera.combined);
		batch.enableBlending();
		debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
				PIXELS_TO_METERS, 0);

		batch.begin();
		renderBall();
		renderPlayers();
		batch.end();
		renderRubeSpatials();

//		debugRenderer.render(world, debugMatrix);
		update(delta);
		stage.draw();
		camera.update();
	}

	private void renderField() {
		//háttérszín
		Gdx.gl.glClearColor(125/255f, 162/255f, 119/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
				(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

		//render field
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		//pitch grass lining
		shapeRenderer.setColor(143f/255,181f/255,134f/255,1);
		for (int i=0; i<6; i++) {
			if (i%2 == 0)
				shapeRenderer.rect(-FieldData.TOUCHLINE_LENGTH/2+i*450, -FieldData.GOALLINE_LENGTH/2,
						450, FieldData.GOALLINE_LENGTH);
		}

		shapeRenderer.setColor(1,1,1,0);

		//CENTER SPOT
		shapeRenderer.circle(0,0,FieldData.BALL_RADIUS/2);

		//PENALTY SPOT
		shapeRenderer.circle(-FieldData.TOUCHLINE_LENGTH/2 + FieldData.PENALTYSPOT_DISTANCE_FROM_GOALLINE,0,FieldData.BALL_RADIUS/2);
		shapeRenderer.circle(FieldData.TOUCHLINE_LENGTH/2 - FieldData.PENALTYSPOT_DISTANCE_FROM_GOALLINE,0,FieldData.BALL_RADIUS/2);

		shapeRenderer.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(1,1,1,0);

		//FIELD
		shapeRenderer.rect(-FieldData.TOUCHLINE_LENGTH/2,-FieldData.GOALLINE_LENGTH/2,
				FieldData.TOUCHLINE_LENGTH,FieldData.GOALLINE_LENGTH);

		//GOAL BOX
		//right
		shapeRenderer.rect(-FieldData.TOUCHLINE_LENGTH/2,-FieldData.GOALBOX_HEIGHT/2,
				FieldData.GOALBOX_WIDTH,FieldData.GOALBOX_HEIGHT);
		//left
		shapeRenderer.rect(FieldData.TOUCHLINE_LENGTH/2 - FieldData.GOALBOX_WIDTH,
				FieldData.GOALBOX_HEIGHT/2 - FieldData.GOALBOX_HEIGHT,
				FieldData.GOALBOX_WIDTH,FieldData.GOALBOX_HEIGHT);

		//PENALTY BOX
		//right
		shapeRenderer.rect(-FieldData.TOUCHLINE_LENGTH/2,-FieldData.PENALTYBOX_HEIGHT/2,
				FieldData.PENALTYBOX_WIDTH,FieldData.PENALTYBOX_HEIGHT);
		//left
		shapeRenderer.rect(FieldData.TOUCHLINE_LENGTH/2 - FieldData.PENALTYBOX_WIDTH,
				FieldData.PENALTYBOX_HEIGHT/2 - FieldData.PENALTYBOX_HEIGHT,
				FieldData.PENALTYBOX_WIDTH,FieldData.PENALTYBOX_HEIGHT);

		//HALF-WAY LINE
		shapeRenderer.line(0,-FieldData.GOALLINE_LENGTH/2,0,FieldData.GOALLINE_LENGTH/2);

		//CENTER CIRCLE
		shapeRenderer.circle(0,0,100*2);
		shapeRenderer.end();
	}

	private void renderAimingLine() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		if ((aiming.getAimingType() == AimingType.LINE_AIMING) && (aiming.isAimingInProgress())) {
			Vector3 mousePosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(mousePosition);
			mousePosition.set(mousePosition.x, mousePosition.y,0);

			shapeRenderer.line(aiming.getPlayerOrigoX()*PIXELS_TO_METERS,
					aiming.getPlayerOrigoY()*PIXELS_TO_METERS,
					mousePosition.x, mousePosition.y);

			Vector2 oppositLine = new Vector2(mousePosition.x-aiming.getPlayerOrigoX()*PIXELS_TO_METERS,
					mousePosition.y-aiming.getPlayerOrigoY()*PIXELS_TO_METERS);

			shapeRenderer.setColor(0.5f,1,0.5f,0);

			shapeRenderer.line(aiming.getPlayerOrigoX()*PIXELS_TO_METERS,
					aiming.getPlayerOrigoY()*PIXELS_TO_METERS,
					(aiming.getPlayerOrigoX()*PIXELS_TO_METERS-oppositLine.x),
					(aiming.getPlayerOrigoY()*PIXELS_TO_METERS-oppositLine.y));
		}

		shapeRenderer.end();
	}

	private void renderBall() {
		ballSprite.setPosition((ball.getPosition().x * PIXELS_TO_METERS) - ballSprite.getWidth() / 2,
				(ball.getPosition().y * PIXELS_TO_METERS) - ballSprite.getHeight() / 2)	;
		ballSprite.setRotation((float) Math.toDegrees(ball.getAngle()));
		batch.draw(ballSprite, ballSprite.getX(), ballSprite.getY(), ballSprite.getOriginX(),
				ballSprite.getOriginY(),
				ballSprite.getWidth(), ballSprite.getHeight(), ballSprite.getScaleX(), ballSprite.
						getScaleY(), ballSprite.getRotation());
	}

	private void renderPlayers() {
		Sprite sprite = null;

		if (teamA != null)
			for (Player player : teamA.getPlayers()) {
				sprite = player.getSprite();
				sprite.setPosition((player.getBody().getPosition().x * PIXELS_TO_METERS) - sprite.
								getWidth() / 2,
						(player.getBody().getPosition().y * PIXELS_TO_METERS) - sprite.getHeight() / 2)	;
				sprite.setRotation((float) Math.toDegrees(player.getBody().getAngle()));

				batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(),
						sprite.getOriginY(),
						sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.
								getScaleY(), sprite.getRotation());

				shadowSprite.setPosition((player.getBody().getPosition().x * PIXELS_TO_METERS) - shadowSprite.
								getWidth() / 2,
						(player.getBody().getPosition().y * PIXELS_TO_METERS) - shadowSprite.getHeight() / 2)	;
				batch.draw(shadowSprite, shadowSprite.getX(), shadowSprite.getY(), shadowSprite.getOriginX(),
						shadowSprite.getOriginY(),
						shadowSprite.getWidth(), shadowSprite.getHeight(), shadowSprite.getScaleX(), shadowSprite.
								getScaleY(), shadowSprite.getRotation());
			}

		if (teamB != null)
			for (Player player : teamB.getPlayers()) {
				sprite = player.getSprite();
				sprite.setPosition((player.getBody().getPosition().x * PIXELS_TO_METERS) - sprite.
								getWidth() / 2,
						(player.getBody().getPosition().y * PIXELS_TO_METERS) - sprite.getHeight() / 2)	;
				sprite.setRotation((float) Math.toDegrees(player.getBody().getAngle()));

				batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(),
						sprite.getOriginY(),
						sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.
								getScaleY(), sprite.getRotation());

				shadowSprite.setPosition((player.getBody().getPosition().x * PIXELS_TO_METERS) - shadowSprite.
								getWidth() / 2,
						(player.getBody().getPosition().y * PIXELS_TO_METERS) - shadowSprite.getHeight() / 2)	;
				batch.draw(shadowSprite, shadowSprite.getX(), shadowSprite.getY(), shadowSprite.getOriginX(),
						shadowSprite.getOriginY(),
						shadowSprite.getWidth(), shadowSprite.getHeight(), shadowSprite.getScaleX(), shadowSprite.
								getScaleY(), shadowSprite.getRotation());
			}

		if (state instanceof Placeing) {
			sprite = placeableSprite;
			Placeing placeing = (Placeing)state;
			for (Player player : placeing.getPlayers()) {
				sprite.setPosition((player.getBody().getPosition().x * PIXELS_TO_METERS) - sprite.
								getWidth() / 2,
						(player.getBody().getPosition().y * PIXELS_TO_METERS) - sprite.getHeight() / 2)	;
				sprite.setRotation((float) Math.toDegrees(player.getBody().getAngle()));

				batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(),
						sprite.getOriginY(),
						sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.
								getScaleY(), sprite.getRotation());
			}
		}

		if ((state instanceof AimingState) && (!aiming.isAimingInProgress())) {
			AimingState aimingState = (AimingState)state;

			sprite = movableSprite;
			for (Player player : aimingState.getOnlyMovingPlayers()) {
				sprite.setPosition((player.getBody().getPosition().x * PIXELS_TO_METERS) - sprite.
								getWidth() / 2,
						(player.getBody().getPosition().y * PIXELS_TO_METERS) - sprite.getHeight() / 2)	;
				sprite.setRotation((float) Math.toDegrees(player.getBody().getAngle()));

				batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(),
						sprite.getOriginY(),
						sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.
								getScaleY(), sprite.getRotation());
			}

			sprite = aimableSprite;
			for (Player player : aimingState.getPlayersInSector()) {
				sprite.setPosition((player.getBody().getPosition().x * PIXELS_TO_METERS) - sprite.
								getWidth() / 2,
						(player.getBody().getPosition().y * PIXELS_TO_METERS) - sprite.getHeight() / 2)	;
				sprite.setRotation((float) Math.toDegrees(player.getBody().getAngle()));

				batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(),
						sprite.getOriginY(),
						sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.
								getScaleY(), sprite.getRotation());
			}

		}

	}

	private void renderRubeSpatials() {
		if ((mSpatials != null) && (mSpatials.size > 0)) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			for (int i = 0; i < mSpatials.size; i++) {
				mSpatials.get(i).render(batch, 0);
			}
			batch.end();
		}

		if ((mPolySpatials != null) && (mPolySpatials.size > 0)) {
			mPolyBatch.setProjectionMatrix(camera.combined);
			mPolyBatch.begin();
			for (int i = 0; i < mPolySpatials.size; i++) {
				mPolySpatials.get(i).render(mPolyBatch, 0);
			}
			mPolyBatch.end();
		}
	}

//	private void renderMessage() {
//		font.draw(batch,
//				model.getTeamA().getName() +" - " + model.getTeamB().getName() + " " +
//						model.getTeamA().getScore() + ":" + model.getTeamB().getScore() + " | " + message,
//				-FieldData.TOUCHLINE_LENGTH/2+50,
//				FieldData.GOALLINE_LENGTH/2-10);
//	}



	@Override
	public void dispose() {
		img.dispose();
		world.dispose();
		stage.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.RIGHT)
			camera.translate(20, 0);
		if(keycode == Input.Keys.LEFT)
			camera.translate(-20, 0);
		if(keycode == Input.Keys.UP)
			camera.translate(0, 20);
		if(keycode == Input.Keys.DOWN)
			camera.translate(0, -20);
		if(keycode == Input.Keys.PAGE_UP)
			camera.zoom += 0.05;
		if(keycode == Input.Keys.PAGE_DOWN)
			camera.zoom -= 0.05;
		if(keycode == Input.Keys.I) {
			System.out.println("SOCEVENTS\n\n");
			for (SocEvent socEvent : socEvents) {
				System.out.println(socEvent.getClass().toString());
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.SPACE) {
			//átküldjük a többi kliensnek
			ClientState clientState = state.getClientState();
			Gdx.app.log("StadiumScreen - CLIENT", "sendState - with SPACE BUTTON");
			client.sendState(clientState);
		}

		if(keycode == Input.Keys.O) {
			loadOtherTeam(connectedUsers.teamAId);
		}

		if(keycode == Input.Keys.P) {
			loadOtherTeam(connectedUsers.teamBId);
		}



		if(keycode == Input.Keys.A) {
			loadTeamA();
			model.setTeamA(teamA);

			//TODO a kezdésnél kell majd ezt használni
			if ((teamA != null) && (teamB != null)) {
				controller = new Controller(teamA, teamB, new KickOff(model, teamA, teamB, socEvents));
				state = controller.getState(socEvents);
				controller.setModel(model); //ez lehet nem is kell majd
				lastTouchPlayer = teamA.getPlayers().get(1);
				lastTouchTeam = teamA;

			}

		}

		if(keycode == Input.Keys.B) {
			loadTeamB();
			model.setTeamB(teamB);

			//TODO a kezdésnél kell majd ezt használni
			if ((teamA != null) && (teamB != null)) {
				controller = new Controller(teamA, teamB, new KickOff(model, teamA, teamB, socEvents));
				state = controller.getState(socEvents);
				controller.setModel(model); //ez lehet nem is kell majd
				lastTouchPlayer = teamA.getPlayers().get(1);
				lastTouchTeam = teamA;
			}
		}


		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	// On touch we apply force from the direction of the users touch.
	// This could result in the object "spinning"
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// translate the mouse coordinates to world coordinates
		testPoint.set(screenX, screenY, 0);
		camera.unproject(testPoint);
		testPoint.set(testPoint.x / PIXELS_TO_METERS, testPoint.y / PIXELS_TO_METERS, 0);
		// ask the world which bodies are within the given
		// bounding box around the mouse pointer
		hitBody = null;
		world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f, testPoint.x + 0.1f, testPoint.y + 0.1f);

		if (state instanceof AimingState) {
			if (hitBody != null) {
				//megnézzük, hogy a user jogosult-e célozni
				Boolean isAimableUser = false;
				AimingState aimingState = (AimingState)state;

				if (aimingState.getTeam().equals(teamA) && (mainModel.getUser().equals(userA))) isAimableUser = true;
				if (aimingState.getTeam().equals(teamB) && (mainModel.getUser().equals(userB))) isAimableUser = true;

				if (isAimableUser)
					if (aiming.getAimingType() == AimingType.POKE_AIMING) {
						Vector2 center = hitBody.getWorldCenter();
	//					System.out.println("--" + hitBody.getWorldCenter().x + " " + hitBody.getWorldCenter().y);
						Vector2 ketto = new Vector2(center.x - testPoint.x, center.y - testPoint.y);
						hitBody.applyLinearImpulse(ketto.x + ketto.x * 150, ketto.y + ketto.y * 150, testPoint.x, testPoint.y, true);
						hitBody.setAwake(true);
						socEvents.add(new AimingFinished());
						socEventChanged();

					}

				if (isAimableUser)
					if (aiming.getAimingType() == AimingType.LINE_AIMING) {
						for (Player player : teamA.getPlayers()) player.setAimed(false);
						for (Player player : teamB.getPlayers()) player.setAimed(false);

						//megnézzük, hogy a kattintott játékos jöhet-e célzással
						Boolean isAimablePlayer = false;
						aimingState = (AimingState)state;
						for (Player player : aimingState.getPlayers()) {
							if (player.getBody().equals(hitBody)) {
								isAimablePlayer = true;
								socEvents.add(new AimingStarted(player));
								socEventChanged();

								player.setAimed(true);
							}
						}

						if (isAimablePlayer) {
							aiming.setPlayerBody(hitBody);
							aiming.setAimingInProgress(true);
							aiming.setPlayerOrigoX(hitBody.getWorldCenter().x);
							aiming.setPlayerOrigoY(hitBody.getWorldCenter().y);
						}
					}
			}
		} else {
			if (hitBody != null) {
				Boolean isPlaceableUser = false;

				if (state instanceof Placeing) {
					Placeing placeing = (Placeing)state;
					if (placeing.getTeam().equals(teamA) && (mainModel.getUser().equals(userA))) isPlaceableUser = true;
					if (placeing.getTeam().equals(teamB) && (mainModel.getUser().equals(userB))) isPlaceableUser = true;
				}

				if (isPlaceableUser)
					if (state instanceof Placeing) {

						Boolean isPlaceablePlayer = false;
						Placeing placeing = (Placeing)state;
						for (Player player : placeing.getPlayers()) {
							if (player.getBody().equals(hitBody)) isPlaceablePlayer = true;
						}

						if (isPlaceablePlayer) {
							MouseJointDef def = new MouseJointDef();
							def.bodyA = groundBody;
							def.bodyB = hitBody;
							def.collideConnected = true;
							def.target.set(testPoint.x, testPoint.y);
							def.maxForce = 100000.0f * hitBody.getMass()*10000;
							mouseJoint = (MouseJoint)world.createJoint(def);
							hitBody.setAwake(true);
						}
					}
			}
		}

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
			Placeing placeing = (Placeing) state;
			placeing.playerPlaced();

			if (placeing.isAllPlayerPlaced()) {
				socEvents.add(new PlaceingFinished());
				moveStartTime = System.nanoTime(); //ez azért kell, hogy a helyezés ideje ne számítson

				socEventChanged();


				isStateSentToClients = false;


				ClientPlaceing clientPlaceing = new ClientPlaceing();
				for (Player player : teamA.getPlayers()) {

					Position position = new Position();
					position.x = player.getBody().getPosition().x * FieldData.PIXELS_TO_METERS;
					position.y = player.getBody().getPosition().y * FieldData.PIXELS_TO_METERS;
					clientPlaceing.teamApositions.add(position);
				}

				for (Player player : teamB.getPlayers()) {
					Position position = new Position();
					position.x = player.getBody().getPosition().x * FieldData.PIXELS_TO_METERS;
					position.y = player.getBody().getPosition().y * FieldData.PIXELS_TO_METERS;
					clientPlaceing.teamBPositions.add(position);
				}
				System.out.println("placeingFinished - send to client");
				Gdx.app.log("StadiumScreen - CLIENT", "sendPlaceing");
				client.sendPlaceing(clientPlaceing);

				model.setTeamsPositions(clientPlaceing.teamApositions, clientPlaceing.teamBPositions);

			}
		}

		if ((aiming.getAimingType() == AimingType.LINE_AIMING) && (aiming.isAimingInProgress())) {
			aiming.setAimingInProgress(false);

			Vector3 mousePosition = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(mousePosition);
			mousePosition.set(mousePosition.x, mousePosition.y,0);

			Vector2 ketto = new Vector2(aiming.getPlayerOrigoX() - mousePosition.x/PIXELS_TO_METERS,
					aiming.getPlayerOrigoY() - mousePosition.y/PIXELS_TO_METERS);

			ketto.x /= 0.09f;
			ketto.y /= 0.09f;
			Float maxLen = 60f;

			if (ketto.len()>maxLen) {
				ketto.setLength(maxLen);
			}

			ClientAiming clientAiming = new ClientAiming();

			AimingState aimingState = (AimingState)state;

			Player player = (Player) aiming.getPlayerBody().getUserData();

			if (aimingState.getTeam().equals(teamA)) {
				clientAiming.teamAOrB = "teamApositions";
				clientAiming.playerNumber = teamA.getPlayers().indexOf(player);
			}

			if (aimingState.getTeam().equals(teamB)) {
				clientAiming.teamAOrB = "teamBPositions";
				clientAiming.playerNumber = teamB.getPlayers().indexOf(player);
			}

			clientAiming.forceX = ketto.x;
			clientAiming.forceY = ketto.y;
			clientAiming.playerOrigoX = aiming.getPlayerOrigoX();
			clientAiming.playerOrigoY = aiming.getPlayerOrigoY();

			Gdx.app.log("StadiumScreen - CLIENT", "sendAiming");
			client.sendAiming(clientAiming);


			aiming.getPlayerBody().applyLinearImpulse(ketto.x, ketto.y,
					aiming.getPlayerOrigoX(),
					aiming.getPlayerOrigoY(), true);

//			hitBody.setAwake(true);

			socEvents.add(new AimingFinished());
			moveStartTime = System.nanoTime();
			socEventChanged();

		}

		return false;
	}

	Vector2 target = new Vector2();

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (mouseJoint != null) {
			camera.unproject(testPoint.set(screenX,screenY,0));
			testPoint.set(testPoint.x/PIXELS_TO_METERS, testPoint.y/PIXELS_TO_METERS,0);
			mouseJoint.setTarget(target.set(testPoint.x,testPoint.y));
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {

		camera.zoom += (amount * 0.1f);
		if (camera.zoom < 0.1f)
		{
			camera.zoom = 0.1f;
		}
		camera.update();
		return true;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		initialScale = camera.zoom;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if (!aiming.isAimingInProgress() && (mouseJoint == null)) camera.position.add(-deltaX * camera.zoom, deltaY * camera.zoom, 0);
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		float ratio = initialDistance / distance;
		camera.zoom = initialScale * ratio;
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void pinchStop() {
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
	public void update(Observable observable, Object o) {
		if (o instanceof Ball) {
			ball.setLinearVelocity(0, 0);
			ball.setTransform(model.getBall().getPosX(), model.getBall().getPosY(), 0);
		}

		if (o instanceof Team) {
			for (Player player : teamA.getPlayers())
				player.getBody().setTransform(new Vector2(player.position.x / PIXELS_TO_METERS,
						player.position.y / PIXELS_TO_METERS), 0);

			for (Player player : teamB.getPlayers())
				player.getBody().setTransform(new Vector2(player.position.x / PIXELS_TO_METERS,
						player.position.y / PIXELS_TO_METERS), 0);
		}
	}

	public void connectedUsersChanged(ConnectedUsers connectedUsers) {
		Gdx.app.log("StadiumScreen - CLIENT", "CONNECTED_USERS_CHANGED");
		this.connectedUsers = connectedUsers;

		//TODO ez lehet, hogy így  nem lesz jó
		if (connectedUsers.connectedUserAId != null)
			if (teamA == null)
				if (userRole != UserRole.USER_A) {
					Gdx.app.log("StadiumScreen", "loadOtherTeam TEAM_A");
					loadOtherTeam(connectedUsers.teamAId);
				}

		if (connectedUsers.connectedUserBId != null)
			if (teamB == null)
				if (userRole != UserRole.USER_B) {
					Gdx.app.log("StadiumScreen", "loadOtherTeam TEAM_B");
					loadOtherTeam(connectedUsers.teamBId);
				}


		String userNames = "";
		if (connectedUsers.connectedUserAId != null)
			userNames += "Home user: " + mainModel.getUserById(connectedUsers.connectedUserAId) +
					" (" + connectedUsers.teamAId + ")";
		else userNames += "Home user: -";

		if (connectedUsers.connectedUserBId != null)
			userNames += "\nAway user: " + mainModel.getUserById(connectedUsers.connectedUserBId) +
					" (" + connectedUsers.teamBId + ")";
		else userNames += "\nAway user: -";

		userNames += "\nSpectators:";

		for (Integer id : connectedUsers.connectedSpectatorsIds) {
			userNames += "\n" + mainModel.getUserById(id);
		}

		if (connectedUsersLb == null) {
			connectedUsersStartText = userNames;

		} else connectedUsersLb.setText(userNames);


	}

	public void aimingReceived(ClientAiming clientAiming) {
		Gdx.app.log("StadiumScreen - CLIENT", "aimingReceived");
		Body playerBody = null;
		Player player = null;

		if (clientAiming.teamAOrB.equals("teamApositions")) {
			player = teamA.getPlayers().get(clientAiming.playerNumber);
			playerBody = teamA.getPlayers().get(clientAiming.playerNumber).getBody();
		}

		if (clientAiming.teamAOrB.equals("teamBPositions")) {
			player = teamB.getPlayers().get(clientAiming.playerNumber);
			playerBody = teamB.getPlayers().get(clientAiming.playerNumber).getBody();
		}

		socEvents.add(new AimingStarted(player));
		socEventChanged();

		for (Player actPlayer : teamA.getPlayers()) actPlayer.setAimed(false);
		for (Player actPlayer : teamB.getPlayers()) actPlayer.setAimed(false);
		player.setAimed(true);
		aiming.setPlayerBody(playerBody);

		playerBody.applyLinearImpulse(clientAiming.forceX, clientAiming.forceY,
				clientAiming.playerOrigoX,
				clientAiming.playerOrigoY, true);

		socEvents.add(new AimingFinished());
		moveStartTime = System.nanoTime();
		socEventChanged();
		aiming.setAimingInProgress(false);

	}

	public void placeingReceived(ClientPlaceing clientPlaceing) {
		System.out.println("RECEIVED PLACEING..............");
		Gdx.app.log("StadiumScreen - CLIENT", "placeingReceived");
		socEvents.add(new PlaceingFinished());
		socEventChanged();
		model.setTeamsPositions(clientPlaceing.teamApositions, clientPlaceing.teamBPositions);
	}

	public void movingReceived(ClientMoving clientMoving) {
		System.out.println("RECEIVED MOVING..............");
		Gdx.app.log("StadiumScreen - CLIENT", "movingReceived");
		model.setTeamsPositions(clientMoving.teamApositions, clientMoving.teamBPositions,
				clientMoving.ballPosition);
	}

	public void stateReceived(ClientState clientState) {
		Gdx.app.log("StadiumScreen - CLIENT", "stateReceived");

		Team team;
		List<Player> players = new ArrayList<Player>();

		if (clientState.stateType == StateType.AIMING_STATE) {
			if (clientState.teamSide == TeamSide.LEFT) {
				Gdx.app.log("StadiumScreen - CLIENT", "stateReceived - AIMING STATE - TeamSide.LEFT");
				team = teamA;
				for (Integer i : clientState.playerNumbers)
					players.add(teamA.getPlayers().get(i));

			} else {
				Gdx.app.log("StadiumScreen - CLIENT", "stateReceived - AIMING STATE - TeamSide.RIGHT");
				team = teamB;
				for (Integer i : clientState.playerNumbers)
					players.add(teamB.getPlayers().get(i));
			}
			state = new AimingState(team, players);
		}

		if (clientState.stateType == StateType.PLACEING) {
			if (clientState.teamSide == TeamSide.LEFT) {
				Gdx.app.log("StadiumScreen - CLIENT", "stateReceived - PLACEING STATE - TeamSide.LEFT");
				team = teamA;
				for (Integer i : clientState.playerNumbers)
					players.add(teamA.getPlayers().get(i));

			} else {
				Gdx.app.log("StadiumScreen - CLIENT", "stateReceived - PLACEING STATE - TeamSide.RIGHT");
				team = teamB;
				for (Integer i : clientState.playerNumbers)
					players.add(teamB.getPlayers().get(i));
			}
			state = new Placeing(team, players, clientState.numberOfPlaceing);
		}

		if (clientState.stateType == StateType.OUTOFPLAY) {
			Gdx.app.log("StadiumScreen - CLIENT", "stateReceived - OUT_OF_PLAY");
			state = new OutOfPlay();
		}
		if (clientState.stateType == StateType.ACTION) {
			Gdx.app.log("StadiumScreen - CLIENT", "stateReceived - ACTION");
			state = new Action();
		}

		//beállítjuk a whosTurn értékét
		if (state instanceof AimingState) {
			AimingState actAiming = (AimingState) state;

			if (actAiming.getTeam().getTeamSide() == TeamSide.LEFT)
				whosTurn = UserRole.USER_A;
			else whosTurn = UserRole.USER_B;
		}
		if (state instanceof Placeing) {
			Placeing actPlaceing = (Placeing)state;
			if (actPlaceing.getTeam().getTeamSide() == TeamSide.LEFT)
				whosTurn = UserRole.USER_A;
			else whosTurn = UserRole.USER_B;
		}

//		model.setPlayersMoving(true);
//		model.getBall().setMoving(true);
//		System.out.println("RECEIVED: " + whosTurn.toString());

	}

	public void socEventReceived(ClientSocEvent clientSocEvent) {
		Gdx.app.log("StadiumScreen", "SOCEVENT received");
		SocEvent socEvent = null;
		Player player;

		if (clientSocEvent.type == SocEventType.AIMING_STARTED) {
			if (clientSocEvent.teamSide == TeamSide.LEFT) {
				Gdx.app.log("StadiumScreen", "SOCEVENT received - AIMING STATE - left");
				player = teamA.getPlayers().get(clientSocEvent.playerNumber);
			} else {
				Gdx.app.log("StadiumScreen", "SOCEVENT received - AIMING STATE - right");
				player = teamB.getPlayers().get(clientSocEvent.playerNumber);
			}


			socEvent = new AimingStarted(player);
			System.out.println("---RECPLAY---" + player.getFirstName());
		}

		if (clientSocEvent.type == SocEventType.PLAYER_TOUCHED_BALL) {
			if (clientSocEvent.teamSide == TeamSide.LEFT) {
				Gdx.app.log("StadiumScreen", "SOCEVENT received - PLAYER_TOUCHED_BALL - left");
				player = teamA.getPlayers().get(clientSocEvent.playerNumber);

			} else {
				Gdx.app.log("StadiumScreen", "SOCEVENT received - PLAYER_TOUCHED_BALL - right");
				player = teamB.getPlayers().get(clientSocEvent.playerNumber);
			}


			System.out.println("---RECPLAY---" + player.getFirstName());
			socEvent = new PlayerTouchedBall(player);
		}

		if (clientSocEvent.type == SocEventType.PLAYER_TOUCHED_PLAYER) {
			if (clientSocEvent.teamSide == TeamSide.LEFT) {
				Gdx.app.log("StadiumScreen", "SOCEVENT received - PLAYER_TOUCHED_PLAYER - left");
				player = teamA.getPlayers().get(clientSocEvent.playerNumber);
			} else {
				Gdx.app.log("StadiumScreen", "SOCEVENT received - PLAYER_TOUCHED_PLAYER - right");
				player = teamB.getPlayers().get(clientSocEvent.playerNumber);
			}


			System.out.println("---RECPLAY---" + player.getFirstName());
			socEvent = new PlayerTouchedPlayer(player, clientSocEvent.x, clientSocEvent.y);
		}

		if (clientSocEvent.type == SocEventType.AIMING_FINISHED) {
			Gdx.app.log("StadiumScreen", "SOCEVENT received - AIMING_FINISHED");
			//
			model.setPlayersMoving(true);
			model.getBall().setMoving(true);
			//
			socEvent = new AimingFinished();
			moveStartTime = System.nanoTime();

		}
		if (clientSocEvent.type == SocEventType.PLACEING_FINISHED) {
			Gdx.app.log("StadiumScreen", "SOCEVENT received - PLACEING_FINISHED");
			socEvent = new PlaceingFinished();
		}
		if (clientSocEvent.type == SocEventType.PLAYERS_STOPPED) {
			Gdx.app.log("StadiumScreen", "SOCEVENT received - PLAYERS_STOPPED");
			model.setPlayersMoving(false);
			socEvent = new PlayersStopped();
		}
		if (clientSocEvent.type == SocEventType.BALL_STOPPED) {
			Gdx.app.log("StadiumScreen", "SOCEVENT received - BALL_STOPPED");
			socEvent = new BallStopped();
			model.getBall().setMoving(false);
			ballIsMoving = false;
		}

		System.out.println("SOCEVENT RECEIVED: " + socEvent.getClass());
		socEvents.add(socEvent);
	}

	public void socEventChanged() {
		Gdx.app.log("StadiumScreen", "SOCEVENT CHANGED");
		//a userB nem hoz létre saját socEventeket, ezeket a userB-től kapja,
		//így mindig kitöröljük azt, amita userB hozzáad a saját listájához
//		if (userRole != whosTurn) {
		if (userRole == UserRole.USER_B) {
			if (socEvents.size()>0) socEvents.remove(socEvents.size()-1);
		}

		//csak az A csapat küldi az infót
//		if ((socEvents.size()>0) && (userRole == whosTurn)) {
		if ((socEvents.size()>0) && (userRole == UserRole.USER_A)) {
			System.out.println("NEW socevent (" + socEvents.get(socEvents.size()-1).getClass().toString() + " - State: "
					+ state.getClass().toString());

			ClientSocEvent clientSocEvent = socEvents.get(socEvents.size()-1).getClientSocEvent();

//			client.sendSocEvent(clientSocEvent);
		} else System.out.println("A socevents üres.");
	}

	public MainModel getMainModel() {
		return mainModel;
	}

	public void setMainModel(MainModel mainModel) {
		this.mainModel = mainModel;
	}

	public Screen getNextScreen() {
		return nextScreen;
	}

	public void setNextScreen(Screen nextScreen) {
		this.nextScreen = nextScreen;
	}

	public ButtonTeam getButtonTeam() {
		return buttonTeam;
	}

	public void setButtonTeam(ButtonTeam buttonTeam) {
		this.buttonTeam = buttonTeam;
	}

	public User getUserA() {
		return userA;
	}

	public void setUserA(User userA) {
		this.userA = userA;
	}

	public User getUserB() {
		return userB;
	}

	public void setUserB(User userB) {
		this.userB = userB;
	}

	public java.util.List<User> getSpectators() {
		return spectators;
	}

	public void setSpectators(java.util.List<User> spectators) {
		this.spectators = spectators;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
}