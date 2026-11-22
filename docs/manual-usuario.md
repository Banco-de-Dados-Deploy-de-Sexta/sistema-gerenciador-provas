# 📖 Manual do Usuário

A aplicação abre na tela de **Login**. O menu lateral de cada tela concentra as ações disponíveis.

## Login

Informe o seu **registro** (ex.: `SGP-P1231` para professor ou `SGP-A1231` para aluno) e clique em **Entrar**. O sistema identifica se você é professor ou aluno pelo cadastro. Nesta versão não há senha.

- **Professor:** é levado à tela de **Provas**. O nome e o registro do professor aparecem no topo do menu lateral.
- **Aluno:** é levado à área do aluno, com as provas associadas a ele (veja [Área do aluno](#área-do-aluno)).

Use **Sair / Voltar** no menu lateral para encerrar a sessão e voltar ao login.

---

# Professor

## Banco de questões

Acesse pelo botão **Questões** na tela de Provas.

- **Visualizar:** clique em uma questão da lista para ver o enunciado e as alternativas, com a resposta correta marcada.
- **Criar questão:** clique em **Criar questão**, escolha o **tipo de questão**, digite a pergunta (de 5 a 500 caracteres) e preencha os itens do tipo escolhido. Clique em **Confirmar** para salvar.
- **Editar questão:** selecione a questão e clique em **Editar questão**. O tipo é definido na criação e não muda na edição. Questões que estão em uma prova já **aplicada** a alunos não podem mais ser alteradas.
- **Excluir questão:** selecione a questão e clique em **Excluir questão**. Só é possível excluir questões que **não estão em nenhuma prova**. Se ela estiver vinculada, remova-a das provas em **Questões e pesos** antes.
- **Baixar questões:** clique em **Baixar questões**, escolha entre a questão selecionada ou todas as questões do banco e se o arquivo deve indicar as respostas corretas. É gerado um único arquivo `.txt`.

### Tipos de questão

| Tipo | Como preencher | Correção |
|------|----------------|----------|
| **Múltipla escolha** | De 4 a 5 alternativas (a até e), com até 300 caracteres cada. Marque exatamente uma como correta. | Automática: vale o peso inteiro se a alternativa marcada for a correta. |
| **Verdadeiro ou falso em sequência** | De 4 a 5 afirmações, cada uma marcada como V ou F. | Automática e proporcional: cada afirmação certa vale uma fração do peso. |
| **Correspondência** | De 4 a 5 pares de conceito e definição, sem conceitos ou definições repetidos. | Automática e proporcional: cada par certo vale uma fração do peso. |
| **Dissertativa** | Apenas o enunciado. | Manual: o professor atribui uma nota de zero até o peso da questão. |

## Provas

Todas as provas cadastradas aparecem na lista, de todos os professores. Só o professor **dono** da prova (quem a criou) pode editá-la, excluí-la, agendá-la, montar suas questões, aplicá-la e ver seus resultados; as demais servem de consulta e modelo.

- **Visualizar:** cada card mostra código, data, nome do professor dono, título, curso, matéria, bimestre, quantidade de questões e o **estado** da prova. Ao clicar em uma prova, as questões dela aparecem com seus pesos.
  - **Rascunho:** a prova ainda não tem questões.
  - **Pronta:** tem questões, mas ainda não tem data.
  - **Agendada:** tem questões e data de hoje em diante.
  - **Encerrada:** a data da prova já passou.
  - **Aplicada:** a prova já foi aplicada a pelo menos um aluno.
- **Criar prova:** clique em **Criar prova**, preencha título (até 100 caracteres), matéria, curso, bimestre (1 a 4) e, se quiser, a data e os horários, e clique em **Salvar prova**. O código (ex.: `PRV001`) é gerado automaticamente e a prova fica registrada em nome do professor logado.
- **Editar prova:** selecione a prova e clique em **Editar prova**. A data e os horários só são validados novamente se forem alterados, então é possível corrigir os dados de uma prova cuja data já passou. Não é possível editar uma prova **Aplicada**.
- **Excluir prova:** selecione a prova e clique em **Excluir prova**. É pedida uma confirmação. As questões continuam no banco de questões. Não é possível excluir uma prova **Aplicada**.
- **Agendar prova:** selecione a prova e clique em **Agendar prova**. Escolha a data (só dias úteis do semestre letivo atual, de hoje em diante), o horário de início (obrigatório) e, se quiser, o horário de término (no mesmo dia e depois do início). Sem horário de término, a prova fica disponível até o fim do dia agendado. Depois que algum aluno entregar a prova, a data e os horários não podem mais ser alterados, e o botão fica desabilitado.
- **Baixar prova:** selecione a prova e clique em **Baixar prova** para salvar um arquivo `.txt` com os dados da prova, as questões, os pesos e as alternativas, sem indicar as respostas corretas.
- **Baixar todas as provas:** clique em **Baixar todas as provas** e escolha uma pasta. É gerado um arquivo `.txt` por prova.

## Montar a prova (questões e pesos)

Selecione uma prova e clique em **Questões e pesos**. Escolha as questões do banco que farão parte da prova e defina o peso de cada uma (maior que zero; o padrão é 1). Clique em **Salvar composição** para gravar. Uma mesma questão não pode ser adicionada duas vezes à mesma prova. A composição de uma prova **Aplicada** não pode mais ser alterada.

## Aplicar prova a alunos

Selecione uma prova sua e clique em **Aplicar prova**. A tela **Alunos da prova** lista todos os alunos cadastrados; marque os que farão a prova, individualmente ou com **Marcar todos** / **Desmarcar todos**, e clique em **Salvar alunos**.

- A prova precisa ter **pelo menos uma questão** para ser aplicada, e todas as questões precisam estar **completas**. Se alguma questão estiver incompleta, o sistema avisa qual é.
- Um mesmo aluno não pode ser vinculado duas vezes à mesma prova.
- Ao salvar com pelo menos um aluno marcado, a prova passa ao estado **Aplicada** e os botões **Editar prova**, **Excluir prova** e **Questões e pesos** ficam desabilitados para ela. As questões da prova também deixam de poder ser editadas no banco de questões.
- Para **remover** um aluno da aplicação, abra **Aplicar prova** de novo, desmarque o aluno e clique em **Salvar alunos**. Não é possível remover um aluno que já entregou a prova.
- Lembre-se de **agendar** a prova: o aluno só consegue realizá-la no período agendado.

## Corrigir dissertativas

Selecione uma prova sua e clique em **Corrigir dissertativas**. Escolha a entrega de um aluno e atribua a cada resposta aberta uma nota de zero até o peso da questão. Enquanto houver resposta dissertativa sem nota, a nota final do aluno fica **pendente**; quando todas forem corrigidas, a nota final é calculada e liberada para o aluno.

## Visualizar resultados

Selecione uma prova sua e clique em **Visualizar resultados**. A tela mostra a nota média, a maior e a menor nota e a lista de alunos com a nota de cada um, de 0 a 10. Clique em um aluno para ver a resposta enviada, o gabarito e a pontuação de cada questão.

A nota é calculada assim: (soma dos pontos obtidos ÷ soma dos pesos de todas as questões) × 10, com duas casas decimais. Questões em branco contam como erradas.

## Calcular resultado (simulação)

O botão **Calcular resultado** abre uma simulação de correção: o professor marca as respostas de uma folha de respostas e o sistema calcula os pontos das questões objetivas. Essa tela não grava nada e não considera as questões dissertativas. As notas oficiais dos alunos ficam em **Visualizar resultados**.

---

# Área do aluno

Ao entrar com um registro de aluno, aparece a lista das provas **associadas a você**, com o estado de cada uma:

- **Agendada:** a prova ainda não começou.
- **Disponível:** está no período de realização e você ainda não a entregou.
- **Encerrada:** o período da prova terminou sem entrega.
- **Realizada:** você entregou e a nota aguarda a correção das questões dissertativas.
- **Corrigida:** a nota final está disponível.

## Realizar prova

Selecione uma prova **Disponível** e clique em **Realizar prova**. As questões e as alternativas aparecem em uma ordem sorteada para você, que se mantém igual se você sair e voltar. Responda e clique em **Entregar prova**.

- Antes da entrega, o sistema confirma e avisa quantas questões estão sem resposta. Questões em branco contam como erradas.
- Cada prova pode ser entregue **uma única vez**, e as respostas não podem ser alteradas depois.
- A entrega só é aceita **dentro do período agendado**. Se o horário de término passar, a entrega é recusada.
- Use **Limpar respostas** para apagar o que marcou e **Cancelar e Sair** para sair sem entregar.

## Ver e baixar a minha prova

Selecione uma prova que você entregou e clique em **Ver / baixar minha prova**. Aparecem as questões na mesma ordem em que você as viu, as suas respostas e, depois da correção, a resposta correta, a pontuação de cada questão e a nota final. Clique em **Baixar prova** para salvar tudo em um arquivo `.txt`.
