package game;

public class Hand {
	int lastCard;
	Card[] cards;
	Hand() {
		//in the worst case for size, a hand may hold at most 12 cards
		lastCard = -1;
		cards = new Card[12];
	}
	public String toString() {
		String result = "Cards:";
		for (Card c : cards)
			if (c != null)
				result += c.toString();
		return result + "\n";
	}
	public int value() {
		int aces = 0;
		int total = 0;
		for (Card c : cards)
			if (c != null) {
				if (c.value() == 1)
					aces++;
				total += c.value();
			}
		if (aces > 0)
			total += (total + 10 < 22) ? 10 : 0;
		return total;
	}
	Card addCard(Card c) {
		return cards[++lastCard] = c;
	}
	void emptyHand() {
		lastCard = -1;
		cards = new Card[12];
	}
	Card getCard() {
		return (lastCard < 0) ? null : cards[lastCard--];
	}
	Card getCard(int index) {
		return (index > lastCard) ? null : cards[index];
	}
}
