package game;

import java.util.Map;
import java.util.HashMap;


public class Table {
	private Dealer dealer;
	private int everyone; //player count
	private Player[] players;
	private String name = "";
	
	public String getName() { return name; }
	public int getEveryone() { return everyone; }
	public Player[] getPlayers() { return players; }
	
	public Table (String name, int numPlayers) {
		this.name = name;
		dealer = new Dealer("Dealer");
		players = new Player[numPlayers]; //fixed length to define max player count
	}
	public void reset() {
		dealer.reset();
		for (Player p: players) {
			p.reset();
			p.hand.addCard(dealer.getCard());
			p.hand.addCard(dealer.getCard());
		}
	}
	public void setBet(String username, int amount) {
		Player p = getPlayer(username);
		if (p != null) {
			if (amount <= p.getBalance())
				p.setBet(amount);
		}
	}
	public String toString() {
		String result = dealer.toString();
		for (Player p: players)
			result += p.toString();
		return result;
	}
	public boolean addPlayer(String username) {
		if (players.length == everyone) return false;
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				players[i] = new Player(username);
				//when added to game each player gets two cards
				players[i].hand.addCard(dealer.getCard());
				players[i].hand.addCard(dealer.getCard());
				everyone++;
				return true;
			} else
				if (players[i].getUsername().equals(username))
					return false;
		}
		return false;
	}
	public String dealCard(String username) {
		Player p = getPlayer(username);
		if (p != null) {
			Card c = dealer.getCard();
			p.hand.addCard(c);
			return c.toString();
		}
		return null;
	}
	public Player getPlayer(String username) {
		for (Player p: players)
			if (p != null)
				if (p.getUsername().equals(username))
					return p;
		return null;
	}
	public Player getNextPlayer(String username) {
		for (int i = 0; i < players.length; i++)
			if (players[i] != null)
				if (players[i].getUsername().equals(username))
					if (i + 1 < players.length)
						if (players[i + 1] != null)
							return players[i + 1];
		return null;
	}
	public String[] roundup() {
		String dstr = dealer.play();
		String result[] = new String[1 + players.length * 2];
		result[0] = "It is now time for the dealer to play.\nThe dealer hit "
				  + dstr.charAt(0) + ((dstr.charAt(0) == '1') ? " time. " : " times. ")
				  + ((dstr.length() > 1) ? "They were dealt:" + dstr.substring(1) : "")
				  + dealer.toString();
		for (int i = 1; i < players.length + 1; i++) {
			result[(i * 2) - 1] = players[i - 1].getUsername();
			switch (players[i - 1].getState(dealer.hand.value())) {
				case -3:
					result[i * 2] = " tied dealer's blackjack. Total remains the same at "
							+ players[i - 1].getBalance() + " chips.";
				break;
				case -1:
					players[i - 1].lose();
					result[i * 2] = " busted. "
							+ players[i - 1].getBet() + " chips were lost.";
				break;
				case 0:
					players[i - 1].lose();
					result[i * 2] = " had a sum worse than the dealer's. "
							+ players[i - 1].getBet() + " chips were lost.";
				break;
				case 1:
					result[i * 2] = " tied with the dealer. Total remains the same at "
							+ players[i - 1].getBalance() + " chips.";
				break;
				case 2:
					players[i - 1].win();
					result[i * 2] = " had a sum better than the dealer's. "
							+ players[i - 1].getBet() + " chips were won.";
				break;
				case 3:
					players[i - 1].win(); players[i - 1].win();
					result[i * 2] = " beat the dealer with blackjack. "
							 + players[i - 1].getBet()*2 + " chips were won.";
				default: break;
			}
		}
		return result;
	}
 	public String gameOver() {
		Map<String, Integer> stats =  new HashMap<>();
		String losers[]  = new String[players.length];
		String winners[] = new String[players.length];

		stats.put("max", 0);
		stats.put("wins", 0);
		stats.put("done", 0);

		for (Player p: players) {
			if (p.getBalance() == 0) {
				losers[stats.get("done")] = p.getUsername();
				stats.put("done", stats.get("done") + 1);
			}
			else {
				if (p.getBalance() >= stats.get("max")) {
					if (p.getBalance() > stats.get("max")) {
						stats.put("wins", 0);
						stats.put("max", p.getBalance());
					}
					winners[stats.get("wins")] = p.getUsername();
					stats.put("wins", stats.get("wins") + 1);
				}
			}
		}
		if (stats.get("done") == 0) {
			return null;
		} else if (stats.get("done") == everyone) {
			return "Dealer won!";
		} else {
			if (stats.get("wins") == 1)
				return (winners[0] + " wins!");
			else {
				String ties = "Win tied between ";
				for (int i = 0; i < stats.get("wins"); i++) {
					ties += winners[i];
					if ((i + 1) < stats.get("wins"))
						ties += " and ";
				}
				return ties;
			}
		}
	}
}
