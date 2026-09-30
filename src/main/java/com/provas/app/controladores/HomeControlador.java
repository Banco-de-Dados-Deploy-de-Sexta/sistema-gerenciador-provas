package com.provas.app.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

//Controlador da tela Home (ligado ao home.fxml)
public class HomeControlador {

    public Pane painel;
    public GridPane gridAlternativas;
    public TextField letraCaixaTexto;
    public TextField perguntaCaixaTexto;

    private final List<Alternativa> alternativas = new ArrayList<>();

    //Grupo que garante que apenas uma alternativa fique selecionada
    private final ToggleGroup grupoAlternativas = new ToggleGroup();

    @FXML
    public void adicionarAlternativa(){
        //O indice da nova alternativa e a proxima linha livre do grid (comeca em 0)
        int novoNumeroAlternativa = alternativas.size();
        String letra = String.valueOf((char) ('a' + novoNumeroAlternativa));
        Alternativa novaAlternativa = new Alternativa(novoNumeroAlternativa, letra, perguntaCaixaTexto.getText());
        alternativas.add(novaAlternativa);
        adicionarAlternativaAGrid(novaAlternativa);
        perguntaCaixaTexto.clear();
    }

    private void criarAlternativasExemplo(){
        alternativas.add(new Alternativa(0,"a", "z = 1"));
        alternativas.add(new Alternativa(1,"b", "z = 2"));
    }

    private void adicionarAlternativaAGrid(Alternativa alternativa){
        RadioButton radio = new RadioButton(alternativa.letraAlternativa);
        radio.setToggleGroup(grupoAlternativas);
        gridAlternativas.addRow(alternativa.numeroAlternativa, radio, new Text(alternativa.perguntaAlternativa));
    }

    private void criarGridAlternativas(){
        for (Alternativa alternativa : alternativas){
            adicionarAlternativaAGrid(alternativa);
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
