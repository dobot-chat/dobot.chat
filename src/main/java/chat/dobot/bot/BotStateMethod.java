package chat.dobot.bot;

@FunctionalInterface
public interface BotStateMethod {
    void execute(Contexto contexto);
}