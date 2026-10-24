# Sistema de Gerenciamento de Provas para Universidades - API 2º Semestre BD

# Equipe Deploy de Sexta

## 🏅 Desafio <a id="desafio"></a>

O desafio consiste em desenvolver um sistema gerenciador de avaliações para uma universidade, será utilizado por Professores e Alunos. Professores podem criar, consultar, aplicar e comparar diferentes avaliações e questões, além de aplica-las aos alunos. Alunos por sua vez podem realizar, corrigir e consultar notas das avaliações pelo sistema. Diante dessas e outras funcionalidades, a principal Dor do cliente — e principal objetivo do sistema — é corrigir automaticamente as avaliações em massa, de modo a facilitar o trabalho do Professor.

---

 📌 Backlog do Projeto (Tabela)

| Rank | ID | Prioridade | User Story | Story Points | Sprint | Requisito do Cliente | Status |
| :--: | :--: | :--------: | ---------- | :----------: | :----: | :-------------------: | :----: |
| 1 | PB01 | Alta | Como professor quero que as questões da prova sejam corrigidas automaticamente para eu lançar as notas dos alunos | 8 | 2 | RF-01 | ⬜ |
| 2 | PB17 | Alta | Como professor quero criar uma questão para poder utilizá-la na montagem de provas | 5 | 2 | RF-15 | ⬜ |
| 3 | PB22 | Alta | Como professor quero digitar a pergunta da questão para adiciona-la à prova | 2 | 2 | RF-21 | ⬜ |
| 4 | PB23 | Alta | Como professor quero criar diferentes alternativas para uma questão para montar questões de múltipla escolha | 5 | 2 | RF-22 | ⬜ |
| 5 | PB24 | Alta | Como professor quero editar as alternativas de uma questão para mudar seu conteúdo ou corrigir algum erro | 3 | 2 | RF-23 | ⬜ |
| 6 | PB25 | Alta | Como professor quero excluir alternativas de uma questão para manter apenas as opções relevantes ao respondê-la | 2 | 2 | RF-24 | ⬜ |
| 7 | PB26 | Alta | Como professor quero definir a alternativa correta de uma questão para que a prova seja corrigida corretamente | 3 | 2 | RF-25 | ⬜ |
| 8 | PB18 | Alta | Como professor quero editar uma questão para trocar seu tipo ou corrigir algum erro de digitação | 3 | 2 | RF-16 | ⬜ |
| 9 | PB19 | Alta | Como professor quero excluir uma questão não atribuída a nenhuma prova para manter a lista apenas com questões que estou utilizando | 3 | 2 | RF-17 | ⬜ |
| 10 | PB20 | Alta | Como professor quero visualizar uma lista com todas as questões criadas no sistema para assim poder comparar ao criar uma nova prova | 3 | 2 | RF-19 | ⬜ |
| 11 | PB02 | Alta | Como professor quero criar uma prova para aplicá-la aos alunos | 5 | 2 | RF-02 | ⬜ |
| 12 | PB14 | Alta | Como professor quero atribuir uma questão que criei a uma prova para aplica-la aos alunos | 5 | 2 | RF-11 | ⬜ |
| 13 | PB12 | Média | Como professor quero definir os pesos das questões para as questões mais difíceis valerem mais pontos | 3 | 2 | RF-09 | ⬜ |
| 14 | PB05 | Média | Como professor quero visualizar todas as provas disponíveis no sistema para ter diferentes modelos para criar as minhas | 3 | 2 | RF-05 | ⬜ |
| 15 | PB03 | Média | Como professor quero editar uma prova para mudar uma questão ou corrigir algum erro | 3 | 2 | RF-03 | ⬜ |
| 16 | PB04 | Média | Como professor quero excluir uma prova não aplicada para manter a lista apenas com provas que estou utilizando | 2 | 2 | RF-04 | ⬜ |
| 17 | PB21 | Média | Como professor quero escolher entre vários tipos de questão ao criá-la para montar provas com formatos variados | 5 | 3 | RF-20 | ⬜ |
| 18 | PB07 | Média | Como professor quero aplicar uma prova a um ou mais alunos para que eles a realizem no sistema | 5 | 3 | RF-06 | ⬜ |
| 19 | PB08 | Média | Como professor quero agendar uma prova em uma data específica para que os alunos associados possam realizar naquele dia | 3 | 3 | RF-07 | ⬜ |
| 20 | PB06 | Média | Como aluno quero visualizar as provas associadas a mim para saber se fui bem nas provas que fiz e quais serão as próximas | 3 | 3 | RF-05 | ⬜ |
| 21 | PB09 | Média | Como aluno quero realizar as provas associadas a mim para ficar com nota acima da média na faculdade | 8 | 3 | RF-06 | ⬜ |
| 22 | PB13 | Média | Como professor quero que o sistema calcule e aplique a nota final da prova após a correção para não precisar calcular manualmente a nota de cada aluno | 5 | 3 | RF-10 | ⬜ |
| 23 | PB10 | Baixa | Como professor quero salvar as provas em meu computador para enviar o arquivo da prova em outros canais de mensagem | 5 | 3 | RF-08 | ⬜ |
| 24 | PB11 | Baixa | Como aluno quero salvar as minhas provas em meu computador para consulta-las depois com mais facilidade | 3 | 3 | RF-08 | ⬜ |
| 25 | PB15 | Baixa | Como professor quero que as questões das provas sejam embaralhadas ao serem aplicadas para cada aluno para dificultar que eles colem uns dos outros | 3 | 3 | RF-13 | ⬜ |
| 26 | PB16 | Baixa | Como professor quero que as alternativas das questões das provas sejam embaralhadas ao serem aplicadas para cada aluno para dificultar que eles colem uns dos outros | 3 | 3 | RF-12 | ⬜ |
| 27 | PB27 | Baixa | Como professor quero salvar as questões em meu computador para enviar o arquivo da questão em outros canais de mensagem | 3 | 3 | RF-26 | ⬜ |

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

<h4 align="center">
  <a href="https://www.java.com/pt-br/">
    <img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI4MiIgaGVpZ2h0PSIyOCIgdmlld0JveD0iMCAwIDgyIDI4Ij4KICA8cmVjdCB3aWR0aD0iODIiIGhlaWdodD0iMjgiIGZpbGw9IiNmNWY1ZjUiLz4KICA8dGV4dCB4PSI0MSIgeT0iMTgiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJWZXJkYW5hLCBHZW5ldmEsIHNhbnMtc2VyaWYiIGZvbnQtd2VpZ2h0PSJib2xkIiBmb250LXNpemU9IjEyIiBsZXR0ZXItc3BhY2luZz0iMSIgZmlsbD0iI2ZmMDAwMCI+SkFWQTwvdGV4dD4KPC9zdmc+Cg=="/>
  </a>
  <a href="https://openjfx.io/">
    <img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDYiIGhlaWdodD0iMjgiIHZpZXdCb3g9IjAgMCAxMDYgMjgiPgogIDxyZWN0IHdpZHRoPSIxMDYiIGhlaWdodD0iMjgiIGZpbGw9IiNmNWY1ZjUiLz4KICA8dGV4dCB4PSI1MyIgeT0iMTgiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZvbnQtZmFtaWx5PSJWZXJkYW5hLCBHZW5ldmEsIHNhbnMtc2VyaWYiIGZvbnQtd2VpZ2h0PSJib2xkIiBmb250LXNpemU9IjEyIiBsZXR0ZXItc3BhY2luZz0iMSIgZmlsbD0iI2ZmMDAwMCI+SkFWQUZYPC90ZXh0Pgo8L3N2Zz4K"/>
  </a>
</h4>

---

## 📁 Estrutura do projeto

```bash
  📦 sistema-gerenciador-avaliacoes
    ┣ 📁 src
    ┣ 📁 docs
      ┣ 📁 prototipos
      ┣ 📁 sprints        
      ┗ 📜 backlog.md
      ┗ 📜 requisitos.md
    ┗ 📜 README.md
```

---

## 📁 Pasta de Documentação: [Link](docs) 📄

## 🏃‍ DoR - Definition of Ready <a id="dor"></a>

- User Stories com **Critérios de Aceitação** claramente definidos
- Regras de negócio da funcionalidade **documentadas** (ex.: regras de criação/correção de provas, permissões de acesso, etc.)
- Dependências técnicas ou de outras User Stories **identificadas**
- Escopo da funcionalidade **delimitado**
- Protótipo/tela de referência **disponível**, quando aplicável

## 🏆 DoD - Definition of Done <a id="dod"></a>

- Manual de Usuário atualizado
- Manual da Aplicação atualizado
- Funcionalidade implementada e **executando corretamente** no ambiente de desenvolvimento
- Código organizado, **legível** e seguindo o padrão definido pela equipe
- Todos os critérios de aceitação **atendidos**
- Tratamento de entradas inválidas e casos de erro implementado
- Integração com o banco de dados validada (persistência e consulta corretas)
- Atualização do **README.md** e demais documentações (se necessário)

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
