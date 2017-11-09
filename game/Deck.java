package game;

class Deck extends Hand {
	Deck() {
		cards = new Card[52];
		fill();
		shuffle();
	}
	private void fill() {
		int c = 0;
		for (Card.Suit s : Card.Suit.values())
			for (Card.Rank r : Card.Rank.values())
				cards[c++] = new Card(r, s);
	}
	void shuffle() {
    	// Put all the used cards back into the deck, and shuffle it into random order.
		lastCard = 51;
		for (int i = 51; i > 0; i--) {
            int rnd = (int) (Math.random() * (i + 1));
            Card tmp = cards[i];
            cards[i] = cards[rnd];
            cards[rnd] = tmp;
        }
    }
}