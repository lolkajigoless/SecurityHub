/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.securityhub;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {

    private static final String USERNAME = "securitiy.hub@gmail.com"; // seu email aqui
    private static final String PASSWORD = "dyyv cwog yshe xsqv"; // senha ou app password

    public static void enviarEmail(String destinatario, String assunto, String mensagem) throws MessagingException {
        // Configurações do servidor SMTP Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); 
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Autenticação
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(USERNAME));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        msg.setSubject(assunto);
        msg.setText(mensagem);

        Transport.send(msg);
    }
    
}


