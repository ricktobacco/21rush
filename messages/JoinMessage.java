package messages;

import java.io.Serializable;

public class JoinMessage implements Serializable {
    protected static final long serialVersionUID = 1L;
    protected String gamename;

    public JoinMessage(String gamename) {
        this.gamename = gamename;
    }
    public String getName() {
        return gamename;
    }
}
