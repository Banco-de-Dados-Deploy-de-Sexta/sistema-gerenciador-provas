
# Documentação - Sprint 3

# 🎯 Meta da Sprint

Completar o ciclo de vida da prova, da aplicação pelo professor à nota final do aluno, e ampliar os formatos de questão. Ao final da Sprint 3, garantimos que:

- O professor poderá criar questões de diferentes tipos: alternativa, associação e texto livre (**PB21**)
- O professor poderá aplicar uma prova a um ou mais alunos e agendá-la para um dia útil (**PB07**, **PB08**)
- O aluno poderá visualizar as provas associadas a ele e realizá-las no sistema no dia agendado, com questões e alternativas embaralhadas para cada aluno (**PB06**, **PB09**, **PB15**, **PB16**)
- O sistema calculará a nota final de cada aluno após a correção, e o professor poderá consultá-la por prova (**PB13**)
- Professor e aluno poderão salvar provas e questões em seus computadores (**PB10**, **PB11**, **PB27**)

# 📌 Backlog da Sprint

| Rank | Prioridade | User Story | Story Points | Requisito do Cliente | Status |
|:----:|:----------:|------------|:------------:|:---------------------:|:------:|
| 1 | Média | **PB21** — Como professor, quero escolher entre vários tipos de questão ao criá-la para montar provas com formatos variados. | 5 | RF-20 | ⬜ |
| 2 | Média | **PB07** — Como professor, quero aplicar uma prova a um ou mais alunos para que eles a realizem no sistema. | 5 | RF-06 | ✅ |
| 3 | Média | **PB08** — Como professor, quero agendar uma prova em uma data específica para que os alunos associados possam realizá-la naquele dia. | 3 | RF-07 | ⬜ |
| 4 | Média | **PB06** — Como aluno, quero visualizar as provas associadas a mim para saber se fui bem nas provas que fiz e quais serão as próximas. | 3 | RF-05 | ⬜ |
| 5 | Média | **PB09** — Como aluno, quero realizar as provas associadas a mim para ficar com nota acima da média na faculdade. | 8 | RF-06 | ⬜ |
| 6 | Média | **PB13** — Como professor, quero que o sistema calcule e aplique a nota final da prova após a correção para não precisar calcular manualmente a nota de cada aluno. | 5 | RF-10 | ⬜ |
| 7 | Baixa | **PB10** — Como professor, quero salvar as provas em meu computador para enviar o arquivo da prova em outros canais de mensagem. | 5 | RF-08 | ⬜ |
| 8 | Baixa | **PB11** — Como aluno, quero salvar as minhas provas em meu computador para consultá-las depois com mais facilidade. | 3 | RF-08 | ⬜ |
| 9 | Baixa | **PB15** — Como professor, quero que as questões das provas sejam embaralhadas ao serem aplicadas para cada aluno para dificultar que eles colem uns dos outros. | 3 | RF-13 | ⬜ |
| 10 | Baixa | **PB16** — Como professor, quero que as alternativas das questões das provas sejam embaralhadas ao serem aplicadas para cada aluno para dificultar que eles colem uns dos outros. | 3 | RF-12 | ⬜ |
| 11 | Baixa | **PB27** — Como professor, quero salvar as questões em meu computador para enviar o arquivo da questão em outros canais de mensagem. | 3 | RF-26 | ⬜ |

**Total de Story Points da Sprint:** 46

**Período:** 02/11 a 22/11

---

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

## 🧩 Pré-requisitos técnicos da Sprint <a id="prerequisitos"></a>

Itens que não são User Stories, mas precisam existir para que as histórias da sprint possam ser desenvolvidas:

- **Acesso por perfil:** ao abrir o sistema, o usuário informa seu registro na tela de login e o sistema o reconhece como **professor** ou **aluno** pelo campo `tipo_usuario` da tabela `usuario`. O usuário logado fica disponível para as telas durante a sessão. Autenticação com senha fica fora do escopo desta sprint.
- **Estado "aplicada" da prova:** com a PB07, uma prova aplicada deixa de poder ser editada (PB03) ou excluída (PB04), cumprindo a regra de negócio da PB04 que dependia desta sprint.
- **Persistência das respostas:** as respostas de cada aluno, a ordem em que as questões e alternativas foram exibidas e a nota calculada precisam ser gravadas no banco (PB09, PB13, PB15 e PB16).

---

## ✅ DoR por User Story <a id="dor-stories"></a>

### PB21 — Escolher o tipo da questão
- **Critério de Aceitação:** Ao criar uma questão o professor deve poder escolher entre questões de alternativa (**a** à **e**), questões de associação ou de texto livre.
- **Regras de Negócio:**
  - Toda questão tem exatamente um tipo, escolhido na criação (RN5).
  - **Alternativa:** mantém as regras da Sprint 2: de 4 a 5 alternativas, com até 300 caracteres cada e exatamente uma correta.
  - **Associação:** de 2 a 5 pares (item da coluna A e resposta correspondente da coluna B), cada lado com até 300 caracteres e sem itens repetidos.
  - **Texto livre:** não tem resposta correta cadastrada; a correção é feita manualmente pelo professor (PB13).
  - O enunciado segue a regra da PB22 (de 5 a 500 caracteres) para todos os tipos.
  - Ao editar uma questão (PB18), a troca de tipo descarta as alternativas ou pares incompatíveis após confirmação do professor, e não é permitida se a questão estiver em uma prova já aplicada.
- **Dependências:** PB17 e PB18 (Sprint 2).
- **Escopo:** Seletor de tipo na criação e na edição da questão, cadastro dos pares de associação, exibição do tipo na lista de questões (PB20) e correção automática das questões de associação (cada par certo vale a fração correspondente do peso da questão).
- **Protótipo:** [Card de criação de questão](../../prototipos/card-criar-questao.png) ([Figma](https://www.figma.com/design/6aOVWSEUFLoiedYlltR30I?node-id=5-1090)) como base. **Pendente:** incluir o seletor de tipo e o layout dos pares de associação no Figma.

### PB07 — Aplicar prova a um ou mais alunos
- **Critério de Aceitação:** O professor pode aplicar uma prova a vários alunos ou individualmente.
- **Regras de Negócio:**
  - Só é possível aplicar provas com pelo menos uma questão, e todas as questões precisam estar completas (alternativa correta definida ou pares de correspondência cadastrados). As duas regras são verificadas no serviço (`ProvaServico.salvarAlunos`), não só na tela.
  - Um mesmo aluno não pode ser associado duas vezes à mesma prova (registros repetidos ou vazios são ignorados; a tabela `prova_aluno` tem chave primária composta).
  - Ao ser aplicada, a prova passa para o estado **Aplicada** e não pode mais ser editada (PB03) nem excluída (PB04).
  - É possível remover um aluno da aplicação enquanto ele ainda não tiver realizado a prova.
  - O professor só pode aplicar as suas próprias provas (RN2). O professor é identificado pelo registro informado na tela de login e gravado na prova (`registro_professor`) no momento da criação; a mesma verificação vale para editar, excluir, agendar e compor a prova, e é feita tanto nos botões da tela quanto no serviço.
- **Dependências:** PB02 e PB14 (Sprint 2) e a entidade Aluno (ver pré-requisitos).
- **Escopo:** Tela para selecionar um ou mais alunos e associá-los à prova, listagem dos alunos associados e remoção de associação.
- **Protótipo:** Tela de seleção de alunos ("Alunos da prova") e botão "Aplicar prova" no [Menu de ações da prova](../../prototipos/menu-acoes-prova.png) ([Figma](https://www.figma.com/design/6aOVWSEUFLoiedYlltR30I?node-id=5-15)).
- **Casos de teste:** `app/src/test/java/servicos/ProvaServicoTest.java` (executar com `mvnw -f app/pom.xml test`).

### PB08 — Agendar prova
- **Critério de Aceitação:** O professor pode agendar a prova em alguma data de dia útil da semana.
- **Regras de Negócio:**
  - A data deve ser um dia útil, de segunda a sexta-feira.
  - Não é possível agendar para uma data passada.
  - A data pode ser alterada enquanto nenhum aluno tiver realizado a prova.
- **Dependências:** PB02 (Sprint 2). Relaciona-se com a PB07 e a PB09, já que o aluno só realiza a prova no dia agendado.
- **Escopo:** Seleção da data da prova com validação de dia útil e de data passada. O botão "Agendar prova" já existe na tela de provas, mas ainda sem essas validações. Horário de início e término fica fora do escopo (registrado como pendência em [requisitos.md](../../requisitos.md)).
- **Protótipo:** [Menu de ações da prova](../../prototipos/menu-acoes-prova.png) ([Figma](https://www.figma.com/design/6aOVWSEUFLoiedYlltR30I?node-id=5-15)), botão "Agendar prova".

### PB06 — Aluno visualizar suas provas
- **Critério de Aceitação:** Ao consultar as provas como aluno deve ser possível visualizá-las em detalhes, como estado da prova, disciplina, conteúdo, quantidade de questões, com maiores detalhes após a realização da prova.
- **Regras de Negócio:**
  - O aluno visualiza somente as provas associadas a ele (RN4).
  - Estados exibidos para o aluno: **Agendada** (data futura), **Disponível** (dia da prova e ainda não realizada), **Realizada** (aguardando nota) e **Corrigida** (nota disponível).
  - Antes da realização, o aluno não vê o conteúdo das questões. Após a correção, vê a nota, as próprias respostas e quais acertou.
- **Dependências:** PB07 e PB08. Os detalhes após a realização dependem da PB09 e da PB13.
- **Escopo:** Tela inicial do aluno com a lista das provas associadas e o detalhe de cada uma.
- **Protótipo:** [Tela de consulta de provas](../../prototipos/tela-consultar-provas.png) e [Card de consulta de prova](../../prototipos/card-consultar-prova.png) ([Figma](https://www.figma.com/design/6aOVWSEUFLoiedYlltR30I?node-id=5-1657)) como base. **Pendente:** adaptar a versão do aluno no Figma.

### PB09 — Aluno realizar prova
- **Critério de Aceitação:** Ao ter uma prova associada a seu nome, o aluno deve poder realizar a prova no sistema no dia em que foi agendada.
- **Regras de Negócio:**
  - A prova só pode ser iniciada na data agendada e por alunos associados a ela.
  - Cada aluno realiza a prova uma única vez. Após a entrega, as respostas não podem ser alteradas.
  - Antes de entregar, o sistema confirma com o aluno e avisa se houver questões sem resposta. Questões sem resposta contam como erradas (PB01).
  - Ao entregar, as questões de alternativa e de associação são corrigidas automaticamente (PB01 e PB21).
- **Dependências:** PB07, PB08 e PB01 (Sprint 2). Questões de associação e texto livre dependem da PB21.
- **Escopo:** Tela de realização da prova com uma resposta por questão (alternativa marcada, pares associados ou texto livre), entrega e gravação das respostas no banco.
- **Protótipo:** [Card de visualização de questão](../../prototipos/card-visualizar-questao.png) como base para cada questão. **Pendente:** criar a tela de realização no Figma.

### PB13 — Calcular e aplicar a nota final
- **Critério de Aceitação:** O professor pode ver a nota da prova corrigida individualmente em cada prova.
- **Regras de Negócio:**
  - A nota é calculada na escala de 0 a 10: (soma dos pesos das questões acertadas ÷ soma dos pesos de todas as questões) × 10, com duas casas decimais (RN7).
  - Questões de associação valem a fração do peso correspondente aos pares acertados.
  - Questões de texto livre são pontuadas manualmente pelo professor, de 0 até o peso da questão (RN6). Enquanto houver questão de texto livre sem pontuação, a nota fica como **pendente**.
  - A nota é gravada para cada aluno e passa a aparecer para ele na PB06.
- **Dependências:** PB01 e PB12 (Sprint 2) e PB09.
- **Escopo:** Cálculo e gravação da nota, tela do professor com a lista de alunos de cada prova e suas notas, e correção manual das questões de texto livre. A tela atual de "Calcular resultado" passa a usar as respostas gravadas pelos alunos.
- **Protótipo:** **Pendente:** criar no Figma a tela de notas da prova.

### PB10 — Professor salvar provas no computador
- **Critério de Aceitação:** Professores devem poder salvar todas as provas armazenadas no sistema em seus computadores.
- **Regras de Negócio:**
  - O arquivo contém os dados da prova (código, título, curso, matéria, bimestre e data) e suas questões com pesos e alternativas.
  - O arquivo não indica as respostas corretas, pois se destina a ser enviado para outras pessoas.
  - Deve ser possível salvar uma prova selecionada ou todas as provas de uma vez, gerando um arquivo por prova na pasta escolhida.
- **Dependências:** PB02 e PB14 (Sprint 2). Questões de associação e texto livre dependem da PB21.
- **Escopo:** Exportação em `.txt`. A exportação de uma prova pelo botão "Baixar prova" já existe; falta a exportação de todas as provas e o suporte aos novos tipos de questão.
- **Protótipo:** [Menu de ações da prova](../../prototipos/menu-acoes-prova.png) ([Figma](https://www.figma.com/design/6aOVWSEUFLoiedYlltR30I?node-id=5-15)), botão "Baixar prova".

### PB11 — Aluno salvar suas provas no computador
- **Critério de Aceitação:** Alunos devem poder salvar as provas que realizaram em seus computadores.
- **Regras de Negócio:**
  - O aluno só pode salvar as provas que ele mesmo realizou (USO22).
  - O arquivo contém as questões, as respostas do aluno e, quando a prova estiver corrigida, o resultado de cada questão e a nota final.
- **Dependências:** PB06, PB09 e PB13.
- **Escopo:** Exportação em `.txt` a partir do detalhe da prova na área do aluno.
- **Protótipo:** **Pendente:** incluir o botão "Baixar prova" na versão do aluno da tela de consulta.

### PB15 — Embaralhar questões por aluno
- **Critério de Aceitação:** Cada aluno realiza a prova com as questões embaralhadas com critérios aleatórios.
- **Regras de Negócio:**
  - A ordem das questões é sorteada para cada aluno no momento da aplicação (PB07).
  - A ordem sorteada é gravada, para que o aluno veja sempre a mesma ordem, inclusive ao baixar a prova (PB11).
  - A correção e a nota não dependem da ordem das questões.
- **Dependências:** PB07 e PB09.
- **Escopo:** Sorteio e gravação da ordem das questões por aluno, e exibição nessa ordem na realização da prova.
- **Protótipo:** Não se aplica (processamento interno).

### PB16 — Embaralhar alternativas por aluno
- **Critério de Aceitação:** Cada aluno realiza a prova com as alternativas das questões embaralhadas com critérios aleatórios.
- **Regras de Negócio:**
  - Nas questões de alternativa, a ordem das alternativas é sorteada para cada aluno. As letras (**a** à **e**) seguem a ordem exibida.
  - Nas questões de associação, a coluna de respostas é embaralhada.
  - A ordem sorteada é gravada, e a correção usa a alternativa escolhida, não a letra exibida.
- **Dependências:** PB07, PB09 e PB21.
- **Escopo:** Sorteio e gravação da ordem das alternativas por aluno, e exibição nessa ordem na realização da prova.
- **Protótipo:** Não se aplica (processamento interno).

### PB27 — Professor salvar questões no computador
- **Critério de Aceitação:** Professores devem poder salvar todas as questões armazenadas no sistema em seus computadores.
- **Regras de Negócio:**
  - Deve ser possível salvar uma questão selecionada ou todas as questões do banco de questões em um único arquivo.
  - O professor escolhe se o arquivo inclui ou não a indicação da resposta correta.
  - O arquivo inclui o tipo de cada questão (PB21).
- **Dependências:** PB17 e PB20 (Sprint 2). Os tipos dependem da PB21.
- **Escopo:** Exportação em `.txt` a partir do banco de questões.
- **Protótipo:** [Lista de questões](../../prototipos/lista-questoes.png) ([Figma](https://www.figma.com/design/6aOVWSEUFLoiedYlltR30I?node-id=5-423)) como base. **Pendente:** incluir o botão "Baixar questões" no menu do banco de questões.
