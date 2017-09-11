package com.server;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.soccer.StadiumScreen;
import com.soccer.aiming.ClientAiming;
import com.soccer.aiming.ClientMoving;
import com.soccer.aiming.ClientPlaceing;
import com.soccer.socevents.ClientSocEvent;
import com.soccer.states.ClientState;
import com.user.UserRole;

import java.io.IOException;


public class SocClient extends Client {
	static public final int port = 54555;
	public Client client;
	private String match_id;
	private Integer user_id, team_id;
	private UserRole userRole;
	private StadiumScreen stadiumScreen;



	public SocClient(final String match_id, final Integer user_id, final UserRole userRole,
					 final Integer team_id) {

		this.match_id = match_id;
		this.user_id = user_id;
		this.userRole = userRole;
		this.team_id = team_id;
		
		client = this;
		client.start();
		Network.register(client);


		client.addListener(new Listener() {

			@Override
			public void connected(Connection connection) {
				RegisterUser registerUser = new RegisterUser();
				registerUser.match_id = match_id;
				registerUser.user_id = user_id;
				registerUser.userRole = userRole;
				registerUser.team_id = team_id;
				client.sendTCP(registerUser);
			}

			@Override
			public void disconnected(Connection connection) {
			}

			@Override
			public void received(Connection connection, Object object) {

				if (object instanceof ClientAiming) {
					ClientAiming clientAiming = (ClientAiming) object;
					stadiumScreen.aimingReceived(clientAiming);
				}

				if (object instanceof ClientPlaceing) {
					ClientPlaceing clientPlaceing = (ClientPlaceing)object;
					stadiumScreen.placeingReceived(clientPlaceing);
				}

				if (object instanceof ClientMoving) {
					ClientMoving clientMoving = (ClientMoving)object;
					stadiumScreen.movingReceived(clientMoving);
				}

				if (object instanceof ClientSocEvent) {
					ClientSocEvent clientSocEvent = (ClientSocEvent)object;
					stadiumScreen.socEventReceived(clientSocEvent);
				}

				if (object instanceof ClientState) {
					ClientState clientState = (ClientState) object;
					stadiumScreen.stateReceived(clientState);
				}

				if (object instanceof ConnectedUsers) {
					ConnectedUsers connectedUsers = (ConnectedUsers)object;
					stadiumScreen.connectedUsersChanged(connectedUsers);
				}

				
				if (object instanceof Message) {
					Message message = (Message)object;
				} else System.out.println("Unidentified message - client");
				

			}
		});


	}


	public void sendAiming(ClientAiming clientAiming) {
		client.sendTCP(clientAiming);
	}

	public void sendPlaceing(ClientPlaceing clientPlaceing) {
		System.out.println("sendPlaceing");
		client.sendTCP(clientPlaceing);
	}

	public void sendMoving(ClientMoving clientMoving) {
		System.out.println("sendMoving");
		client.sendUDP(clientMoving);
	}

	public void sendSocEvent(ClientSocEvent clientSocEvent) {
		System.out.println("sendSocEvent");
		client.sendTCP(clientSocEvent);
	}

	public void sendState(ClientState clientState) {
		System.out.println("sendState");
		client.sendTCP(clientState);
	}

	public void connect() {

		new Thread("Connect") {
			public void run () {
				try {
					client.connect(5000, "localhost", port, port);
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
	
	public StadiumScreen getStadiumScreen() {
		return stadiumScreen;
	}

	public void setStadiumScreen(StadiumScreen stadiumScreen) {
		this.stadiumScreen = stadiumScreen;
	}
}
