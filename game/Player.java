package game;

public class Player {
	private String username;
	private int chips;
	private int bet;

	Hand hand;
	Player(String name) {
		chips = 500;
		username = name;
		hand = new Hand();
	}
	public Hand getHand() { return hand; }
	public int getBalance() {
		return chips;
	}
	public String getUsername() {
		return username;
	}
	public String toString() {
		String dashes = "-------------------------------------------------------\n";
		String result = dashes;
		result += "Player: " + username + "\n\n";
		result += "Status: " + hand.value();
		if (hand.value() == 21) result += " - blackjack";
		else if (hand.value() > 21) result += " - bust";
		result += "\n" + hand.toString();
		result += "Chip Total: " + chips + " | Bet Amount: " + bet + "\n";
		return result + dashes;
	}
	void win() { chips += bet; }
	void lose() {
		chips -= bet;
	}
	void reset() { hand.emptyHand(); }
	void setBet(int amt) { bet = amt; }

	int getBet() { return bet; }
	int getState(int dealerValue) {
		if (hand.value() > 21) {
			return -1; //busted
		} else {
			if (hand.value() == 21)
				return (dealerValue == 21) ? -3 : 3;
			//both player and dealer blackjack is unlucky -3
			else if (hand.value() < dealerValue)
				return (dealerValue > 21) ? 2 : 0;
			else if (hand.value() == dealerValue)
				return 1;
			return 2;
		}
	}
}
