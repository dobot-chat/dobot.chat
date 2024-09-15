package chat.dobot.dominio;

import chat.dobot.app.Autor;

public record Mensagem(Autor autor, String conteudo) {
}
