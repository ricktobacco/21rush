package messages;

public class StartMessage extends JoinMessage {
	private int numPlayers;

	public StartMessage(String gamename, int numPlayers) {
		super(gamename);
		this.numPlayers = numPlayers;
	}
	public String getName() {
		return gamename;
	}
	public int getNumPlayers() {
		return numPlayers;
	}
}
