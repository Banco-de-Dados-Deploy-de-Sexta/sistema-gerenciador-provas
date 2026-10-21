USE banco;

-- Execute apenas em bancos existentes cuja tabela prova_questao ainda não possui peso_questao.
ALTER TABLE prova_questao
    ADD COLUMN peso_questao FLOAT NOT NULL DEFAULT 1;
