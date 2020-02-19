INSERT IGNORE INTO oprd (storeno, ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1,
                         auxMy2, icmsSubst, auxLong3, auxLong4, auxMy3, auxMy4, qtty, qtty_src,
                         qtty_xfr, cost, qttyRcv, qttyCancel, qttyVendaMes, qttyVendaMesAnt,
                         qttyVendaMedia, qttyPendente, stkDisponivel, qttyAbc, seqno, status, bits,
                         bits2, auxShort1, auxShort2, auxShort3, auxShort4, prdno, grade, remarks,
                         padbyte, gradeFechada, obs, auxStr)
SELECT :storeno AS storeno, :ordnoMae AS ordno, mult, ipi, freight, icms, auxLong1, auxLong2,
       auxMy1, auxMy2, icmsSubst, @ORIGINAL := auxLong3, auxLong4, auxMy3, auxMy4,
       (auxLong3 / 1000) - :qttyEdit AS qtty, (auxLong3 / 1000) - :qttyEdit AS qtty_src, qtty_xfr,
       cost, 0 AS qttyRcv, 0 AS qttyCancel, qttyVendaMes, qttyVendaMesAnt, qttyVendaMedia,
       qttyPendente, stkDisponivel, qttyAbc, seqno, status, bits, bits2, auxShort1, auxShort2,
       auxShort3, auxShort4, prdno, grade, remarks, padbyte, gradeFechada, obs,
       :localizacao AS auxStr
FROM oprd
WHERE (storeno = :storeno) AND
      (ordno = :ordno) AND
      (prdno = :prdno) AND
      (grade = :grade)
GROUP BY storeno, ordno, prdno, grade;

UPDATE oprd
SET oprd.qtty     = (@ORIGINAL / 1000) - :qttyEdit * 1.00,
    oprd.qtty_src = (@ORIGINAL / 1000) - :qttyEdit * 1.00,
    oprd.auxStr   = :localizacao
WHERE (oprd.storeno = :storeno) AND
      (oprd.ordno = :ordnoMae) AND
      (oprd.prdno = :prdno) AND
      (oprd.grade = :grade);

UPDATE oprd
SET oprd.qtty     = :qttyEdit * 1.00,
    oprd.qtty_src = :qttyEdit * 1.00,
    oprd.auxStr   = :localizacao
WHERE (oprd.storeno = :storeno) AND
      (oprd.ordno = :ordno) AND
      (oprd.prdno = :prdno) AND
      (oprd.grade = :grade);

DELETE
FROM oprd
WHERE oprd.storeno = :storeno AND
      oprd.ordno = :ordnoMae AND
      oprd.prdno = :prdno AND
      oprd.grade = :grade AND
      ROUND(oprd.qtty, 2) <= 0;

DELETE
FROM oprd
WHERE oprd.storeno = :storeno AND
      oprd.ordno = :ordno AND
      oprd.prdno = :prdno AND
      oprd.grade = :grade AND
      ROUND(oprd.qtty, 2) <= 0
