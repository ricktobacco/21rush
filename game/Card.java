package game;

public class Card {
	enum Rank {
		ACE,
		TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
		JACK, QUEEN, KING
	}
	enum Suit {    
		CLUBS,
		SPADES,
		HEARTS,
		DIAMONDS
	}
    private final Rank rank;
    private final Suit suit;
    Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }
    int value() {
    	switch(rank) {
    		case ACE: return 1;
    		case TWO: return 2;
    		case THREE: return 3;
    		case FOUR: return 4;
    		case FIVE: return 5;
    		case SIX: return 6;
    		case SEVEN: return 7;
    		case EIGHT: return 8;
    		case NINE: return 9;
    		case TEN: return 10;
    		case JACK: return 10;
    		case QUEEN: return 10;
    		case KING: return 10;
    	}
    	return 0;
    }
    public String toString() { return " | " + rank + " of " + suit; }
}
