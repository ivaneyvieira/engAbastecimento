SELECT O.prdno, O.grade, IFNULL(TRIM(MID(P.name, 1, 37)), '') AS descricao,
       P.qttyPackClosed / 1000 AS embalagem, IFNULL(LPAD(P.clno, 6, '0'), '') AS centrodelucro,
       MID(L.localizacao, 1, 4) AS abreviacao, IFNULL(ROUND(O.qtty / 1000, 2), 0.00) AS qtty
FROM sqldados.eoprd          AS O
  INNER JOIN sqldados.prd    AS P
               ON (O.prdno = P.no)
  INNER JOIN sqldados.prdloc AS L
               ON (O.prdno = L.prdno AND O.grade = L.grade)
WHERE O.storeno = :storeno AND
      O.ordno = :ordno AND
      MID(L.localizacao, 1, 4) NOT IN ('EXP4', 'CD00', '')
GROUP BY prdno, grade, abreviacao