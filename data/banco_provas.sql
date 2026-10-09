CREATE DATABASE IF NOT EXISTS banco;
USE banco;

CREATE TABLE prova (
  codigo varchar(7) primary key,
  titulo text not null,
  materia varchar(50) not null,
  data_prova datetime,
  curso varchar(50),
  bimestre int
);

CREATE TABLE questao (
  id_questao int primary key,
  titulo varchar(50) not null,
  descricao varchar(300) not null
);

CREATE TABLE prova_questao (
  codigo_prova varchar(7),
  id_questao int,

  PRIMARY KEY(codigo_prova, id_questao),

  CONSTRAINT fk_codigo_prova
  FOREIGN KEY (codigo_prova)
  REFERENCES prova(codigo),

  CONSTRAINT fk_id_questao
  FOREIGN KEY (id_questao)
  REFERENCES questao(id_questao)
);

CREATE TABLE alternativa (
  id_alternativa int,
  id_questao int not null, -- FK
  texto varchar(100) not null,
  correta boolean not null,
  PRIMARY KEY (id_alternativa, id_questao),
  CONSTRAINT fk_alternativa_questao
  FOREIGN KEY (id_questao)
  REFERENCES questao(id_questao)
); 

INSERT INTO prova
(codigo, titulo, materia, data_prova, curso, bimestre)
VALUES
('LDP01', 'Prova 1 de LP', 'Linguagem de Programação I', '2026-09-21', 'Banco de Dados', 2),
('AMD01', 'Prova 1 de Modelagem de Dados', 'Arquitetura e Modelagem de Dados', '2026-09-17', 'Banco de Dados', 2),
('SBD01', 'Prova 1 de Sistemas de Banco de Dados', 'Sistemas de Banco de Dados', '2026-10-05', 'Banco de Dados', 2),
('POO01', 'Prova 1 de Programação Orientada a Objetos', 'Programação Orientada a Objetos', '2026-10-10', 'Banco de Dados', 2),
('SQL01', 'Prova 1 de SQL Avançado', 'SQL', '2026-10-15', 'Banco de Dados', 2);



INSERT INTO questao
(id_questao, titulo, descricao)
VALUES
(1, 'Qual o significado da sigla MER?', 'Assinale a alternativa correta:'),
(2, 'O que é JavaFX?', 'Assinale a alternativa correta:'),
(3, 'Qual a função de uma chave primária?', 'Assinale a alternativa correta:'),
(4, 'O que representa um relacionamento 1:N?', 'Assinale a alternativa correta:'),
(5, 'O que é um objeto em POO?', 'Assinale a alternativa correta:'),
(6, 'Qual conceito permite reutilização de código?', 'Assinale a alternativa correta:'),
(7, 'Qual comando é utilizado para consultar dados?', 'Assinale a alternativa correta:'),
(8, 'Qual cláusula filtra registros em uma consulta SQL?', 'Assinale a alternativa correta:'),
(9, 'Qual comando remove uma tabela do banco de dados?', 'Assinale a alternativa correta:'),
(10, 'O que significa ACID em bancos de dados?', 'Assinale a alternativa correta:');

 
INSERT INTO prova_questao
(codigo_prova, id_questao)
VALUES
('AMD01', 1),
('LDP01', 2),
('AMD01', 3),
('AMD01', 4),
('LDP01', 5),
('POO01', 6),
('SBD01', 7),
('SQL01', 8),
('SQL01', 9),
('SBD01', 10);

INSERT INTO alternativa
(id_alternativa, id_questao, texto, correta)
VALUES
(1, 1, 'a) Modelo Erro-Resposta', FALSE),
(2, 1, 'b) Meio Esperado de Resolução', FALSE),
(3, 1, 'c) Modelo Entidade-Relacionamento', TRUE),

(1, 2, 'a) Uma extensão para formatar documentos', FALSE),
(2, 2, 'b) Uma ferramenta para desenhar gráficos em JAVA', TRUE),
(3, 2, 'c) Uma biblioteca de código aberto para Python', FALSE),

(1, 3, 'a) Identificar unicamente cada registro da tabela', TRUE),
(2, 3, 'b) Criar índices automaticamente', FALSE),
(3, 3, 'c) Armazenar apenas valores numéricos', FALSE),

(1, 4, 'a) Um registro relacionado a vários registros', TRUE),
(2, 4, 'b) Vários registros relacionados a um único registro', FALSE),
(3, 4, 'c) Nenhum relacionamento entre tabelas', FALSE),

(1, 5, 'a) Um bloco de comentários do código', FALSE),
(2, 5, 'b) Uma instância de uma classe', TRUE),
(3, 5, 'c) Um pacote Java', FALSE),

(1, 6, 'a) Encapsulamento', FALSE),
(2, 6, 'b) Herança', TRUE),
(3, 6, 'c) Sobrecarga', FALSE),

(1, 7, 'a) SELECT', TRUE),
(2, 7, 'b) INSERT', FALSE),
(3, 7, 'c) DELETE', FALSE),

(1, 8, 'a) ORDER BY', FALSE),
(2, 8, 'b) GROUP BY', FALSE),
(3, 8, 'c) WHERE', TRUE),

(1, 9, 'a) DELETE', FALSE),
(2, 9, 'b) DROP TABLE', TRUE),
(3, 9, 'c) TRUNCATE', FALSE),

(1, 10, 'a) Um protocolo de rede', FALSE),
(2, 10, 'b) Propriedades de transações em bancos de dados', TRUE),
(3, 10, 'c) Um tipo de banco NoSQL', FALSE);
