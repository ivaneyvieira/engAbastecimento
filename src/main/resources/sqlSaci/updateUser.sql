UPDATE sqldados.users
SET bits3  = :bitAcesso,
    auxStr = :abreviacoes
WHERE login = :login