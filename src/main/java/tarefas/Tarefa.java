package tarefas;

import com.mychat2.annotations.Entidade;

@Entidade
public record Tarefa(int id, String descricao) {}
