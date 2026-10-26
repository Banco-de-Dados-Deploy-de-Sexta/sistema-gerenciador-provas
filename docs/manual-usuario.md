# 📖 Manual do Usuário

A aplicação abre na tela de **Provas**. O menu lateral de cada tela concentra as ações disponíveis.

## Banco de questões

Acesse pelo botão **Questões** na tela de Provas.

- **Visualizar:** clique em uma questão da lista para ver o enunciado e as alternativas, com a correta marcada.
- **Criar questão:** digite a pergunta (de 5 a 500 caracteres), escreva cada alternativa e clique em **Adicionar alternativa**. Uma questão tem de **4 a 5 alternativas** (a até e), com até 300 caracteres cada. Marque a alternativa correta no botão de seleção ao lado dela (exatamente uma) e clique em **Confirmar**.
- **Editar questão:** selecione a questão e clique em **Editar questão**. É possível alterar o enunciado, **editar o texto de qualquer alternativa** diretamente no campo, remover alternativas (ícone ✕) e trocar a correta. O texto de uma alternativa não pode ficar vazio. Se a alternativa correta for removida, marque outra antes de confirmar.
- **Excluir questão:** selecione a questão e clique em **Excluir questão**. Só é possível excluir questões que **não estão em nenhuma prova**. Se ela estiver vinculada, remova-a das provas em **Questões e pesos** antes.

## Provas

- **Visualizar:** cada card mostra código, data, título, curso, matéria, bimestre, quantidade de questões e o **estado** da prova. Ao clicar em uma prova, as questões dela aparecem com seus pesos.
  - **Rascunho:** a prova ainda não tem questões.
  - **Pronta:** tem questões, mas ainda não tem data.
  - **Agendada:** tem questões e data de hoje em diante.
  - **Encerrada:** a data da prova já passou.
- **Criar prova:** clique em **Criar prova**, preencha título, matéria, curso, bimestre (1 a 4) e, se quiser, a data, e clique em **Salvar prova**. O código (ex.: `PRV0001`) é gerado automaticamente.
- **Editar prova:** selecione a prova e clique em **Editar prova**.
- **Excluir prova:** selecione a prova e clique em **Excluir prova**. É pedida uma confirmação. As questões continuam no banco de questões.
- **Agendar prova:** selecione a prova, clique em **Agendar prova** e escolha a data.
- **Baixar prova:** selecione a prova e clique em **Baixar prova** para salvar um arquivo `.txt` com as questões e alternativas (sem indicar as corretas).

## Montar a prova (questões e pesos)

Selecione uma prova e clique em **Questões e pesos**. Escolha as questões do banco que farão parte da prova e defina o peso de cada uma (maior que zero; o padrão é 1). Clique em **Salvar composição** para gravar. Uma mesma questão não pode ser adicionada duas vezes à mesma prova.

## Corrigir prova

Selecione uma prova e clique em **Calcular resultado** no menu lateral. Para cada questão, marque a alternativa que o aluno respondeu e clique em **Calcular resultado**. O sistema soma o peso das questões acertadas e mostra os pontos obtidos sobre o total possível. Questões sem resposta contam como erradas. Use **Limpar respostas** para corrigir a prova de outro aluno.
