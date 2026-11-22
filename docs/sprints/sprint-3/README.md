
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
| 1 | Média | **PB21** — Como professor, quero escolher entre vários tipos de questão ao criá-la para montar provas com formatos variados. | 5 | RF-20 | ✅ |
| 2 | Média | **PB07** — Como professor, quero aplicar uma prova a um ou mais alunos para que eles a realizem no sistema. | 5 | RF-06 | ✅ |
| 3 | Média | **PB08** — Como professor, quero agendar uma prova em uma data específica para que os alunos associados possam realizá-la naquele dia. | 3 | RF-07 | ✅ |
| 4 | Média | **PB06** — Como aluno, quero visualizar as provas associadas a mim para saber se fui bem nas provas que fiz e quais serão as próximas. | 3 | RF-05 | ✅ |
| 5 | Média | **PB09** — Como aluno, quero realizar as provas associadas a mim para ficar com nota acima da média na faculdade. | 8 | RF-06 | ✅ |
| 6 | Média | **PB13** — Como professor, quero que o sistema calcule e aplique a nota final da prova após a correção para não precisar calcular manualmente a nota de cada aluno. | 5 | RF-10 | ✅ |
| 7 | Baixa | **PB10** — Como professor, quero salvar as provas em meu computador para enviar o arquivo da prova em outros canais de mensagem. | 5 | RF-08 | ✅ |
| 8 | Baixa | **PB11** — Como aluno, quero salvar as minhas provas em meu computador para consultá-las depois com mais facilidade. | 3 | RF-08 | ✅ |
| 9 | Baixa | **PB15** — Como professor, quero que as questões das provas sejam embaralhadas ao serem aplicadas para cada aluno para dificultar que eles colem uns dos outros. | 3 | RF-13 | ✅ |
| 10 | Baixa | **PB16** — Como professor, quero que as alternativas das questões das provas sejam embaralhadas ao serem aplicadas para cada aluno para dificultar que eles colem uns dos outros. | 3 | RF-12 | ✅ |
| 11 | Baixa | **PB27** — Como professor, quero salvar as questões em meu computador para enviar o arquivo da questão em outros canais de mensagem. | 3 | RF-26 | ✅ |

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
- **Critério de Aceitação:** Ao criar uma questão o professor deve poder escolher entre questões de múltipla escolha (**a** à **e**), verdadeiro ou falso em sequência, correspondência (associação) ou dissertativa (texto livre).
- **Regras de Negócio:**
  - Toda questão tem exatamente um tipo, escolhido na criação (RN5). O tipo não muda na edição (PB18).
  - **Múltipla escolha:** mantém as regras da Sprint 2: de 4 a 5 alternativas, com até 300 caracteres cada e exatamente uma correta.
  - **Verdadeiro ou falso em sequência:** de 4 a 5 afirmações, com até 300 caracteres cada, cada uma marcada como verdadeira ou falsa.
  - **Correspondência:** de 4 a 5 pares (conceito e definição correspondente), cada lado com até 300 caracteres e sem conceitos ou definições repetidos.
  - **Dissertativa:** não tem alternativas nem resposta correta cadastrada; a correção é feita manualmente pelo professor (PB13).
  - O enunciado segue a regra da PB22 (de 5 a 500 caracteres) para todos os tipos.
  - As regras são verificadas na tela e no serviço (`QuestaoServico`).
  - Uma questão que está em uma prova já aplicada a alunos não pode mais ser editada, para não alterar respostas e notas já registradas.
- **Dependências:** PB17 e PB18 (Sprint 2).
- **Escopo:** Seletor de tipo na criação da questão, formulário próprio de cada tipo, exibição do tipo na lista de questões (PB20) e correção automática das questões de verdadeiro ou falso e de correspondência (cada afirmação ou par certo vale a fração correspondente do peso da questão).
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
- **Casos de teste:** `app/test/java/servicos/ProvaServicoTest.java` (executar com `mvnw -f app/pom.xml test`).

### PB08 — Agendar prova
- **Critério de Aceitação:** O professor pode agendar a prova em alguma data de dia útil da semana.
- **Regras de Negócio:**
  - A data deve ser um dia útil, de segunda a sexta-feira.
  - Não é possível agendar para uma data passada nem, no dia de hoje, para um horário que já passou.
  - A data deve estar no **semestre letivo atual**: 1º semestre de janeiro a junho, 2º semestre de julho a dezembro, sempre do ano corrente. O calendário só habilita essas datas.
  - Ao **criar ou editar** a prova pelo formulário, o horário é opcional: a prova pode ficar só com a data.
  - Ao **agendar** pelo botão "Agendar prova", o horário de início é obrigatório. O horário de término é opcional, precisa ser no mesmo dia e posterior ao início.
  - Data e horários podem ser alterados enquanto nenhum aluno tiver realizado a prova. Após a primeira entrega, o botão "Agendar prova" fica desabilitado e o serviço recusa a alteração.
  - Ao editar os demais dados de uma prova, a data e os horários só são validados novamente se forem alterados. Assim, é possível corrigir os dados de uma prova não aplicada cuja data já passou.
  - Só o professor dono da prova pode agendá-la (RN2).
- **Dependências:** PB02 (Sprint 2). Relaciona-se com a PB07 e a PB09, já que o aluno só realiza a prova no período agendado (entre início e término, ou durante o dia agendado quando não há término).
- **Escopo:** Seleção de data e horários no formulário da prova e no diálogo "Agendar prova", com validação de dia útil, data passada, semestre letivo, horário passado e término posterior ao início, feita nas telas e no serviço (`ProvaServico`).
- **Protótipo:** [Menu de ações da prova](../../prototipos/menu-acoes-prova.png) ([Figma](https://www.figma.com/design/6aOVWSEUFLoiedYlltR30I?node-id=5-15)), botão "Agendar prova".
- **Casos de teste:** `app/test/java/servicos/ProvaServicoTest.java`, classes "Agendar prova (PB08)" e "Reagendar com entregas (PB08)".

### PB06 — Aluno visualizar suas provas
- **Critério de Aceitação:** Ao consultar as provas como aluno deve ser possível visualizá-las em detalhes, como estado da prova, disciplina, conteúdo, quantidade de questões, com maiores detalhes após a realização da prova.
- **Regras de Negócio:**
  - O aluno visualiza somente as provas associadas a ele (RN4).
  - Estados exibidos para o aluno: **Agendada** (data futura), **Disponível** (dentro do período agendado e ainda não realizada), **Encerrada** (período terminado sem entrega), **Realizada** (aguardando nota) e **Corrigida** (nota disponível).
  - Antes da realização, o aluno não vê o conteúdo das questões. Após a correção, vê a nota, as próprias respostas e quais acertou.
- **Dependências:** PB07 e PB08. Os detalhes após a realização dependem da PB09 e da PB13.
- **Escopo:** Tela inicial do aluno com a lista das provas associadas e o detalhe de cada uma.
- **Protótipo:** [Tela de consulta de provas](../../prototipos/tela-consultar-provas.png) e [Card de consulta de prova](../../prototipos/card-consultar-prova.png) ([Figma](https://www.figma.com/design/6aOVWSEUFLoiedYlltR30I?node-id=5-1657)) como base. **Pendente:** adaptar a versão do aluno no Figma.

### PB09 — Aluno realizar prova
- **Critério de Aceitação:** Ao ter uma prova associada a seu nome, o aluno deve poder realizar a prova no sistema no dia em que foi agendada.
- **Regras de Negócio:**
  - A prova só pode ser iniciada na data agendada e por alunos associados a ela.
  - A entrega só é aceita dentro do período agendado. O serviço (`ProvaServico.entregarProva`) recusa entregas feitas depois do horário de término e entregas de alunos não associados à prova.
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
- **Escopo:** Cálculo e gravação da nota, tela do professor com a lista de alunos de cada prova e suas notas, e correção manual das questões dissertativas. As notas e as respostas gravadas pelos alunos ficam na tela "Visualizar resultados"; a tela "Calcular resultado" permanece como simulação de correção, sem gravar nada e sem considerar as questões dissertativas.
- **Protótipo:** **Pendente:** criar no Figma a tela de notas da prova.

### PB10 — Professor salvar provas no computador
- **Critério de Aceitação:** Professores devem poder salvar todas as provas armazenadas no sistema em seus computadores.
- **Regras de Negócio:**
  - O arquivo contém os dados da prova (código, título, curso, matéria, bimestre e data) e suas questões com pesos e alternativas.
  - O arquivo não indica as respostas corretas, pois se destina a ser enviado para outras pessoas.
  - Deve ser possível salvar uma prova selecionada ou todas as provas de uma vez, gerando um arquivo por prova na pasta escolhida.
- **Dependências:** PB02 e PB14 (Sprint 2). Questões de associação e texto livre dependem da PB21.
- **Escopo:** Exportação em `.txt` de uma prova (botão "Baixar prova") ou de todas as provas (botão "Baixar todas as provas"), com suporte a todos os tipos de questão.
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
  - A ordem das questões é sorteada para cada aluno a partir de uma semente derivada do código da prova e do registro do aluno. A ordem é a mesma sempre que o aluno abre a prova e é diferente entre alunos.
  - A posição de cada questão é gravada na entrega (`resposta_aluno.ordem_questao`), e o aluno vê a mesma ordem ao consultar e ao baixar a prova (PB11).
  - A correção e a nota não dependem da ordem das questões.
- **Dependências:** PB07 e PB09.
- **Escopo:** Sorteio reproduzível da ordem das questões por aluno, exibição nessa ordem na realização e na consulta da prova.
- **Protótipo:** Não se aplica (processamento interno).

### PB16 — Embaralhar alternativas por aluno
- **Critério de Aceitação:** Cada aluno realiza a prova com as alternativas das questões embaralhadas com critérios aleatórios.
- **Regras de Negócio:**
  - Nas questões de múltipla escolha, a ordem das alternativas é sorteada para cada aluno. As letras (**a** à **e**) seguem a ordem exibida.
  - Nas questões de verdadeiro ou falso, as afirmações são embaralhadas; nas de correspondência, os conceitos e as definições são embaralhados.
  - O sorteio usa a mesma semente da PB15 (prova e aluno), então a ordem é a mesma na realização, na consulta e no arquivo baixado pelo aluno. A correção usa a alternativa, afirmação ou definição escolhida, não a posição exibida.
- **Dependências:** PB07, PB09 e PB21.
- **Escopo:** Sorteio reproduzível da ordem das alternativas, afirmações e pares por aluno, e exibição nessa ordem na realização e na consulta da prova.
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
