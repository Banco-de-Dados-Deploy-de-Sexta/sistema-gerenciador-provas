# Sistema gerenciador de provas

## Objetivo

- Criar um sistema para corrigir questões de provas

## Requisitos funcionais

- RF1 - Corrigir questões da prova
- RF2 - Criar e armazenar prova
- RF3 - Editar prova
- RF4 - Excluir prova
- RF5 - Visualizar prova
- RF6 - Realizar prova
- RF7 - Agendar prova
- RF8 - Salvar prova
- RF9 - Definir peso das questões
- RF10 - Calcular nota da prova
- RF11 - Atribuir questão à prova
- RF12 - Embaralhar alternativas
- RF13 - Embaralhar questões
- RF14 - Definir cálculo prova
- RF15 - Criar e armazenar questão
- RF16 - Editar questão
- RF17 - Excluir questão
- RF18 - Vizualizar questão
- RF19 - Vizualizar todas as questões disponíves
- RF20 - Definir tipo de questão
- RF21 - Escrever pergunta da questão
- RF22 - Criar e armazenar alternativas
- RF23 - Editar alternativas
- RF24 - Excluir alternativas
- RF25 - Definir alternativa correta
- RF26 - Salvar questão

## Requisitos não funcionais

- RNF1 - Reaproveitar questões passadas
- RNF2 - Criar questões dissertativas

## Entidades do domínio

- E1 - Prova
- E2 - Questão

## Agentes

- A1 - Professor
- A2 - Aluno

## Caso de uso

USO# - O aluno pode consultar o [gabarito] da prova

- USO1 - O professor pode corrigir questões da prova
- USO2 - O professor pode criar e armazenar prova 
- USO3 - O professor pode editar prova
- USO4 - O professor pode excluir prova
- USO5 - O professor pode visualizar provas
- USO6 - O professor pode agendar prova
- USO7 - O professor pode aplicar provas
- USO7 - O professor pode salvar prova
- USO8 - O professor pode definir peso das questões
- USO9 - O professor pode atribuir questão da prova
- USO10 - O professor pode embaralhar questões da prova
- USO11 - O professor pode embaralhar questões da prova
- USO12 - O professor pode definir cálculo da prova
- USO13 - O professor pode criar questão e armazenar
- USO14 - O professor pode definir tipo de questão
- USO15 - O professor pode escrever a pergunta da questão
- USO16 - O professor pode salvar questão
- USO16 - O professor pode criar alternativas
- USO17 - O professor pode editar alternativas
- USO18 - O professor pode excluir alternativas
- USO19 - O professor pode definir alternativa correta
- USO20 - O aluno pode visualizar provas (somente as que ele realizou)
- USO21- O aluno pode realizar a prova
- USO22 - O aluno pode salvar somente as provas dele

## Regras de negócio

- RN1 - O professor pode visualizar somente as sua provas
- RN2 - O professor pode aplicar somente as suas provas
- RN3 - O professor pode reaproveitar passadas
- RN4 - O aluno pode visualizar somente as suas questões
- RN5 - O sistema deve permitir diferentes tipos de questões
- RN6 - O sistema deve auxiliar na correção de questões dissertativas
- RN7 - O sistema deve calcular nota da prova


RN# - O sistema deve gerar um [gabarito] //TODO: criar entidade de domínio gabarito
RN# - O professor deve criar avaliação com data e hora de início e término //TODO: definir data e hora como obrigatório na entidade prova
RN# - O [semestre] deve ter no mínimo duas provas por matéria //TODO: criar entidade semestre
