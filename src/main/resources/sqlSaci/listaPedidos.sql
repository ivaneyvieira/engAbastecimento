SELECT storeno, ordno
FROM sqldados.eord
WHERE ordno = :ordno AND
      storeno = :storeno