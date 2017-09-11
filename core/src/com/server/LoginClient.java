package com.server;

import com.badlogic.gdx.utils.Json;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.server.LoginRequest;
import com.server.LoginResponse;
import com.server.Message;
import com.server.Network;
import com.soccer.StadiumScreen;
import com.soccer.aiming.ClientAiming;
import com.soccer.aiming.ClientPlaceing;
import com.soccer.screens.CalendarScreen;
import com.soccer.screens.LoginScreen;
import com.user.BookedMatch;
import com.user.ButtonTeam;
import com.user.MessageType;
import com.user.User;
import com.user.UserRole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

enum ConnectType {
	LOGIN, USERLOADER, MESSAGE, TEAMLOADER;
}

public class LoginClient extends Client {
	static public final int port = 54555;
	public Client client;
	private LoginScreen loginScreen;
	private StadiumScreen stadiumScreen;
    private CalendarScreen calendarScreen;
	private LoginRequest loginRequest;
	private UserLoaderRequest userLoaderRequest;
    private MessageRequest messageRequest;
	private TeamLoaderRequest teamLoaderRequest;
//	private String userName, password;

	public LoginClient(int writeBufferSizte, int objectBufferSize) {
		super(writeBufferSizte, objectBufferSize);

		Log.set(Log.LEVEL_DEBUG);

//		this.userName = userName;
//		this.password = password;

		client = this;

		client.start();
		Network.register(client);


		client.addListener(new Listener() {

			@Override
			public void connected(Connection connection) {
//				client.sendTCP(registerUser);
			}

			@Override
			public void disconnected(Connection connection) {
				client.sendTCP(new Message("Bye. Ezt �n, a t�voz� kliens k�ld�m."));
				System.out.println("Disconnected");
			}

			@Override
			public void received(Connection connection, Object object) {

				if (object instanceof LoginResponse) {
					LoginResponse loginResponse = (LoginResponse)object;
//					client.close();
					loginScreen.sendLoginResult(loginResponse.userId);
				}

				if (object instanceof UserLoaderResponse) {
					UserLoaderResponse userLoaderResponse = (UserLoaderResponse)object;
//					client.close();
					Json json  = new Json();
					User user = json.fromJson(User.class, userLoaderResponse.userJson);
					List<User> users = new ArrayList<User>();
					for (String userJson : userLoaderResponse.usersJson) {
						users.add(json.fromJson(User.class, userJson));
					}
					loginScreen.sendUserResult(user, users);
				}

				if (object instanceof TeamLoaderResponse) {
					TeamLoaderResponse teamLoaderResponse = (TeamLoaderResponse)object;
					Json json  = new Json();
					ButtonTeam team = json.fromJson(ButtonTeam.class, teamLoaderResponse.teamJson);
					stadiumScreen.sendTeam(team);
				}

                if (object instanceof MessageResponse) {
                    MessageResponse messageResponse = (MessageResponse)object;
                    client.close();
                    Json json  = new Json();

                    if (messageResponse.messageType == MessageType.ALL_BOOKED_MATCHES) {
                        List<BookedMatch> bookedMatches = new ArrayList<BookedMatch>();

                        for (String matchJson : messageResponse.responseList) {
                            bookedMatches.add(json.fromJson(BookedMatch.class, matchJson));
                        }

                        calendarScreen.sendAllBookedMatches(bookedMatches);
                    }

                }
				

			}
		});
	}

	public void sendLoginRequest(String userName, String password) {
		loginRequest = new LoginRequest();
		loginRequest.userName = userName;
		loginRequest.password = password;
		connect(ConnectType.LOGIN);
	}

	public void sendUserLoaderRequest(Integer userId) {
		userLoaderRequest = new UserLoaderRequest();
		userLoaderRequest.userId = userId;
		connect(ConnectType.USERLOADER);
	}

	public void sendTeamLoaderRequest(Integer teamId) {
		teamLoaderRequest = new TeamLoaderRequest();
		teamLoaderRequest.teamId = teamId;
		connect(ConnectType.TEAMLOADER);
	}

    public void sendMessageRequest(Integer userId, MessageType messageType) {
        messageRequest = new MessageRequest();
        messageRequest.messageType = messageType;
        messageRequest.userId = userId;
        connect(ConnectType.MESSAGE);
    }


	public void connect(final ConnectType connectType) {
		new Thread("Connect") {
			public void run () {
				try {
					client.connect(5000, "192.168.0.18", port, port);

					if (connectType == ConnectType.LOGIN) {
						client.sendTCP(loginRequest);
					}

					if (connectType == ConnectType.USERLOADER) {
						client.sendTCP(userLoaderRequest);
					}

					if (connectType == ConnectType.TEAMLOADER) {
						client.sendTCP(teamLoaderRequest);
					}

                    if (connectType == ConnectType.MESSAGE) {
                        client.sendTCP(messageRequest);
                    }

					System.out.println("sendTCP");
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}.start();
	}

	public void disconnect() {
		client.close();
	}

	public LoginScreen getLoginScreen() {
		return loginScreen;
	}

	public void setLoginScreen(LoginScreen loginScreen) {
		this.loginScreen = loginScreen;
	}

    public CalendarScreen getCalendarScreen() {
        return calendarScreen;
    }

    public void setCalendarScreen(CalendarScreen calendarScreen) {
        this.calendarScreen = calendarScreen;
    }

	public StadiumScreen getStadiumScreen() {
		return stadiumScreen;
	}

	public void setStadiumScreen(StadiumScreen stadiumScreen) {
		this.stadiumScreen = stadiumScreen;
	}
}
