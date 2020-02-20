SELECT storeno, ordno, custno, paymno, status
FROM sqldados.eord
WHERE ordno = :ordno AND
      storeno = :storeno