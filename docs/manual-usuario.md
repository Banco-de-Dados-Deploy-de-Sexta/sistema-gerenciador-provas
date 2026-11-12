# 📖 Manual do Usuário

A aplicação abre na tela de **Login**. O menu lateral de cada tela concentra as ações disponíveis.

## Login

Informe o seu **registro** (ex.: `SGP-P1231` para professor ou `SGP-A1231` para aluno) e clique em **Entrar**. O sistema identifica se você é professor ou aluno pelo cadastro. Nesta versão não há senha.

- **Professor:** é levado à tela de **Provas**. O nome e o registro do professor aparecem no topo do menu lateral.
- **Aluno:** recebe uma mensagem de boas-vindas. A área do aluno ainda não está disponível.

Use **Sair / Voltar** no menu lateral para encerrar a sessão e voltar ao login.

## Banco de questões

Acesse pelo botão **Questões** na tela de Provas.

- **Visualizar:** clique em uma questão da lista para ver o enunciado e as alternativas, com a correta marcada.
- **Criar questão:** digite a pergunta (de 5 a 500 caracteres), escreva cada alternativa e clique em **Adicionar alternativa**. Uma questão tem de **4 a 5 alternativas** (a até e), com até 300 caracteres cada. Marque a alternativa correta no botão de seleção ao lado dela (exatamente uma) e clique em **Confirmar**.
- **Editar questão:** selecione a questão e clique em **Editar questão**. É possível alterar o enunciado, **editar o texto de qualquer alternativa** diretamente no campo, remover alternativas (ícone ✕) e trocar a correta. O texto de uma alternativa não pode ficar vazio. Se a alternativa correta for removida, marque outra antes de confirmar.
- **Excluir questão:** selecione a questão e clique em **Excluir questão**. Só é possível excluir questões que **não estão em nenhuma prova**. Se ela estiver vinculada, remova-a das provas em **Questões e pesos** antes.

## Provas

Todas as provas cadastradas aparecem na lista, de todos os professores. Só o professor **dono** da prova (quem a criou) pode editá-la, excluí-la, agendá-la, montar suas questões ou aplicá-la; as demais servem de consulta e modelo.

- **Visualizar:** cada card mostra código, data, nome do professor dono, título, curso, matéria, bimestre, quantidade de questões e o **estado** da prova. Ao clicar em uma prova, as questões dela aparecem com seus pesos.
  - **Rascunho:** a prova ainda não tem questões.
  - **Pronta:** tem questões, mas ainda não tem data.
  - **Agendada:** tem questões e data de hoje em diante.
  - **Encerrada:** a data da prova já passou.
  - **Aplicada:** a prova já foi aplicada a pelo menos um aluno (veja [Aplicar prova a alunos](#aplicar-prova-a-alunos)).
- **Criar prova:** clique em **Criar prova**, preencha título (até 100 caracteres), matéria, curso, bimestre (1 a 4) e, se quiser, a data e os horários, e clique em **Salvar prova**. O código (ex.: `PRV001`) é gerado automaticamente e a prova fica registrada em nome do professor logado.
- **Editar prova:** selecione a prova e clique em **Editar prova**. Não é possível editar uma prova **Aplicada**.
- **Excluir prova:** selecione a prova e clique em **Excluir prova**. É pedida uma confirmação. As questões continuam no banco de questões. Não é possível excluir uma prova **Aplicada**.
- **Agendar prova:** selecione a prova e clique em **Agendar prova**. Escolha a data (só dias úteis do semestre letivo atual, de hoje em diante), o horário de início (obrigatório) e, se quiser, o horário de término (no mesmo dia e depois do início). Para hoje, só aparecem horários que ainda não passaram. Depois que algum aluno entregar a prova, a data e os horários não podem mais ser alterados, e o botão fica desabilitado.
- **Baixar prova:** selecione a prova e clique em **Baixar prova** para salvar um arquivo `.txt` com as questões e alternativas (sem indicar as corretas).

## Montar a prova (questões e pesos)

Selecione uma prova e clique em **Questões e pesos**. Escolha as questões do banco que farão parte da prova e defina o peso de cada uma (maior que zero; o padrão é 1). Clique em **Salvar composição** para gravar. Uma mesma questão não pode ser adicionada duas vezes à mesma prova. A composição de uma prova **Aplicada** não pode mais ser alterada.

## Aplicar prova a alunos

Selecione uma prova sua e clique em **Aplicar prova**. A tela **Alunos da prova** lista todos os alunos cadastrados; marque os que farão a prova, individualmente ou com **Marcar todos** / **Desmarcar todos**, e clique em **Salvar alunos**. O subtítulo mostra quantos alunos estão selecionados.

- A prova precisa ter **pelo menos uma questão** para ser aplicada, e todas as questões precisam estar **completas**: alternativa correta marcada (múltipla escolha) ou todos os pares preenchidos (correspondência). Se alguma questão estiver incompleta, o sistema avisa qual é.
- Um mesmo aluno não pode ser vinculado duas vezes à mesma prova.
- Ao salvar com pelo menos um aluno marcado, a prova passa ao estado **Aplicada** e os botões **Editar prova**, **Excluir prova** e **Questões e pesos** ficam desabilitados para ela.
- Para **remover** um aluno da aplicação, abra **Aplicar prova** de novo, desmarque o aluno e clique em **Salvar alunos**. Se nenhum aluno ficar marcado, a prova deixa de estar aplicada e volta a poder ser editada ou excluída.
- Só o professor dono da prova pode aplicá-la, editá-la, excluí-la, agendá-la ou alterar suas questões. Ao tentar fazer isso com uma prova de outro professor, o sistema avisa que não há permissão.

## Corrigir prova

Selecione uma prova e clique em **Calcular resultado** no menu lateral. Para cada questão, marque a alternativa que o aluno respondeu e clique em **Calcular resultado**. O sistema soma o peso das questões acertadas e mostra os pontos obtidos sobre o total possível. Questões sem resposta contam como erradas. Use **Limpar respostas** para corrigir a prova de outro aluno.
