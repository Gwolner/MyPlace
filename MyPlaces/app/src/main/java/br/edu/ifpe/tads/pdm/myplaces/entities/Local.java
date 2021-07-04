package br.edu.ifpe.tads.pdm.myplaces.entities;

public class Local {
    private String nome;
    private String lat;
    private String lng;
    private String cidade;
    private String estado;
    private AvaliacoesLocal avaliacao;
    private String observacao;
    private CategoriasLocal categoria;
    private String diretorioFotos; //atributo que armazena o caminho do diretório das fotos do local.

    public Local(String nome, String lat, String lng, String cidade, String estado,
                 AvaliacoesLocal avaliacao, String observacao, CategoriasLocal categoria){
        this.nome = nome;
        this.lat = lat;
        this.lng = lng;
        this.cidade = cidade;
        this.estado = estado;
        this.avaliacao = avaliacao;
        this.observacao = observacao;
        this.categoria = categoria;
        //comando para criar diretório para fotos com o nome do local
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public AvaliacoesLocal getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(AvaliacoesLocal avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getDiretorioFotos() {
        return diretorioFotos;
    }

    public void setDiretorioFotos(String diretorioFotos) {
        this.diretorioFotos = diretorioFotos;
    }

    public CategoriasLocal getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriasLocal categoria) {
        this.categoria = categoria;
    }
}
