SELECT storeno, no AS ordno, l2 AS ordnoMae, C4 AS tipo
FROM sqldados.ords
WHERE (no BETWEEN 1000 AND 9999 OR NO BETWEEN 100000000 AND 999999999)
  AND MID(no, 1, 1) * 1 IN (2, 3, 4, 5)
  AND storeno = :storeno