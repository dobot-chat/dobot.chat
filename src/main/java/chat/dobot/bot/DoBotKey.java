package chat.dobot.bot;

import chat.dobot.bot.service.DoBotService;
import io.javalin.config.Key;

import java.util.Map;

public enum DoBotKey {
    SERVICE(new Key<Map<String, DoBotService<Record>>>("service"));

    private final Key<?> k;

    <T> DoBotKey(Key<T> key) {
        this.k = key;
    }

    public <T> Key<T> key() {
        @SuppressWarnings("unchecked")
        Key<T> typedKey = (Key<T>) this.k;
        return typedKey;
    }
}
