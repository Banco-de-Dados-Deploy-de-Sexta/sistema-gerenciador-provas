
# Documentação - Sprint 2

# 📌 Backlog da Sprint

| Rank | Prioridade | User Story | Requisito do Cliente | Status |
|:----:|:----------:|------------|:---------------------:|:------:|
| 1 | Alta | **PB01** — Como professor, quero que as questões da prova sejam corrigidas automaticamente para eu lançar as notas dos alunos. | RF-01 | ⬜ |
| 2 | Alta | **PB17** — Como professor, quero criar uma questão para poder utilizá-la na montagem de provas. | RF-15 | ⬜ |
| 3 | Alta | **PB22** — Como professor, quero digitar a pergunta da questão para adicioná-la à prova. | RF-21 | ⬜ |
| 4 | Alta | **PB23** — Como professor, quero criar diferentes alternativas para uma questão para montar questões de múltipla escolha. | RF-22 | ⬜ |
| 5 | Alta | **PB24** — Como professor, quero editar as alternativas de uma questão para mudar seu conteúdo ou corrigir algum erro. | RF-23 | ⬜ |
| 6 | Alta | **PB25** — Como professor, quero excluir alternativas de uma questão para manter apenas as opções relevantes ao respondê-la. | RF-24 | ⬜ |
| 7 | Alta | **PB26** — Como professor, quero definir a alternativa correta de uma questão para que a prova seja corrigida corretamente. | RF-25 | ⬜ |
| 8 | Alta | **PB18** — Como professor, quero editar uma questão para trocar seu tipo ou corrigir algum erro de digitação. | RF-16 | ⬜ |
| 9 | Alta | **PB19** — Como professor, quero excluir uma questão não atribuída a nenhuma prova para manter a lista apenas com questões que estou utilizando. | RF-17 | ⬜ |
| 10 | Alta | **PB20** — Como professor, quero visualizar uma lista com todas as questões criadas no sistema para poder compará-las ao criar uma nova prova. | RF-19 | ⬜ |
| 11 | Alta | **PB02** — Como professor, quero criar uma prova para aplicá-la aos alunos. | RF-02 | ⬜ |
| 12 | Alta | **PB14** — Como professor, quero atribuir uma questão que criei a uma prova para aplicá-la aos alunos. | RF-11 | ⬜ |
| 13 | Média | **PB12** — Como professor, quero definir os pesos das questões para que as questões mais difíceis valham mais pontos. | RF-09 | ⬜ |
| 14 | Média | **PB05** — Como professor, quero visualizar todas as provas disponíveis no sistema para ter diferentes modelos para criar as minhas. | RF-05 | ⬜ |
| 15 | Média | **PB03** — Como professor, quero editar uma prova para mudar uma questão ou corrigir algum erro. | RF-03 | ⬜ |
| 16 | Média | **PB04** — Como professor, quero excluir uma prova não aplicada para manter a lista apenas com provas que estou utilizando. | RF-04 | ⬜ |

---

## 🏃‍ DoR - Definition of Ready <a id="dor"></a>

- User Stories com **Critérios de Aceitação** claramente definidos
- Regras de negócio da funcionalidade **documentadas** (ex.: regras de criação/correção de provas, permissões de acesso, etc.)
- Modelagem de dados/impacto no banco de dados **identificado** (novas tabelas, campos ou relacionamentos necessários)
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

## ✅ DoR por User Story <a id="dor-stories"></a>

### PB01 — Correção automática das questões da prova
- **Critério de Aceitação:** Ao percorrer uma prova o sistema deve corrigir as questões de acordo com a resposta correta definida pelo professor (exceto questões de texto livre).
- **Regras de Negócio:** Uma questão é considerada correta quando a alternativa respondida é igual à alternativa definida como correta; questões sem resposta são consideradas erradas.
- **Banco de Dados:** Leitura da alternativa correta de cada questão e das respostas dadas à prova.
- **Dependências:** PB14 (questões atribuídas à prova) e PB26 (alternativa correta definida).
- **Escopo:** Lógica de correção de questões de alternativa a partir de um conjunto de respostas. A realização da prova pelo aluno (PB09) fica para a Sprint 3.
- **Protótipo:** Não se aplica (processamento interno).

### PB17 — Criar questão
- **Critério de Aceitação:** Ao entrar como professor deve haver uma funcionalidade de criação de questões, com definição de resposta correta (se for alternativa).
- **Regras de Negócio:** Toda questão deve ter um enunciado e um tipo; a questão fica disponível para reaproveitamento em outras provas (RNF1).
- **Banco de Dados:** Criação da tabela de questões (id, enunciado, tipo, professor).
- **Dependências:** Nenhuma.
- **Escopo:** Criação e armazenamento de questões do tipo alternativa. A escolha entre outros tipos de questão (PB21) fica para a Sprint 3.
- **Protótipo:** Tela de cadastro de questão.

### PB22 — Digitar a pergunta da questão
- **Critério de Aceitação:** Ao criar uma questão o professor deve poder digitar livremente o enunciado, com limite mínimo de cinco caracteres.
- **Regras de Negócio:** O enunciado é obrigatório e deve ter no mínimo cinco caracteres.
- **Banco de Dados:** Campo de enunciado na tabela de questões.
- **Dependências:** PB17.
- **Escopo:** Campo de texto do enunciado com validação de tamanho mínimo.
- **Protótipo:** Campo de enunciado na tela de cadastro de questão.

### PB23 — Criar alternativas
- **Critério de Aceitação:** Ao criar uma questão do tipo **alternativa** o professor deve poder criar no máximo cinco alternativas possíveis, de **a** à **e**.
- **Regras de Negócio:** Máximo de cinco alternativas por questão; cada alternativa deve possuir texto.
- **Banco de Dados:** Criação da tabela de alternativas (id, questão, letra, texto, correta) relacionada à tabela de questões.
- **Dependências:** PB17.
- **Escopo:** Cadastro de alternativas durante a criação da questão.
- **Protótipo:** Seção de alternativas na tela de cadastro de questão.

### PB24 — Editar alternativas
- **Critério de Aceitação:** Ao acessar uma questão de alternativas, deve ser possível editar cada uma das alternativas.
- **Regras de Negócio:** O texto da alternativa não pode ficar vazio após a edição.
- **Banco de Dados:** Atualização de registros na tabela de alternativas.
- **Dependências:** PB23.
- **Escopo:** Edição do texto de cada alternativa de uma questão.
- **Protótipo:** Tela de edição de questão.

### PB25 — Excluir alternativas
- **Critério de Aceitação:** Ao acessar uma questão de alternativas, deve ser possível excluir cada uma das alternativas.
- **Regras de Negócio:** Ao excluir a alternativa correta, a questão fica sem resposta correta até que uma nova seja definida.
- **Banco de Dados:** Remoção de registros na tabela de alternativas.
- **Dependências:** PB23.
- **Escopo:** Exclusão individual de alternativas de uma questão.
- **Protótipo:** Tela de edição de questão.

### PB26 — Definir alternativa correta
- **Critério de Aceitação:** Ao acessar uma questão de alternativas, deve ser possível definir uma das alternativas criadas como correta.
- **Regras de Negócio:** Cada questão de alternativa deve ter exatamente uma alternativa correta.
- **Banco de Dados:** Campo que indica a alternativa correta na tabela de alternativas.
- **Dependências:** PB23.
- **Escopo:** Seleção da alternativa correta na criação e na edição da questão.
- **Protótipo:** Seleção da alternativa correta na tela de cadastro/edição de questão.

### PB18 — Editar questão
- **Critério de Aceitação:** Ao entrar como professor deve haver uma funcionalidade de edição de questões armazenadas, podendo alterar todos os atributos da questão.
- **Regras de Negócio:** As mesmas validações da criação (PB22, PB23 e PB26) se aplicam na edição.
- **Banco de Dados:** Atualização de registros na tabela de questões.
- **Dependências:** PB17 e PB20.
- **Escopo:** Edição do enunciado e das alternativas da questão. A troca de tipo depende da PB21 (Sprint 3).
- **Protótipo:** Tela de edição de questão.

### PB19 — Excluir questão
- **Critério de Aceitação:** Ao entrar como professor deve haver uma funcionalidade de exclusão de questões armazenadas.
- **Regras de Negócio:** Só é possível excluir questões que não estejam atribuídas a nenhuma prova; ao excluir a questão, suas alternativas também são excluídas.
- **Banco de Dados:** Remoção de registros nas tabelas de questões e alternativas; verificação na tabela de relacionamento prova-questão.
- **Dependências:** PB17 e PB20.
- **Escopo:** Exclusão de questões não atribuídas, com mensagem de erro caso a questão esteja em uso.
- **Protótipo:** Ação de excluir na lista de questões.

### PB20 — Visualizar lista de questões
- **Critério de Aceitação:** Ao entrar como professor deve haver uma funcionalidade de visualização da lista de questões armazenadas.
- **Regras de Negócio:** A lista deve exibir todas as questões cadastradas no sistema.
- **Banco de Dados:** Consulta à tabela de questões.
- **Dependências:** PB17.
- **Escopo:** Listagem das questões com enunciado e tipo, servindo de ponto de acesso para editar e excluir.
- **Protótipo:** Tela de listagem de questões.

### PB02 — Criar prova
- **Critério de Aceitação:** Ao entrar como professor deve haver uma tela para criação de provas.
- **Regras de Negócio:** Toda prova pertence ao professor que a criou (RN1).
- **Banco de Dados:** Criação da tabela de provas (id, título, disciplina, professor).
- **Dependências:** Nenhuma.
- **Escopo:** Criação e armazenamento de provas. Aplicação e agendamento ficam para a Sprint 3.
- **Protótipo:** Tela de cadastro de prova.

### PB14 — Atribuir questão à prova
- **Critério de Aceitação:** O professor pode atribuir individualmente uma questão a uma ou mais provas.
- **Regras de Negócio:** Uma mesma questão não pode ser atribuída duas vezes à mesma prova.
- **Banco de Dados:** Criação da tabela de relacionamento prova-questão.
- **Dependências:** PB02 e PB17.
- **Escopo:** Adicionar e remover questões de uma prova.
- **Protótipo:** Seleção de questões na tela de cadastro/edição de prova.

### PB12 — Definir peso das questões
- **Critério de Aceitação:** O professor pode definir pesos diferentes para cada questão de uma prova.
- **Regras de Negócio:** O peso deve ser um número maior que zero; o peso padrão de uma questão é 1.
- **Banco de Dados:** Campo de peso na tabela de relacionamento prova-questão.
- **Dependências:** PB14.
- **Escopo:** Definição do peso de cada questão dentro de uma prova.
- **Protótipo:** Campo de peso na lista de questões da prova.

### PB05 — Visualizar provas
- **Critério de Aceitação:** Ao consultar as provas como professor deve ser possível visualizá-las em detalhes, como estado da prova, disciplina, conteúdo, quantidade de questões.
- **Regras de Negócio:** O professor pode visualizar as provas disponíveis no sistema para usá-las como modelo (RN3).
- **Banco de Dados:** Consulta às tabelas de provas e prova-questão.
- **Dependências:** PB02 e PB14.
- **Escopo:** Listagem de provas e tela de detalhes, servindo de ponto de acesso para editar e excluir.
- **Protótipo:** Tela de listagem e detalhes de provas.

### PB03 — Editar prova
- **Critério de Aceitação:** Ao consultar as provas como professor deve ser possível editá-las.
- **Regras de Negócio:** O professor só pode editar as suas próprias provas.
- **Banco de Dados:** Atualização de registros nas tabelas de provas e prova-questão.
- **Dependências:** PB02, PB14 e PB05.
- **Escopo:** Edição dos dados da prova e das questões atribuídas a ela.
- **Protótipo:** Tela de edição de prova.

### PB04 — Excluir prova
- **Critério de Aceitação:** Ao consultar as provas como professor deve ser possível excluí-las.
- **Regras de Negócio:** Só é possível excluir provas que ainda não foram aplicadas; ao excluir a prova, as questões continuam armazenadas no sistema.
- **Banco de Dados:** Remoção de registros nas tabelas de provas e prova-questão.
- **Dependências:** PB02 e PB05.
- **Escopo:** Exclusão de provas não aplicadas, com confirmação antes de excluir.
- **Protótipo:** Ação de excluir na lista de provas.

