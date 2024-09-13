package chat.dobot.dominio;

import chat.dobot.enums.Autor;

public record Mensagem(Autor autor, String conteudo) {
}
