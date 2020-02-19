SELECT O.no AS ordno, O.storeno, cast(TRIM(I.prdno) AS CHAR) AS prdno,
       IFNULL(localizacao, '') AS localizacao, TRIM(MID(P.name, 1, 37)) AS name, I.grade,
       P.mfno_ref AS mfno_ref, T.name AS tipo, ROUND(I.qtty) AS qtty, P.mfno AS fornecedor,
       IFNULL((S.qtty_varejo / 1000 + S.qtty_atacado / 1000), 0) AS estoque,
       P.qttyPackClosed / 1000 AS embalagem
FROM sqldados.ords           AS O
  INNER JOIN sqldados.oprd   AS I
               ON O.no = I.ordno AND O.storeno = I.storeno
  INNER JOIN sqldados.prd    AS P
               ON P.no = I.prdno
  INNER JOIN sqldados.type   AS T
               ON T.no = P.typeno
  LEFT JOIN  sqldados.stk    AS S
               ON S.prdno = I.prdno AND S.grade = I.grade AND S.storeno = 4
  LEFT JOIN  sqldados.prdloc AS LOC
               ON LOC.prdno = I.prdno AND LOC.grade = I.grade AND LOC.storeno = 4
WHERE O.no = :ordno AND
      O.storeno = :storeno
GROUP BY prdno, grade
ORDER BY localizacao, name, grade
