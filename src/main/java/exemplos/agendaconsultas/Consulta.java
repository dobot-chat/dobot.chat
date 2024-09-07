package exemplos.agendaconsultas;

import com.mychat2.anotacoes.Entidade;
import com.mychat2.anotacoes.Id;

import java.time.LocalDateTime;

@Entidade
public record Consulta(@Id int Id, String paciente, String medico, LocalDateTime dataHora) {
}
