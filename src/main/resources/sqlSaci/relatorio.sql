SELECT O.ordno AS ordno, O.storeno, cast(TRIM(I.prdno) AS CHAR) AS prdno,
       IFNULL(localizacao, '') AS localizacao, TRIM(MID(P.name, 1, 37)) AS name, I.grade,
       P.mfno_ref AS mfno_ref, T.name AS tipo, ROUND(I.qtty / 1000) AS qtty, P.mfno AS fornecedor,
       P.qttyPackClosed / 1000 AS embalagem
FROM sqldados.eord           AS O
  INNER JOIN sqldados.eoprd  AS I
               ON O.ordno = I.ordno AND O.storeno = I.storeno
  INNER JOIN sqldados.prd    AS P
               ON P.no = I.prdno
  INNER JOIN sqldados.type   AS T
               ON T.no = P.typeno
  LEFT JOIN  sqldados.prdloc AS LOC
               ON LOC.prdno = I.prdno AND LOC.grade = I.grade AND LOC.storeno = 4
WHERE O.ordno = :ordno AND
      O.storeno = :storeno
GROUP BY prdno, grade
ORDER BY localizacao, name, grade
