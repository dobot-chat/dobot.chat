package chat.dobot.bot.domain;

import chat.dobot.bot.Autor;

public record Mensagem(Autor autor, String conteudo) {
}
