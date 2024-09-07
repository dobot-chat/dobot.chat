package exemplos.gerenciadortarefas;

import com.mychat2.anotacoes.Entidade;
import com.mychat2.anotacoes.Id;

@Entidade
public record Tarefa(@Id int id, String descricao) {}
