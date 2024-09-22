package chat.dobot.bot;

public class DoBotException extends RuntimeException {


    public DoBotException(String message) {
        super(message);
    }

    public DoBotException(String message, Exception e) {
        super(message, e);
    }
}
