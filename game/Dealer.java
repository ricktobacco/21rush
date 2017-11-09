package game;


public class Dealer extends Player {
	private Deck deck;
	public String toString() {
		String dashes  =  "-------------------------------------------------------\n";
		String handstr = (hand.lastCard != 0) ? "Status: " + hand.value() + "\n" : "";
		handstr += (hand.lastCard == 0) ? "Cards: | ?" + hand.getCard(0) + "\n" : hand;
		return ("\n" + dashes + "DEALER\n\n" + handstr + dashes);
	}
	Dealer(String username) {
		super(username);
		deck = new Deck();
		hand.addCard(getCard());
	} Card getCard() {
		return deck.getCard();
	}
	String play() { int hits = -1;
		Card c; String cards = "";
		do { c = hand.addCard(getCard());
			 cards += (++hits > 0) ? c.toString() : "";
		} while (hand.value() < 17);
		return hits + cards;
	}
	void reset() {
		super.reset();
		deck.shuffle();
		hand.addCard(getCard());
	}
}
