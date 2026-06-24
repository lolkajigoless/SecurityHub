/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.securityhub;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
/**
 *
 * @author user
 */
public class PrincipalController {
    
     private static final String MAIUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMEROS = "0123456789";
    private static final String ESPECIAIS = "!@#$%^&*()-_=+[]{};:,.<>?";

    private static final String TODOS = MAIUSCULAS + MINUSCULAS + NUMEROS + ESPECIAIS;

    private static final int TAMANHO_SENHA = 16; // Você pode aumentar se quiser
    
    @FXML
    private Label lblBoasVindas;
    @FXML
    private TextField senhaForte;
    @FXML
    private TextField senha_Colocada_Forca;
    @FXML
    private TextArea Saida_Forca;
    @FXML
    private TextFlow textFlowUsuario;
    @FXML
    private Label labelEmail;
    @FXML
    private Button botaoMostrarEmail;

    
    private boolean emailVisivel = false;

    @FXML    
    private void Voltar() throws IOException {
    App.setRoot("Tela_Principal");
    }
    @FXML
    private void GerarSenha() throws IOException {
        App.setRoot("Gerar_Senha");     
    }
    @FXML
    private void VerificarForcaSenha() throws IOException {
        App.setRoot("VerificarForca_senha");     
    }
    @FXML
    private void MinhasSenhas() throws IOException {
        App.setRoot("GuardarAsSenhas");     
    }
    
    @FXML
    public void initialize() {
        if (!App.getFxmlAtual().equals("Tela_Principal")) {
        return;
        }
        
        Usuario usuario = Sessao.getUsuario();
        if (usuario != null) {
        Text texto1 = new Text("Olá, ");
        texto1.setFont(new Font("Arial", 14));
        Text nomeEmNegrito = new Text(usuario.getNome());
        nomeEmNegrito.setFont(new Font("Arial", 18));
        nomeEmNegrito.setStyle("-fx-font-weight: bold");
        textFlowUsuario.getChildren().addAll(texto1, nomeEmNegrito);
        labelEmail.setFont(new Font("Arial", 14));
        labelEmail.setText("*************"); // Oculta inicialmente
        botaoMostrarEmail.setText("Mostrar Email");
    }
}
    
    @FXML
    private void alternarEmail() {
    Usuario usuario = Sessao.getUsuario();
    if (usuario == null) return;

    if (emailVisivel) {
        labelEmail.setText("*************");
        botaoMostrarEmail.setText("Mostrar Email");
    } else {
        labelEmail.setText(usuario.getEmail());
        botaoMostrarEmail.setText("Ocultar Email");
    }

    emailVisivel = !emailVisivel;
}
    
    
    private void alerta(String msg) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Aviso");
    alert.setHeaderText(null);
    alert.setContentText(msg);
    alert.showAndWait();
}
    
    
    // criar senha forte [
   public static String gerarSenhaSegura() {
    SecureRandom random = new SecureRandom();
    StringBuilder senha = new StringBuilder();
    Set<Character> usados = new HashSet<>();

    // Garantir ao menos um de cada tipo
    senha.append(adicionarUnico(MAIUSCULAS, usados, random));
    senha.append(adicionarUnico(MINUSCULAS, usados, random));
    senha.append(adicionarUnico(NUMEROS, usados, random));
    senha.append(adicionarUnico(ESPECIAIS, usados, random));

    // Preencher o restante sem repetir caracteres
    while (senha.length() < TAMANHO_SENHA) {
        char c = getAleatorio(TODOS, random);
        if (usados.add(c)) {
            senha.append(c);
        }
    }

    // Embaralhar os caracteres
    return embaralhar(senha.toString(), random);
}

private static char getAleatorio(String caracteres, SecureRandom random) {
    int index = random.nextInt(caracteres.length());
    return caracteres.charAt(index);
}

private static char adicionarUnico(String grupo, Set<Character> usados, SecureRandom random) {
    char c;
    do {
        c = getAleatorio(grupo, random);
    } while (usados.contains(c));
    usados.add(c);
    return c;
}

private static String embaralhar(String senha, SecureRandom random) {
    char[] array = senha.toCharArray();
    for (int i = array.length - 1; i > 0; i--) {
        int j = random.nextInt(i + 1);
        // Swap
        char temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    return new String(array);
}

@FXML
private void pegarSenhaForte() {
    senhaForte.setText(gerarSenhaSegura());
}
    // ]
     
     
    // Verificar forca da senha [ 
      public static int calcularForca(String senha) {
        int pontuacao = 0;

        if (senha.length() >= 12) {
            pontuacao += 3;
        } else if (senha.length() >= 8) {
            pontuacao += 1;
        }

        if (senha.matches(".*[A-Z].*")) pontuacao += 1;
        if (senha.matches(".*[a-z].*")) pontuacao += 1;
        if (senha.matches(".*\\d.*")) pontuacao += 1;
        if (senha.matches(".*[^a-zA-Z0-9].*")) pontuacao += 2;

        if (!temRepeticao(senha)) pontuacao += 1;

        return pontuacao;
    }

    public static boolean temRepeticao(String senha) {
    Set<Character> set = new HashSet<>();
    for (char c : senha.toCharArray()) {
        if (!set.add(c)) {
            return true; // já existe no set
        }
    }
    return false; // todos são únicos
}

    public static String classificarSenha(int forca) {
        if (forca <= 2) return "Muito fraca";
        if (forca <= 4) return "Fraca";
        if (forca <= 6) return "Média";
        if (forca <= 8) return "Forte";
        return "Muito forte";
    }
    
    @FXML
    public void PegarForca(){
    String senha_colocada = senha_Colocada_Forca.getText();
        
        if(senha_colocada.trim().isEmpty()){
            Saida_Forca.setText(String.format("Campo vazio!"));
        }
        else{
        
        int forca = calcularForca(senha_colocada);    
        String classificacao = classificarSenha(forca);
        
        Saida_Forca.setText(String.format("Senha: %s\nPontuação: %d/9\nClassificação: %s", senha_colocada, forca, classificacao));
        }
        
    }
    
    // ]
    
   @FXML
private void handleLogout() {
    Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacao.setTitle("Confirmar Logout");
    confirmacao.setHeaderText(null);
    confirmacao.setContentText("Tem certeza que deseja fazer logout? (Atenção: todas as alterações não salvas serão perdidas)");

    Optional<ButtonType> resultado = confirmacao.showAndWait();
    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
        Sessao.setUsuario(null); // Limpa a sessão

        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
            alerta("Erro ao voltar para a tela de login.");
        }
    }
}

@FXML
private void handleSair() {
    Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
    Alert Informar = new Alert(Alert.AlertType.INFORMATION);
    confirmacao.setTitle("Confirmar Saida");
    confirmacao.setHeaderText(null);
    confirmacao.setContentText("Tem certeza de que deseja sair? (Atenção: todas as alterações não salvas serão perdidas)");

    Optional<ButtonType> resultado = confirmacao.showAndWait();
    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
    try{    
    Platform.exit();
    }
    catch(Exception err){Informar.setContentText("Erro ao tentar sair!");System.out.println(err.getMessage());}
    }
}
    
    
}
   


    
    

