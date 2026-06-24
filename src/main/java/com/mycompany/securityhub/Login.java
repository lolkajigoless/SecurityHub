package com.mycompany.securityhub;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javax.mail.MessagingException;

public class Login {
    
    // conjunto de caracteres que podem ser usados no token (você pode incluir símbolos se quiser mais entropia).
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TOKEN_LENGTH = 15;
    private static final SecureRandom random = new SecureRandom(); // fornece uma fonte segura de números aleatórios.
   
    
    @FXML
    private Button Recuperar_Token;
    @FXML
    private Button btnVoltar;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField token;
    @FXML
    private TextField ColocarToken;
    @FXML
    private TextField ColocarEmailRec;
    @FXML
    private TextField colocar_codigo;
    
    
    @FXML
    private void RecuperarToken() throws IOException {
        App.setRoot("recuperar_token");
        
    }
        
    @FXML    
    private void Voltar() throws IOException {
    App.setRoot("primary");
    }
    
    public boolean validarEmail(String email) {
    // Regex simples para validar e-mails
    String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    return email.matches(emailRegex);
    }
    
    private void mostrarAlerta(String titulo, String mensagem) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(titulo);
    alert.setContentText(mensagem);
    alert.showAndWait();
    }
    
    public void mostrarAlertaSucesso(String titulo, String mensagem) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(null); // sem cabeçalho
    alert.setContentText(mensagem);

    // Opcional: mudar o ícone do diálogo para um "check" verde, se quiser pode colocar um ícone personalizado
    // alert.getDialogPane().getScene().getWindow().setOnCloseRequest(e -> ...);

    alert.showAndWait();
    }
    
    @FXML
 private void registrarUsuario() {
    String nome = txtNome.getText();
    String email = txtEmail.getText();
    String TOken = token.getText();

     if (nome.isEmpty() || email.isEmpty() || TOken.isEmpty()) {
        mostrarAlerta("Erro", "Preencha todos os campos!");
        return;
    }

    if (!validarEmail(email)) {
        mostrarAlerta("Erro", "Email inválido! Por favor, insira um email válido.");
        return;
    }

    try (Connection con = Conexao_DB.conectar()) {
        String sql = "INSERT INTO usuarios (nome, email, token) VALUES (?, ?, ?)";
        var stmt = con.prepareStatement(sql);
        stmt.setString(1, nome);
        stmt.setString(2, email);
        stmt.setString(3, TOken);
        stmt.executeUpdate();

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Registro");
        alerta.setHeaderText(null);
        alerta.setContentText("Usuário registrado com sucesso!");
        alerta.showAndWait();
    } catch (SQLException e) {
        Alert erro = new Alert(Alert.AlertType.ERROR);
        erro.setTitle("Erro");
        erro.setHeaderText("Erro ao registrar usuário");
        erro.setContentText(e.getMessage());
        erro.showAndWait();
    }
}
    
    public static String GerarToken(){
    
        StringBuilder Token = new StringBuilder(TOKEN_LENGTH); // Cria um StringBuilder com capacidade inicial de 15

        
        for (int i = 0;i < TOKEN_LENGTH; i++){
            int index = random.nextInt(CHARACTERS.length());  // Pega um índice aleatório do conjunto de caracteres
            Token.append(CHARACTERS.charAt(index)); // Adiciona o caractere correspondente ao token
        }
        
        return Token.toString(); // Retorna o token como string    
    }
    
    public void atualizarToken() {
    token.setText(GerarToken());
    }
    
    @FXML
    private void loginComToken() {
    String TOOken = ColocarToken.getText();

    if (TOOken == null || TOOken.isEmpty()) {
        mostrarAlerta("Erro", "Por favor, insira o token.");
        return;
    }

    UsuarioDAO usuarioDAO = new UsuarioDAO();
    try {
        Usuario usuario = usuarioDAO.buscarPorToken(TOOken);
        if (usuario != null) {
            // Login OK! Pode ir para a próxima tela, passar o usuário, etc.
           mostrarAlertaSucesso("Sucesso", "Bem-vindo, " + usuario.getNome() + "!");
           Sessao.setUsuario(usuario);
            
            // Exemplo: carregar a tela principal
            App.setRoot("Tela_Principal");
        } else {
            mostrarAlerta("Erro", "Token inválido.");
        }
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        mostrarAlerta("Erro", "Erro ao tentar fazer login: " + e.getMessage());
    }
}
    
    
    @FXML
    private void continuar()throws IOException{
    App.setRoot("Tela_Principal");
    }
    
    @FXML
    private void registro() throws IOException {
    App.setRoot("Registrar");
    }
    
  private String codigoVerificacao;
private String emailUsadoNoEnvio;

@FXML
private void enviarCodigoParaEmail() {
    String email = ColocarEmailRec.getText();

    if (email == null || email.isEmpty()) {
        mostrarAlerta("Erro", "Por favor, insira o e-mail.");
        return;
    }

    Usuario usuario;
    try {
        usuario = new UsuarioDAO().buscarPorEmail(email); // DAO que busca no banco
    } catch (SQLException e) {
        e.printStackTrace();
        mostrarAlerta("Erro", "Erro ao acessar o banco de dados.");
        return;
    }

    if (usuario == null) {
        mostrarAlerta("Erro", "Esse e-mail não está registrado.");
        return;
    }

    // Gerar código 6 dígitos e salvar e-mail
    codigoVerificacao = String.format("%06d", new Random().nextInt(999999));
    emailUsadoNoEnvio = email;

    String assunto = "Seu código de verificação";
    String mensagem = "Olá, " + usuario.getNome() + "! Seu código de verificação é: " + codigoVerificacao;

    try {
        EmailUtil.enviarEmail(email, assunto, mensagem);
        mostrarAlertaSucesso("Código enviado", "O código foi enviado para seu e-mail.");
    } catch (MessagingException e) {
        e.printStackTrace();
        mostrarAlerta("Erro", "Erro ao enviar e-mail: " + e.getMessage());
    }
}

@FXML
private void verificarCodigoDigitado() {
    String codigoDigitado = colocar_codigo.getText();

    if (codigoDigitado == null || codigoDigitado.isEmpty()) {
        mostrarAlerta("Erro", "Por favor, digite o código.");
        return;
    }

    if (codigoVerificacao == null || emailUsadoNoEnvio == null) {
        mostrarAlerta("Erro", "Código ou e-mail não definidos. Envie o código novamente.");
        return;
    }

    if (codigoDigitado.equals(codigoVerificacao)) {
        try {
            Usuario usuario = new UsuarioDAO().buscarPorEmail(emailUsadoNoEnvio);

            if (usuario != null) {
                mostrarAlertaSucesso("Sucesso", "Email confirmado!\nToken: " + usuario.getToken() + "\nNao esqueca de escrever esse token em um papel fisico para nao perder 😁");

                // Aqui você pode redirecionar para a próxima tela, usando o token
            } else {
                mostrarAlerta("Erro", "Usuário não encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao buscar o token.");
        }

    } else {
        mostrarAlerta("Erro", "Código incorreto. Tente novamente.");
    }
}

    
    
    
}


