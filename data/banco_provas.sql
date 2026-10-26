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
titulo varchar(500) not null,
descricao varchar(300) not null
);

CREATE TABLE prova_questao (
codigo_prova varchar(7),
id_questao int,
peso_questao float NOT NULL DEFAULT 1,

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
texto varchar(300) not null,
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
(codigo_prova, id_questao, peso_questao)
VALUES
('AMD01', 1, 1),
('LDP01', 2, 1),
('AMD01', 3, 1),
('AMD01', 4, 1),
('LDP01', 5, 1),
('POO01', 6, 1),
('SBD01', 7, 1),
('SQL01', 8, 1),
('SQL01', 9, 1),
('SBD01', 10, 1);

INSERT INTO alternativa
(id_alternativa, id_questao, texto, correta)
VALUES
(1, 1, 'Modelo Erro-Resposta', FALSE),
(2, 1, 'Meio Esperado de Resolução', FALSE),
(3, 1, 'Modelo Entidade-Relacionamento', TRUE),
(4, 1, 'Mapa de Estruturas Relacionais', FALSE),

(1, 2, 'Uma extensão para formatar documentos', FALSE),
(2, 2, 'Uma ferramenta para desenhar gráficos em JAVA', TRUE),
(3, 2, 'Uma biblioteca de código aberto para Python', FALSE),
(4, 2, 'Um banco de dados orientado a objetos', FALSE),

(1, 3, 'Identificar unicamente cada registro da tabela', TRUE),
(2, 3, 'Criar índices automaticamente', FALSE),
(3, 3, 'Armazenar apenas valores numéricos', FALSE),
(4, 3, 'Permitir valores repetidos na coluna', FALSE),

(1, 4, 'Um registro relacionado a vários registros', TRUE),
(2, 4, 'Vários registros relacionados a um único registro', FALSE),
(3, 4, 'Nenhum relacionamento entre tabelas', FALSE),
(4, 4, 'Um registro relacionado a apenas um registro', FALSE),

(1, 5, 'Um bloco de comentários do código', FALSE),
(2, 5, 'Uma instância de uma classe', TRUE),
(3, 5, 'Um pacote Java', FALSE),
(4, 5, 'Um tipo primitivo da linguagem', FALSE),

(1, 6, 'Encapsulamento', FALSE),
(2, 6, 'Herança', TRUE),
(3, 6, 'Sobrecarga', FALSE),
(4, 6, 'Abstração de dados', FALSE),

(1, 7, 'SELECT', TRUE),
(2, 7, 'INSERT', FALSE),
(3, 7, 'DELETE', FALSE),
(4, 7, 'UPDATE', FALSE),

(1, 8, 'ORDER BY', FALSE),
(2, 8, 'GROUP BY', FALSE),
(3, 8, 'WHERE', TRUE),
(4, 8, 'HAVING', FALSE),

(1, 9, 'DELETE', FALSE),
(2, 9, 'DROP TABLE', TRUE),
(3, 9, 'TRUNCATE', FALSE),
(4, 9, 'ALTER TABLE', FALSE),

(1, 10, 'Um protocolo de rede', FALSE),
(2, 10, 'Propriedades de transações em bancos de dados', TRUE),
(3, 10, 'Um tipo de banco NoSQL', FALSE),
(4, 10, 'Uma linguagem de consulta', FALSE);
