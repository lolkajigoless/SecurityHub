package com.mycompany.securityhub;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ControleRegistro {

    public void registrarUsuario(String email, String senhaLogin) {
        try {
            String senhaMestra = gerarSenhaMestraAleatoria();
            String senhaMestraCriptografada = criptografarSenhaMestra(senhaMestra, senhaLogin);

            System.out.println("Email: " + email);
            System.out.println("Senha de Login: " + senhaLogin);
            System.out.println("Senha Mestra (texto claro): " + senhaMestra);
            System.out.println("Senha Mestra Criptografada: " + senhaMestraCriptografada);

            // TODO: salvar no banco email, hashSenhaLogin, senhaMestraCriptografada

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String gerarSenhaMestraAleatoria() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public SecretKeySpec gerarChaveAES(String senhaBase) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] chave = sha.digest(senhaBase.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(chave, "AES");
    }

    public String criptografarSenhaMestra(String senhaMestra, String senhaLogin) throws Exception {
        SecretKeySpec key = gerarChaveAES(senhaLogin);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] textoCriptografado = cipher.doFinal(senhaMestra.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(textoCriptografado);
    }

    // Teste rápido
    public static void main(String[] args) {
        ControleRegistro cr = new ControleRegistro();
        cr.registrarUsuario("usuario@exemplo.com", "senhaDoUsuario123");
    }
}
