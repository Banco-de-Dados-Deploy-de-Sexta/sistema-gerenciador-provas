USE banco;

-- Execute apenas em bancos criados antes da Sprint 3 (sem as tabelas usuario e prova_aluno).
-- Cria os usuários (professores e alunos), vincula cada prova ao professor dono
-- e cria a tabela de alunos aos quais uma prova foi aplicada (PB07).

CREATE TABLE usuario (
registro varchar(20) primary key,
nome varchar(100) not null,
tipo_usuario varchar(50) not null
);

INSERT INTO usuario
(registro, nome, tipo_usuario)
VALUES
('SGP-A1231', 'João Victor Graciolli Ramos', 'Aluno'),
('SGP-A1232', 'Miguel Vitale', 'Aluno'),
('SGP-A1233', 'Ana Laura Arantes Machado', 'Aluno'),
('SGP-A1234', 'Eduardo Nakano Silva Senador', 'Aluno'),
('SGP-A1235', 'Gustavo Otacílio Ramos', 'Aluno'),
('SGP-A1236', 'Natan Soares Telles', 'Aluno'),
('SGP-A1237', 'Olavo Salles Gino Teixeira', 'Aluno'),
('SGP-A1238', 'Weslley José Pereira de Moraes', 'Aluno'),

('SGP-P1231', 'Emanuel Mineda', 'Professor'),
('SGP-P1232', 'Adriana', 'Professor'),
('SGP-P1233', 'Giuliano Bertoti', 'Professor');

ALTER TABLE prova
    ADD COLUMN registro_professor varchar(20),
    ADD CONSTRAINT fk_registro_professor
        FOREIGN KEY (registro_professor) REFERENCES usuario(registro);

-- Provas de exemplo do script original: define o professor dono de cada uma.
UPDATE prova SET registro_professor = 'SGP-P1231' WHERE codigo IN ('LDP01', 'POO01');
UPDATE prova SET registro_professor = 'SGP-P1232' WHERE codigo IN ('AMD01', 'SQL01');
UPDATE prova SET registro_professor = 'SGP-P1233' WHERE codigo = 'SBD01';

CREATE TABLE prova_aluno (
codigo_prova varchar(7),
registro_aluno varchar(20),
PRIMARY KEY(codigo_prova, registro_aluno),
CONSTRAINT fk_prova_aluno_prova
FOREIGN KEY (codigo_prova)
REFERENCES prova(codigo),
CONSTRAINT fk_prova_aluno_usuario
FOREIGN KEY (registro_aluno)
REFERENCES usuario(registro)
);
