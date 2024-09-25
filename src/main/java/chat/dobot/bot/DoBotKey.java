package chat.dobot.bot;

import chat.dobot.bot.domain.DoBot;
import chat.dobot.bot.service.DoBotService;
import io.javalin.config.Key;

import java.util.Map;

public enum DoBotKey {
    SERVICE(new Key<Map<String, DoBotService<Record>>>("service")),
    BOTS(new Key<Map<String, Map<String, DoBot>>>("bots")),;

    private final Key<?> k;

    <T> DoBotKey(Key<T> key) {
        this.k = key;
    }

    public <T> Key<T> key() {
        return (Key<T>) this.k;
    }
}
