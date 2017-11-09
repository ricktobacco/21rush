package messages;

public class GameMessage extends JoinMessage {
    private String username;
    private String message;

    public GameMessage(String gamename, String username, String message) {
        super(gamename);
        this.username = username;
        this.message = message;
    }
    public String getUsername() {
        return username;
    }
    public String getMessage() {
        return message;
    }
}