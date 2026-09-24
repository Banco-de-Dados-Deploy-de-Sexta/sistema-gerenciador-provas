# Sistema de Gerenciamento de Provas para Universidades - API 2º Semestre BD

# Equipe Deploy de Sexta

## 🏅 Desafio <a id="desafio"></a>

O desafio consiste em desenvolver um sistema gerenciador de avaliações para uma universidade, será utilizado por Professores e Alunos. Professores podem criar, consultar, aplicar e comparar diferentes avaliações e questões, além de aplica-las aos alunos. Alunos por sua vez podem realizar, corrigir e consultar notas das avaliações pelo sistema. Diante dessas e outras funcionalidades, a principal Dor do cliente — e principal objetivo do sistema — é corrigir automaticamente as avaliações em massa, de modo a facilitar o trabalho do Professor.

---

# 📌 Backlog do Projeto (Tabela)

| Rank | Prioridade | User Story | Sprint | Requisito do Cliente | Status |
| :--: | :--------: | ---------- | :----: | :------------------: | :----: |
| 1 | Media | Como professor quero que as questões da prova sejam corrigidas automaticamente para eu lançar as notas dos alunos | 2 | RF-01 | ⬜ |
| 2 | Media | Como professor quero criar uma prova e quero que ela fique armazenada para manter um histórico | 2 | RF-02 | ⬜ |
| 3 | Media | Como professor quero editar uma prova para mudar uma questão ou corrigir algum erro | 2 | RF-03 | ⬜ |
| 4 | Media | Como professor quero excluir uma prova para manter a lista apenas com provas que estou utilizando | 2 | RF-04 | ⬜ |
| 5 | Media | Como professor quero visualizar todas as provas disponíveis no sistema para ter diferentes modelos para criar as minhas | 3 | RF-05 | ⬜ |
| 6 | Media | Como aluno quero visualizar as provas associadas a mim para saber se fui bem nas provas que fiz e quais serão as próximas | 3 | RF-05 | ⬜ |
| 7 | Media | Como professor quero aplicar uma prova a um ou mais alunos para que ele realize a prova no sistema | 3 | RF-06 | ⬜ |
| 8 | Media | Como professor quero agendar uma prova em uma data específica para que os alunos associados possam realizar naquele dia | 3 | RF-07 | ⬜ |
| 9 | Media | Como aluno quero realizar as provas associdas a mim para ficar com nota acima da média na faculdade | 3 | RF-06 | ⬜ |
| 10 | Baixa | Como professor quero salvar as provas em meu computador para enviar o arquivo da prova em outros canais de mensagem | 3 | RF-08 | ⬜ |
| 11 | Baixa | Como aluno quero salvar as minhas provas em meu computador para consulta-las depois com mais facilidade | 3 | RF-08 | ⬜ |
| 12 | Media | Como professor quero definir os pesos das questões para as questões mais difíceis valerem mais pontos | 3 | RF-09 | ⬜ |
| 13 | Media | Como professor quero que o sistema calcule e aplique uma nota na prova depois de ser corrigida para eu conferir se a nota está correta | 2 | RF-10 | ⬜ |
| 14 | Media | Como professor quero atribuir uma questão que criei a uma prova para complementar um modelo de prova que utilizo | 2 | RF-11 | ⬜ |
| 15 | Baixa | Como professor quero que as questões das provas sejam embaralhadas ao serem aplicadas para cada aluno para dificulta-los de trapacear | 3 | RF-13 | ⬜ |
| 16 | Baixa | Como professor quero que as alternativas das questões das provas sejam embaralhadas ao serem aplicadas para cada aluno para dificulta-los de trapacear | 3 | RF-12 | ⬜ |
| 17 | Media | Como professor quero criar uma questão e quero que ela fique armazenada para manter um histórico | 2 | RF-15 | ⬜ |
| 18 | Media | Como professor quero editar uma questão e para trocar seu tipo ou corrigir algum erro de digitação | 2 | RF-16 | ⬜ |
| 19 | Media | Como professor quero excluir uma questão para manter a lista apenas com questões que estou utilizando | 2 | RF-17 | ⬜ |
| 20 | Media | Como professor quero visualizar uma lista com todas as questões criadas no sistema para assim poder comparar ao criar uma nova prova | 3 | RF-19 | ⬜ |
| 21 | Baixa | Como professor quero escolher entre vários tipos ao criar uma questão para criar uma prova dinâmica | 3 | RF-20 | ⬜ |
| 22 | Media | Como professor quero digitar a pergunta da questão para que o aluno entenda corretamente o enunciado | 2 | RF-21 | ⬜ |
| 23 | Media | Como professor quero criar diferentes alternativas para uma questão para assim fornecer diferentes opções de resposta | 2 | RF-22 | ⬜ |
| 24 | Media | Como professor quero editar as alternativas de uma questão para mudar seu conteúdo ou corrigir algum erro | 2 | RF-23 | ⬜ |
| 25 | Media | Como professor quero excluir as alternativas de uma questão para mudar o número de alternativas da questão | 2 | RF-24 | ⬜ |
| 26 | Media | Como professor quero definir a alternativa correta de uma questão para que a prova seja corrigida corretamente | 2 | RF-25 | ⬜ |
| 27 | Baixa | Como professor quero salvar as questões em meu computador para enviar o arquivo da questão em outros canais de mensagem | 3 | RF-26 | ⬜ |

Backlog detalhado: [Link](docs/backlog.md) 📄

---

## 📅 Cronograma de Sprints <a id="sprint"></a>

| Sprint          |    Período    | Documentação                                     |
| --------------- | :-----------: | :-----------------: |
| 🔖 **SPRINT 1** | 07/09 - 27/09 | [Docs](./docs/sprints/sprint-1/README.md) |
| 🔖 **SPRINT 2** | 05/10 - 25/10 | [Docs](./docs/sprints/sprint-2/README.md) |
| 🔖 **SPRINT 3** | 02/11 - 22/11 | [Docs](./docs/sprints/sprint-3/README.md) |

---

## 💻 Tecnologias utilizadas<a id="tecnologias"></a>

A PREENCHER...

---

## 📁 Estrutura do projeto

```bash
  📦 sistema-gerenciador-avaliacoes
    ┣ 📁 src
    ┣ 📁 docs
      ┣ 📁 sprints        
      ┗ 📜 backlog.md
      ┗ 📜 requisitos.md
    ┗ 📜 README.md
```

---

## 📁 Pasta de Documentação: [Link](docs) 📄

## 🏃‍ DoR - Definition of Ready <a id="dor"></a>

A PREENCHER...

## 🏆 DoD - Definition of Done <a id="dod"></a>

A PREENCHER...

---

## 📖 Manual de Instalação <a id="manualInstalacao"></a>

A PREENCHER...

## 📖 Manual do Usuário <a id="manualUsuario"></a>

A PREENCHER...

---

## 🎓 Equipe <a id="equipe"></a>

<div align="center">
  <table>
    <tr>
      <th>Membro</th>
      <th>Função</th>
      <th>Github</th>
      <th>Linkedin</th>
    </tr>
    <tr>
      <td>João Victor Graciolli Ramos</td>
      <td>Product Owner</td>
      <td><a href="https://github.com/jgraciolli"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/jvgraciolli/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Miguel Vitale</td>
      <td>Scrum Master</td>
      <td><a href="https://github.com/MiguelVitale"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/miguel-vitale-2b42003a5/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Ana Laura Arantes Machado</td>
      <td>Developer</td>
      <td><a href="https://github.com/arantesmachadoanalaura-ops"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/ana-laura-arantes-machado-24a8323bb/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Eduardo Nakano Silva Senador</td>
      <td>Developer</td>
      <td><a href="https://github.com/enksise"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/eduardo-nakano-373702287/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Gustavo Otacílio Ramos</td>
      <td>Developer</td>
      <td><a href="https://github.com/gustavoramos16"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/gustavo-otacílio-7956242a6/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Natan Soares Telles</td>
      <td>Developer</td>
      <td><a href="https://github.com/natan-telles"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/natan-telles-5b2970288/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Olavo Salles Gino Teixeira</td>
      <td>Developer</td>
      <td><a href="https://github.com/olavosalles"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/olavo-salles-b5b698400/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
    <tr>
      <td>Weslley José Pereira de Moraes</td>
      <td>Developer</td>
      <td><a href="https://github.com/wjmoraes12"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></a></td>
      <td><a href="https://www.linkedin.com/in/weslley-moraes-b005472a2/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"></a></td>
    </tr>
  </table>
</div>
