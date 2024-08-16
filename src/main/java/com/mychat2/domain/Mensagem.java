package com.mychat2.domain;

import com.mychat2.enums.Autor;

public record Mensagem(Autor autor, String conteudo) {
}
