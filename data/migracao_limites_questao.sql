USE banco;

-- Execute apenas em bancos criados antes do aumento dos limites da questão:
-- enunciado com até 500 caracteres (PB22) e alternativas com até 300 caracteres (PB24).
ALTER TABLE questao MODIFY titulo VARCHAR(500) NOT NULL;
ALTER TABLE alternativa MODIFY texto VARCHAR(300) NOT NULL;

-- Remove a letra ("a) ", "b) "...) gravada no texto das alternativas de exemplo,
-- pois a aplicação já exibe a letra de cada alternativa.
UPDATE alternativa SET texto = SUBSTRING(texto, 4)
WHERE texto REGEXP '^[a-e][)] ';
