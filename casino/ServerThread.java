
package casino;

import messages.*;
import game.Player;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServerThread extends Thread {
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String username;
	private String table;
	private GameRoom gr;
	private int round;

	ServerThread(Socket s, GameRoom gr) {
		try { this.gr = gr; table = ""; round = 1;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}
	void sendMessage(JoinMessage message) {
		try { oos.writeObject(message);
			  oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	public void run() { try  {
			while (true) { JoinMessage message = (JoinMessage)ois.readObject();
			if (!table.equals("") && !message.getName().equals(table)) continue;
			if (message.getClass().equals(StartMessage.class)) {
				StartMessage m = (StartMessage) message;
				//if the client is trying to start a game and a game with the given name
				//already exists, then send him back a message of the same type
				if (gr.getTable(m.getName()) != null)
					sendMessage(new StartMessage("", 0));
				else {
					gr.addTable(table = m.getName(), m.getNumPlayers());
					sendMessage(m);
				}
			}
			else if (message.getClass().equals(JoinMessage.class)) {
				//the table has a game with the name that the user wants, success, send message back
				if (gr.getTable(message.getName()) != null) {
					if (gr.getPlayers(message.getName()).length > gr.getTable(message.getName()).getEveryone()) {
						table = message.getName();
						sendMessage(message);
					} else sendMessage(new JoinMessage(" "));
				//failure, send message back with empty name telling client they should give a new name
				} else sendMessage(new JoinMessage(""));
			//failure, send message back with empty name telling client they should give a new name
			} else if (message.getClass().equals(BetMessage.class)) {
				BetMessage m = (BetMessage)message;
				//a bet amount of minus one is simply the message to attempt registering a username
				if (m.getAmount() == -1) {
					//if we know the game we are going to try to join
					username = m.getUsername(); //the username we are attempting to register
					if (gr.addPlayer(table, username) == null) { //a new table will not have the player by definition, only an existing one can
						sendMessage(new BetMessage(table," ", -1)); //sending a message with 'space' username signals the client that the game is full
						sendMessage(new BetMessage(table,"", -1)); //sending a message with empty username signals the client to attempt getting a new one
					} else {
						Player[] players = gr.getPlayers(table);
						//is this the last player to join the game?
						if (players[players.length - 1] != null) {
							if (players[players.length - 1].getUsername().equals(username)) {
								//tell the first player (initiator of the game) that we joined
								gr.broadcast(new GameMessage(table, players[0].getUsername(), username + " joined the game!"));
								gr.broadcast(new GameMessage(table,"", "Let the game commence. Good luck to all the players!" +
								/* tell everyone that its the player's turn to bet by setting the amount to 0 */ "\n\nDealer is shuffling cards..."));
								/* tell everyone that the game is ready to start */
								gr.broadcast(new BetMessage(table, players[0].getUsername(), 0));
								//get first bet by setting the amount (the max the player may bet) to less than zero
								//because otherwise it would conflict with the betmessage where it's greater than zero
								//and print to everyone that the player's bet everything that they have
								gr.broadcast(new BetMessage(table, players[0].getUsername(), -players[0].getBalance()));
							}
						} else {
							//check if the player just added was the first player to join the game
							if (players[0].getUsername().equals(username)) {
								//the message doesn't need to broadcast since we know that there is only one person to hear it anyway
								//we can just use sendMessage to talk to him
								sendMessage(new GameMessage(table, username, "Waiting for " + (players.length - 1) + " other players to join!"));
							} else {
								sendMessage(new GameMessage(table, username, "The game will start shortly. Waiting for other players to join!"));
								//notifications for the first player
								gr.broadcast(new GameMessage (table, players[0].getUsername(), username + " joined the game!"));
								gr.broadcast(new GameMessage (table, players[0].getUsername(), "Waiting for " +
											(players.length - gr.getTable(table).getEveryone()) + " more!"));
							}
						}
					}
				} else { Player p = gr.getPlayer(table, m.getUsername());
					if  (p.getUsername().equals(username)) {
						gr.getTable(table).setBet(username, m.getAmount());
						gr.broadcast(new BetMessage(table, username, m.getAmount())); //tell everyone that he made a bet
						//if there needs more players to get bets;
						if ((p = gr.getNextPlayer(table, username)) != null) {
							gr.broadcast(new BetMessage(table, p.getUsername(), 0));
							gr.broadcast(new BetMessage(table, p.getUsername(), -p.getBalance()));
						} else {
							gr.broadcast(new GameMessage(table,"", gr.getTable(table).toString())); //start the game
							gr.broadcast(new GameMessage(table, gr.getTable(table).getPlayers()[0].getUsername(), ""));
						}
					}
				}
			} else if (message.getClass().equals(GameMessage.class)) {
				GameMessage m = (GameMessage)message;
				if (m.getUsername().equals(username)) {
					Player p = gr.getPlayer(table, username);

					boolean keepPlaying = true;
					if (m.getMessage().startsWith("t")) { //hit
						String card = gr.getTable(table).dealCard(username);
						if (p.getHand().value() < 21)
							gr.broadcast(new GameMessage(table, username, " got hit with the " + card));
						else {
							if (p.getHand().value() > 21) {
								gr.broadcast(new GameMessage(table, username, " busted."));
								gr.broadcast(new GameMessage(table,"", p.toString()));
								keepPlaying = false;
							} else if (p.getHand().value() == 21) {
								gr.broadcast(new GameMessage(table, username, " got blackjack."));
								gr.broadcast(new GameMessage(table,"", p.toString()));
								keepPlaying = false;
							}
						}
					} else if (m.getMessage().startsWith("f")) { //stand
						gr.broadcast(new GameMessage(table, username, " stayed."));
						gr.broadcast(new GameMessage(table,"", p.toString()));
						keepPlaying = false;
					}
					if (!keepPlaying) {
						if ((p = gr.getNextPlayer(table, username)) != null)
							gr.broadcast(new GameMessage(table, p.getUsername(), ""));
						else {
							String state[] = gr.roundup(table);
							String endgame = gr.gaveOver(table);
							gr.broadcast(new GameMessage(table, "", state[0]));
							for (int i = 1; i < state.length; i += 2)
								gr.broadcast(new GameMessage(table, state[i], state[i + 1]));
							if (endgame == null) {
								gr.getTable(table).reset();
								Player firstPlayer = gr.getTable(table).getPlayers()[0];
									gr.broadcast(new GameMessage(table,"", "\nROUND " + ++round + "\nDealer is shuffling cards..."));
									gr.broadcast(new BetMessage(table, firstPlayer.getUsername(), 0));
									gr.broadcast(new BetMessage(table, firstPlayer.getUsername(), -firstPlayer.getBalance()));
							} else  gr.broadcast(new GameMessage(table, "", "\n# GAME OVER #")); break; }
						}
					}
				}
			}
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread.run(): ioe " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("ioe in ServerThread.run() cnfe: " + cnfe.getMessage());
		} finally { try {
				if (ois != null) ois.close();
				if (oos != null) oos.close();
		} catch (IOException ioe) { System.out.println(ioe.getMessage()); }
		}
	}
}
