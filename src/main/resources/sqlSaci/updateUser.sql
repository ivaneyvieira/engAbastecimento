UPDATE sqldados.users
SET bits2  = :bitAcesso,
    auxStr = :abreviacoes
WHERE login = :login