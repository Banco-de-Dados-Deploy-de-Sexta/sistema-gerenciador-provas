package com.provas.app.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//Controlador da tela Home (ligado ao home.fxml)
public class HomeControlador {

    public Pane painel;
    public GridPane gridAlternativas;
    public TextField letraCaixaTexto;
    public TextField perguntaCaixaTexto;

    private List<Alternativa> alternativas = new ArrayList<>();

    @FXML
    public void escreverAlternativa(){

    }

    @FXML
    public void adicionarAlternativa(){
        int novoNumeroAlternativa = alternativas.size() + 1;
        Alternativa novaAlternativa = new Alternativa(novoNumeroAlternativa, String.valueOf((char) (novoNumeroAlternativa + 96)), perguntaCaixaTexto.getText());
        alternativas.add(novaAlternativa);
        adicionarAlternativaAGrid(novaAlternativa);
    }

    private void criarAlternativasExemplo(){
        alternativas.add(new Alternativa(0,"a", "z = 1"));
        alternativas.add(new Alternativa(1,"b", "z = 2"));
    }

    private void adicionarAlternativaAGrid(Alternativa alternativa){
        gridAlternativas.add(new RadioButton(alternativa.letraAlternativa), 0, alternativa.numeroAlternativa);
        gridAlternativas.add(new Text(alternativa.perguntaAlternativa), 1, alternativa.numeroAlternativa);
    }

    private void criarGridAlternativas(){
        for (Alternativa alternativa : alternativas){
            gridAlternativas.add(new RadioButton(alternativa.letraAlternativa), 0, alternativa.numeroAlternativa);
            gridAlternativas.add(new Text(alternativa.perguntaAlternativa), 1, alternativa.numeroAlternativa);
        }
    }

    @FXML public void initialize() {
        criarAlternativasExemplo();
        criarGridAlternativas();
    }

}

class Alternativa{
    int numeroAlternativa;
    String letraAlternativa;
    String perguntaAlternativa;

    Alternativa(int numeroAlternativa, String letraAlternativa, String perguntaAlternativa){
        this.numeroAlternativa = numeroAlternativa;
        this.letraAlternativa = letraAlternativa;
        this.perguntaAlternativa = perguntaAlternativa;
    }
}