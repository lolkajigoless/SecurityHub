/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.securityhub;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao_DB {
    private static final String URL = "jdbc:mysql://localhost:3306/securityhub_db";
    private static final String USER = "root"; // seu usuário
    private static final String PASSWORD = ""; // sua senha
    
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
