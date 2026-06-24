/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.securityhub;

public class Usuario {
    private String nome;
    private String email;
    private String token;

    public Usuario(String nome, String email, String token) {
        this.nome = nome;
        this.email = email;
        this.token = token;
    }

    // getters e setters (se quiser)
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getToken() { return token; }
}