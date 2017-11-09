package messages;

public class BetMessage extends JoinMessage {
    private String username;
    private int amount;

    public BetMessage(String gamename, String username, int amount) {
        super(gamename);
        this.username = username;
        this.amount = amount;
    }
    public String getUsername() {
        return username;
    }
    public int getAmount() {
        return amount;
    }
}