package casino;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import game.Table;
import game.Player;
import messages.JoinMessage;

public class GameRoom {
	private Socket s;
	private ServerSocket ss;
	//private Vector<Table> tables;
	private Vector<ServerThread> serverThreads;

	public static void main(String[] args) {
		System.out.println("Welcome to the 21Server!");
		InputHelper ih = new InputHelper();
		new GameRoom(ih.readIntBetween("Please enter a port", 1024, 49151));
	}
	private GameRoom(int port) {
		System.out.println("Binding to port " + port + "...");
		try {
			//tables = new Vector<>();
			ss = new ServerSocket(port);
			System.out.println("Started BlackJack server on localhost:" + port);

			serverThreads = new Vector<>();

			while (true) {
				s = ss.accept(); // blocking
				System.out.println("Connection from: " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("ioe in GameRoom constructor: " + ioe.getMessage());
		} finally {
			try {
				if (s != null) s.close();
				if (ss != null) ss.close();
			} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
			}
		}
	}
	void broadcast(JoinMessage m) {
		if (m != null) {
			for (ServerThread thread : serverThreads) {
				if (thread.getName.equals(m.getName()) {
				thread.sendMessage(m);
			}
		}
	}
	/*void addTable(String name, int numPlayers) {
		tables.add(new Table(name, numPlayers));
	}
	Table getTable(String name) {
		for (Table t: tables)
			if (t.getName().equals(name))
				return t;
		return null;
	}
	String gaveOver(String name) {
		Table t = getTable(name);
		return (t != null) ? t.gameOver() : null;
	}
	String[] roundup(String name) {
		Table t = getTable(name);
		return (t != null) ? t.roundup() : null;
	}
	Player[] getPlayers(String table) {
		Table t;
		if ((t = getTable(table)) != null)
			return t.getPlayers();
		return null;
	}
	Player getPlayer(String table, String name) {
		Table t;
		if ((t = getTable(table)) != null)
			return t.getPlayer(name);
		return null;
	}
	Player getNextPlayer(String table, String name) {
		Table t;
		if ((t = getTable(table)) != null)
			return t.getNextPlayer(name);
		return null;
	}
	Player addPlayer(String table, String name) {
		Table t;
		if ((t = getTable(table)) != null)
			if (t.addPlayer(name))
				return t.getPlayer(name);
		return null;
	}
}
*/
