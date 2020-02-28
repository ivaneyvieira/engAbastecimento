INSERT IGNORE INTO sqldados.abastecimentoLoc(no, abreviacoes)
SELECT no, auxStr
FROM sqldados.users;

UPDATE sqldados.abastecimentoLoc AS L INNER JOIN sqldados.users AS U USING (no)
SET bits2       = :bitAcesso,
    abreviacoes = :abreviacoes
WHERE login = :login