package com.mychat2.dominio;

import com.mychat2.enums.Autor;

public record Mensagem(Autor autor, String conteudo) {
}
