DROP TABLE IF EXISTS TMP_ORIGINAL;
CREATE TEMPORARY TABLE TMP_ORIGINAL
SELECT P.prdno, MAX(P.auxLong3) AS qtty_original
FROM oprd         AS P
  INNER JOIN ords AS O
               ON P.storeno = O.storeno AND P.ordno = O.l2
WHERE O.no = :ordno
  AND O.storeno = :storeno
  AND P.storeno = :storeno
  AND P.prdno = :prdno
  AND P.grade = :grade
GROUP BY P.prdno;

INSERT IGNORE INTO oprd (storeno, ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1,
                         auxMy2, icmsSubst, auxLong3, auxLong4, auxMy3, auxMy4, qtty, qtty_src,
                         qtty_xfr, cost, qttyRcv, qttyCancel, qttyVendaMes, qttyVendaMesAnt,
                         qttyVendaMedia, qttyPendente, stkDisponivel, qttyAbc, seqno, status, bits,
                         bits2, auxShort1, auxShort2, auxShort3, auxShort4, prdno, grade, remarks,
                         padbyte, gradeFechada, obs, auxStr)
SELECT :storeno AS storeno, :ordno AS ordno, 1000 AS mult, 0 AS ipi, 0 AS freight, 0 AS icms,
       0 AS auxLong1, 0 AS auxLong2, 0 AS auxMy1, 0 AS auxMy2, 0 AS icmsSubst,
       ROUND(IFNULL(T.qtty_original, :qtty * 1000)) AS auxLong3, 0 AS auxLong4, 0 AS auxMy3,
       0 AS auxMy4, :qtty AS qtty, :qtty AS qtty_src, 0 AS qtty_xfr, fob / 10000 AS cost,
       0 AS qttyRcv, 0 AS qttyCancel, 0 AS qttyVendaMes, 0 AS qttyVendaMesAnt, 0 AS qttyVendaMedia,
       0 AS asqttyPendente, 0 AS stkDisponivel, 0 AS qttyAbc, 1 AS seqno, 0 AS status, 1 AS bits,
       0 AS bits2, 0 AS auxShort1, 0 AS auxShort2, 0 AS auxShort3, 0 AS auxShort4, :prdno, :grade,
       '' AS remarks, 0 AS padbyte, '' AS gradeFechada, '' AS obs, :localizacao AS auxStr
FROM prd                 AS P
  LEFT JOIN TMP_ORIGINAL AS T
              ON T.prdno = P.no
WHERE P.no = :prdno
