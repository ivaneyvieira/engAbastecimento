SELECT storeno, ordno, custno, paymno, status, bits13 & POW(2, 0) <> 0 AS gravado
FROM sqldados.eord
WHERE ordno = :ordno AND
      storeno = :storeno