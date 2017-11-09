package casino;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.*;

public class GameClient extends Thread {
	private Socket s;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private InputHelper ih;
	private String username;
	private String table;

	private GameClient() { try {
		ih = new InputHelper();		connectToGameRoom();
		ois = new ObjectInputStream(s.getInputStream());
		oos = new ObjectOutputStream(s.getOutputStream());
		table = ""; 				 JoinMessage message;
		if (ih.readIntBetween("Please choose from the options below:\n1) Start Game\n2) Join Game" , 1 , 2) == 1)
			message = new StartMessage(ih.readString("Please choose a name for your game"),
									   ih.readIntBetween("Please choose the number of players in the game (1-3)", 1 , 3));
		else
			message = new JoinMessage (ih.readString("Please enter the name of the game you wish to join"));
			oos.writeObject(message); oos.flush();
			this.start(); //before you start writing to the server, start listening to it: start will cause run() to init
		} catch (IOException ioe) { System.out.println("Unable to connect to server with provided fields"); }
	}
	private void connectToGameRoom() { try { s = new Socket(ih.readString("Please enter the ipaddress"),
							 								ih.readIntBetween("Please enter a port", 1024, 49151)); }
		catch (IOException ioe){ System.out.println("Unable to connect to server with provided fields"); connectToGameRoom(); }
	}
	public static void main (String[] args) { System.out.println("Welcome to Black Jack!"); new GameClient(); }
	public void run() { try {
		while (true) { JoinMessage message = (JoinMessage) ois.readObject();
			if (!table.equals("") && !message.getName().equals(table)) continue;
			if (message.getClass().equals(StartMessage.class)) {
				if (message.getName().equals("")) { System.out.println("Invalid choice. This game name has already been chosen by another user");
					oos.writeObject(new StartMessage(ih.readString("Please choose a name for your game"),
					ih.readIntBetween("Please choose the number of players in the game (1-3)", 1 , 3)));
				} else oos.writeObject(new BetMessage(table = message.getName(), username = ih.readString("Please choose a username"), -1));
				oos.flush();
			} else if (message.getClass().equals(JoinMessage.class)) {
				//the client gets a join message back from the server thread
				//the name of the game to be joined is empty string because
				//the server thread went to the game room and called hasTable
				//to see if any table exists with the name, and to no success
				if (message.getName().equals("")) { System.out.println("Invalid choice. There are no ongoing games with this name");
					 oos.writeObject(new JoinMessage(ih.readString("Please choose a name for your game")));
				} //if the table was found, we need to make sure the client picks a username that hasn't been picked yet
				else oos.writeObject(new BetMessage(table = message.getName(), username = ih.readString("Please choose a username"), -1));
				oos.flush();
			} else if (message.getClass().equals(BetMessage.class) && message.getName().equals(table)) {
				BetMessage m = (BetMessage) message; //game already has this username, ask for another one
				if (m.getUsername().equals("")) { oos.writeObject(new BetMessage(m.getName(), username = ih.readString
						("Invalid choice. This username has already" +
								" been chosen by another player in this game\n" +
								"Please choose a username"), -1)); oos.flush();
				} else if (m.getUsername().equals(" ")) { //space means there is none, the game is full, ask for another game
					System.out.println("This game is full.");
					oos.writeObject(new JoinMessage(ih.readString("What is the name of a game you wish to join ?")));
					oos.flush();
				} else if (m.getUsername().equals(username)) {
					//bet messages are broadcast by the threads, so we need to know if one is referring to us
					//if the amount is less than zero its just a print message that we dont need to print
					//to ourselves because we tell ourselves how much we just bet right after we do
					if (m.getAmount() < 0) {
						int amount = ih.readIntBetween(username + ", it is your turn to make a bet."
								   + " Your chip total is " + -m.getAmount(), 1, -m.getAmount());
						System.out.println("You bet " + amount + " chips");
						oos.writeObject(new BetMessage(m.getName(), username, amount));
						oos.flush();
					}
				} else { if (m.getAmount() == 0) System.out.println("It is " + m.getUsername() + "'s turn to make a bet.");
				  else if (m.getAmount() > 0)  System.out.println(m.getUsername() + " bet " + m.getAmount() + " chips"); }
			} else if (message.getClass().equals(GameMessage.class) && message.getName().equals(table)) {
				GameMessage m = (GameMessage)message;
				if (m.getUsername().equals(username)) {
					if (m.getMessage().equals("")) { oos.writeObject(new GameMessage(m.getName(), m.getUsername(),
								String.valueOf(ih.readBoolean("It is your turn to add cards to your hand")))); oos.flush(); //TODO: flush
					} else { //because "you stayed" or "you bust" ends with a period and after that we dont want to ask for more actions
						if (m.getMessage().endsWith(".")) System.out.println("You" + m.getMessage());
						else if (m.getMessage().endsWith("!")) System.out.println(m.getMessage());
						else {
							oos.writeObject(new GameMessage(m.getName(), username,
									String.valueOf(ih.readBoolean("You" + m.getMessage()))));
							oos.flush(); //TODO: flush
						}
					}
				} else if (m.getUsername().equals("")) {
					System.out.println(m.getMessage());
					if (m.getMessage().endsWith("#")) break;
				} else {
					if (m.getMessage().equals(""))
						System.out.println("It is " + m.getUsername() + "'s turn to add cards to their hand.");
						//special case for not displaying broadcasts specific to first play (who joined game)
					else if (!(m.getMessage().endsWith("!"))) System.out.println(m.getUsername() + m.getMessage());
				}
			}
		}
		} catch (IOException ioe) { System.out.println("ioe in GameClient.run(): " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) { System.out.println("cnfe: " + cnfe.getMessage());
		} finally {
			  try { if (ois != null) ois.close(); if (oos != null) oos.close(); if (s != null) s.close();
			  } catch (IOException ioe) { System.out.println(ioe.getMessage()); }
		}
	}
}
