package com.mycompany.securityhub;

public class Senha {
    private String servico;
    private String descricao;
    private String senha;

    public Senha(String servico, String descricao, String senha) {
        this.servico = servico;
        this.descricao = descricao;
        this.senha = senha;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return servico + ";" + descricao + ";" + senha;
    }

    public static Senha fromString(String linha) {
        String[] partes = linha.split(";", 3);
        if (partes.length == 3) {
            return new Senha(partes[0], partes[1], partes[2]);
        }
        return null;
    }
}
