package exemplos.agendaconsultas;

import java.time.LocalDateTime;

public class ConsultaBuilder {

    private int id;
    private String paciente;
    private String medico;
    private LocalDateTime dataHora;

    public ConsultaBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Consulta build() {
        return new Consulta(id, paciente, medico, dataHora);
    }
}

