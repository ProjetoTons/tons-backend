package br.com.tonspersonalizados.dto.dashboard;

public class PerformanceFuncionarioDto {
    private Long idFuncionario;
    private String nomeFuncionario;
    private TarefasDto tarefas;

    public PerformanceFuncionarioDto(Long idFuncionario, String nomeFuncionario, TarefasDto tarefas) {
        this.idFuncionario = idFuncionario;
        this.nomeFuncionario = nomeFuncionario;
        this.tarefas = tarefas;
    }

    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long idFuncionario) { this.idFuncionario = idFuncionario; }
    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }
    public TarefasDto getTarefas() { return tarefas; }
    public void setTarefas(TarefasDto tarefas) { this.tarefas = tarefas; }

    public static class TarefasDto {
        private int design;
        private int producao;
        private int embalagem;
        private int logistica;

        public TarefasDto(int design, int producao, int embalagem, int logistica) {
            this.design = design;
            this.producao = producao;
            this.embalagem = embalagem;
            this.logistica = logistica;
        }

        public int getDesign() { return design; }
        public void setDesign(int design) { this.design = design; }
        public int getProducao() { return producao; }
        public void setProducao(int producao) { this.producao = producao; }
        public int getEmbalagem() { return embalagem; }
        public void setEmbalagem(int embalagem) { this.embalagem = embalagem; }
        public int getLogistica() { return logistica; }
        public void setLogistica(int logistica) { this.logistica = logistica; }
    }
}
