/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.securityhub;
import com.mycompany.securityhub.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

   public Usuario buscarPorToken(String token) throws SQLException {
    String sql = "SELECT * FROM usuarios WHERE token = ?";
    try (Connection con = Conexao_DB.conectar();
         PreparedStatement stmt = con.prepareStatement(sql)) {

        stmt.setString(1, token);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String nome = rs.getString("nome");
            String email = rs.getString("email");
            return new Usuario(nome, email, token);
        } else {
            return null; // token inválido
        }
    }
}
  public Usuario buscarPorEmail(String email) throws SQLException {
    String sql = "SELECT * FROM usuarios WHERE email = ?";
    try (Connection con = Conexao_DB.conectar();
         PreparedStatement stmt = con.prepareStatement(sql)) {

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String nome = rs.getString("nome");
            String token = rs.getString("token");
            return new Usuario(nome, email, token);
        } else {
            return null; // e-mail não encontrado
        }
    }
}   
 
}