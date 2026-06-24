package com.mycompany.securityhub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.beans.property.SimpleStringProperty;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import java.util.Optional;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

public class Guardar_senhas {

    @FXML private TableView<Senha> tabelaSenhas;
    @FXML private TableColumn<Senha, String> colunaServico;
    @FXML private TableColumn<Senha, String> colunaDescricao;
    @FXML private TableColumn<Senha, String> colunaSenha;

    @FXML private Button btnAdicionar;
    @FXML private Button btnEditar;
    @FXML private Button btnDeletar;

    private ObservableList<Senha> listaSenhas = FXCollections.observableArrayList();

    
    @FXML    
    private void Voltar() throws IOException {
    App.setRoot("Tela_Principal");
    }
    
    @FXML
    public void initialize() {
        colunaServico.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getServico()));
        colunaDescricao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescricao()));
        colunaSenha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSenha()));

        tabelaSenhas.setItems(listaSenhas);

        btnAdicionar.setOnAction(e -> adicionarSenha());
        btnEditar.setOnAction(e -> editarSenhaSelecionada());
        btnDeletar.setOnAction(e -> deletarSenhaSelecionada());

        // Exemplo inicial
        listaSenhas.add(new Senha("Netflix", "Conta pessoal", "senha123@ABC"));
    }

    private void adicionarSenha() {
        Senha nova = new Senha("Novo Serviço", "Descrição aqui", "senha123@ABC");
        listaSenhas.add(nova);
    }

    private void editarSenhaSelecionada() {
        Senha selecionada = tabelaSenhas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            alerta("Selecione uma senha para editar.");
            return;
        }

        Dialog<Senha> dialog = new Dialog<>();
        dialog.setTitle("Editar Senha");
        dialog.setHeaderText("Editar informações da senha");

        ButtonType botaoSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(botaoSalvar, ButtonType.CANCEL);

        TextField campoServico = new TextField(selecionada.getServico());
        TextField campoDescricao = new TextField(selecionada.getDescricao());
        TextField campoSenha = new TextField(selecionada.getSenha());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Serviço:"), 0, 0);
        grid.add(campoServico, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(campoDescricao, 1, 1);
        grid.add(new Label("Senha:"), 0, 2);
        grid.add(campoSenha, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(botao -> {
            if (botao == botaoSalvar) {
                return new Senha(campoServico.getText(), campoDescricao.getText(), campoSenha.getText());
            }
            return null;
        });

        Optional<Senha> resultado = dialog.showAndWait();
        resultado.ifPresent(senhaEditada -> {
            selecionada.setServico(senhaEditada.getServico());
            selecionada.setDescricao(senhaEditada.getDescricao());
            selecionada.setSenha(senhaEditada.getSenha());
            tabelaSenhas.refresh();
        });
    }

    private void deletarSenhaSelecionada() {
        Senha selecionada = tabelaSenhas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            alerta("Selecione uma senha para deletar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar exclusão");
        confirm.setHeaderText("Deletar senha para: " + selecionada.getServico());
        confirm.setContentText("Tem certeza?");

        Optional<ButtonType> resultado = confirm.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            listaSenhas.remove(selecionada);
        }
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
   private Window getJanela() {
    return tabelaSenhas.getScene().getWindow();
}
    
 @FXML
private void gerarDocx() {
    try {
        Usuario usuario = Sessao.getUsuario(); // Pegando o objeto da sessão

        if (usuario == null) {
            alerta("Usuário não está logado.");
            return;
        }

        XWPFDocument documento = new XWPFDocument();
        XWPFParagraph paragrafo = documento.createParagraph();
        XWPFRun run = paragrafo.createRun();

        run.setText("Relatório de Senhas");
        run.setBold(true);
        run.addBreak();
        run.setText("Usuário: " + usuario.getNome());
        run.addBreak();
        run.setText("Email: " + usuario.getEmail());
        run.addBreak();
        run.addBreak();

        // Tabela
        XWPFTable tabela = documento.createTable();
        XWPFTableRow cabecalho = tabela.getRow(0);

        // Célula 1 - Serviço
        XWPFParagraph p1 = cabecalho.getCell(0).getParagraphs().get(0);
        XWPFRun r1 = p1.createRun();
        r1.setBold(true);
        r1.setText("Serviço");

        // Célula 2 - Descrição
        XWPFTableCell cell2 = cabecalho.addNewTableCell();
        XWPFParagraph p2 = cell2.getParagraphs().get(0);
        XWPFRun r2 = p2.createRun();
        r2.setBold(true);
        r2.setText("Descrição");

        // Célula 3 - Senha
        XWPFTableCell cell3 = cabecalho.addNewTableCell();
        XWPFParagraph p3 = cell3.getParagraphs().get(0);
        XWPFRun r3 = p3.createRun();
        r3.setBold(true);
        r3.setText("Senha");

        for (Senha s : listaSenhas) {
            XWPFTableRow linha = tabela.createRow();
            linha.getCell(0).setText(s.getServico());
            linha.getCell(1).setText(s.getDescricao());
            linha.getCell(2).setText(s.getSenha());
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos Word", "*.docx"));
        fileChooser.setInitialFileName("relatorio_senhas.docx");
        File arquivo = fileChooser.showSaveDialog(getJanela());

        if (arquivo != null) {
            try (FileOutputStream out = new FileOutputStream(arquivo)) {
                documento.write(out);
                alerta("Arquivo .docx salvo com sucesso!");
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        alerta("Erro ao gerar .docx.");
    }
}

@FXML
private void importarDocx() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Importar Relatório de Senhas");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos Word", "*.docx"));
    File arquivo = fileChooser.showOpenDialog(getJanela());

    if (arquivo != null) {
        try (FileInputStream fis = new FileInputStream(arquivo);
             XWPFDocument doc = new XWPFDocument(fis)) {

            List<XWPFTable> tabelas = doc.getTables();
            if (tabelas.isEmpty()) {
                alerta("Nenhuma tabela encontrada no documento.");
                return;
            }

            XWPFTable tabela = tabelas.get(0);
            ObservableList<Senha> senhasImportadas = FXCollections.observableArrayList();

            for (int i = 1; i < tabela.getNumberOfRows(); i++) { // pula o cabeçalho
                XWPFTableRow row = tabela.getRow(i);
                String servico = row.getCell(0).getText();
                String descricao = row.getCell(1).getText();
                String senha = row.getCell(2).getText();

                senhasImportadas.add(new Senha(servico, descricao, senha));
            }

            tabelaSenhas.setItems(senhasImportadas);
            alerta("Senhas importadas com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
            alerta("Erro ao ler o arquivo .docx");
        }
    }
}




}
