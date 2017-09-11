package com.server;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.soccer.aiming.ClientAiming;
import com.soccer.aiming.ClientMoving;
import com.soccer.aiming.ClientPlaceing;
import com.soccer.socevents.ClientSocEvent;
import com.soccer.states.ClientState;
import com.user.MessageCenter;
import com.user.MessageType;
import com.user.PasswordCheck;
import com.user.UserLoader;
import com.user.UserRole;

public class SocServerWindow extends JFrame {
	static public final int port = 54555;
	private Server server;
	private JButton stopBtn, startBtn;
	private JLabel statusLb;
	private JScrollPane scrollPane;
	private JTextArea consoleArea;
	private List<Match> matches;

		
	public SocServerWindow() {

		server = new Server(10000, 10000) {
			@Override
			protected Connection newConnection() {
				return new MatchConnection();
			}
		};
		Network.register(server);
		matches = new ArrayList<Match>();

		server.addListener(new Listener() {
			@Override
			public void connected(Connection connection) {
				System.out.println("Connected");
			}

			@Override
			public void disconnected(Connection connection) {
				consoleArea.setText(consoleArea.getText()+"\nOne client disconnected");
				
				server.sendToAllTCP(new Message("One client left us."));

				if (connection instanceof MatchConnection) {
					MatchConnection matchConnection = (MatchConnection)connection;

					//megkeressük, hogy melyik meccsből lépett ki valaki
					for (Match match : matches) {
						if (match.match_id.equals(matchConnection.match_id)) {

							if (matchConnection.equals(match.userAConnection))
								match.userAConnection = null;
							if (matchConnection.equals(match.userBConnection))
								match.userBConnection = null;

							Integer indexToRemove = -1;
							int i=0;
							for (MatchConnection spectatorConnection : match.spectatorConnnecions) {
								if (spectatorConnection.equals(matchConnection)) indexToRemove = i;
								i++;
							}

							if (indexToRemove != -1)
								match.spectatorConnnecions.remove(matchConnection);


							ConnectedUsers connectedUsers = new ConnectedUsers();
							connectedUsers.connectedSpectatorsIds = match.getConnectedSpectatorsIds();
							connectedUsers.connectedUserAId = match.getConnectedUserAId();
							connectedUsers.connectedUserBId = match.getConnectedUserBId();
							connectedUsers.teamAId = match.getUserATeamId();
							connectedUsers.teamBId = match.getUserBTeamId();
							server.sendToAllTCP(connectedUsers);

						}
					}

				}
				
			}

			@Override
			public void received(Connection connection, Object object) {
				System.out.println("Received");

				if (object instanceof RegisterUser) {

					RegisterUser registerUser = (RegisterUser) object;
					MatchConnection matchConnection = (MatchConnection)connection;
					Match match = new Match();

					Boolean isNewMatch = true;
					for (Match actMatch : matches) {
						if (actMatch.match_id.equals(
								registerUser.match_id)) {
							isNewMatch = false;
							match = actMatch;
						}

					}
//
					matchConnection.match_id = registerUser.match_id;
					matchConnection.user_id = registerUser.user_id;
					matchConnection.userRole = registerUser.userRole;
                    matchConnection.team_id = registerUser.team_id;
//
					if (registerUser.userRole.equals(UserRole.USER_A)) match.userAConnection = matchConnection;
					if (registerUser.userRole.equals(UserRole.USER_B)) match.userBConnection = matchConnection;
					if (registerUser.userRole.equals(UserRole.SPECTATOR))
						if (!match.spectatorConnnecions.contains(matchConnection))
							match.spectatorConnnecions.add(matchConnection);

					consoleArea.setText(consoleArea.getText()+
							"\n" + registerUser.user_id +  " connected" );

					if (isNewMatch) {
						match.match_id = registerUser.match_id;
						matches.add(match);
						consoleArea.setText(consoleArea.getText()+"\nNew match created.");
					} else {
						consoleArea.setText(consoleArea.getText()+"\nMatch has existed.");
					}

					ConnectedUsers connectedUsers = new ConnectedUsers();
					connectedUsers.connectedSpectatorsIds = match.getConnectedSpectatorsIds();
					connectedUsers.connectedUserAId = match.getConnectedUserAId();
					connectedUsers.connectedUserBId = match.getConnectedUserBId();
                    connectedUsers.teamAId = match.getUserATeamId();
                    connectedUsers.teamBId = match.getUserBTeamId();
					server.sendToAllTCP(connectedUsers);

				}

				if (object instanceof ClientAiming) {
					ClientAiming clientAiming = (ClientAiming)object;
					MatchConnection matchConnection = (MatchConnection)connection;
					consoleArea.setText(consoleArea.getText()+"\nclientAiming");

					server.sendToAllExceptTCP(matchConnection.getID(), clientAiming);
				}

				if (object instanceof ClientPlaceing) {
					System.out.println("Server received client placeing");
					ClientPlaceing clientPlaceing = (ClientPlaceing)object;
					MatchConnection matchConnection = (MatchConnection)connection;
					consoleArea.setText(consoleArea.getText()+"\nclientPlaceing");

					server.sendToAllExceptTCP(matchConnection.getID(), clientPlaceing);
				}

				if (object instanceof ClientMoving) {
					System.out.println("Server received client moving");
					ClientMoving clientMoving = (ClientMoving)object;
					MatchConnection matchConnection = (MatchConnection)connection;
					consoleArea.setText(consoleArea.getText()+"\nclientMoving");

					server.sendToAllExceptUDP(matchConnection.getID(), clientMoving);
				}

				if (object instanceof ClientSocEvent) {
					System.out.println("Server received client socevent");
					ClientSocEvent clientSocEvent = (ClientSocEvent)object;
					MatchConnection matchConnection = (MatchConnection)connection;
					consoleArea.setText(consoleArea.getText()+"\nclientSoEvent");

					server.sendToAllExceptTCP(matchConnection.getID(), clientSocEvent);
				}

				if (object instanceof ClientState) {
					ClientState clientState = (ClientState)object;
					MatchConnection matchConnection = (MatchConnection)connection;
					consoleArea.setText(consoleArea.getText()+"\nclientState");
					server.sendToAllExceptTCP(matchConnection.getID(), clientState);
				}


				if (object instanceof Message) {
					Message message = (Message)object;
					System.out.println("Server received this message: " + message);
					consoleArea.setText(consoleArea.getText()+"\nServer received this message: " + message);
				}

				if (object instanceof LoginRequest) {

					LoginRequest loginRequest = (LoginRequest)object;
					System.out.println("LoginRequest--" + loginRequest.userName + " - " +
						loginRequest.password);
					LoginResponse loginResponse = new LoginResponse();
					loginResponse.userId =
							PasswordCheck.isCorrectPassword(loginRequest.userName, loginRequest.password);
					server.sendToTCP(connection.getID(), loginResponse);

				}

				if (object instanceof UserLoaderRequest) {
					UserLoaderRequest userLoaderRequest = (UserLoaderRequest)object;
					UserLoaderResponse userLoaderResponse = new UserLoaderResponse();

					userLoaderResponse.userJson
							= UserLoader.loadActualUser(userLoaderRequest.userId);

					userLoaderResponse.usersJson
							= UserLoader.loadAllUsers();

					server.sendToTCP(connection.getID(), userLoaderResponse);
				}

				if (object instanceof TeamLoaderRequest) {
					TeamLoaderRequest teamLoaderRequest = (TeamLoaderRequest) object;
					TeamLoaderResponse teamLoaderResponse = new TeamLoaderResponse();

					teamLoaderResponse.teamJson = UserLoader.loadTeam(teamLoaderRequest.teamId);

					server.sendToTCP(connection.getID(), teamLoaderResponse);
				}

				if (object instanceof MessageRequest) {
					MessageRequest messageRequest = (MessageRequest)object;
					MessageResponse messageResponse = new MessageResponse();

					messageResponse.messageType = messageRequest.messageType;

					if (messageRequest.messageType == MessageType.ALL_BOOKED_MATCHES) {
						messageResponse.responseList = MessageCenter.getAllBookedMatches();
					}

					server.sendToTCP(connection.getID(), messageResponse);
				}


			}
		});
		
		
		createGUI();		
	}
	
	public void createGUI() {
		
		setBounds(400,100,300,300);
		setLayout(new FlowLayout());		
		
		
		startBtn = new JButton("Start");
		stopBtn = new JButton("Stop");
		statusLb = new JLabel("Server has stopped");
		consoleArea = new JTextArea(20,20);
		
		
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					server.bind(port, port);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				server.start();
				statusLb.setText("Server is running");
			}
		});
		
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				server.stop();
				
				statusLb.setText("Server has stopped");
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosed (WindowEvent evt) {
				server.stop();
				System.out.println("Server has stopped.");
			}
		});

		scrollPane = new JScrollPane(consoleArea);

		add(startBtn);
		add(stopBtn);
		add(statusLb);

		add(scrollPane);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		Log.set(Log.LEVEL_DEBUG);
		new SocServerWindow();
	}

}
