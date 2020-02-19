INSERT IGNORE INTO ords (no, date, vendno, discount, amt, package, custo_fin, others, eord_ordno,
                         dataFaturamento, invno, freightAmt, auxLong1, auxLong2, amtOrigem,
                         dataEntrega, discountOrig, l1, l2, l3, l4, m1, m2, m3, m4, deliv, storeno,
                         carrno, empno, prazo, eord_storeno, delivOriginal, bits, bits2, bits3,
                         padbyte, indxno, repno, auxShort1, auxShort2, noofinst, status, s1, s2, s3,
                         s4, frete, remarks, ordnoFromVend, remarksInv, remarksRcv, remarksOrd,
                         auxChar, c1, c2, c3, c4)
SELECT :ordnoNovo AS no, date, vendno, discount, 0 AS amt, package, custo_fin, others, eord_ordno,
       dataFaturamento, invno, freightAmt, auxLong1, auxLong2, amtOrigem, dataEntrega, discountOrig,
       l1, :ordno AS l2 /*Salva o pedido mãe*/, l3, l4, m1, m2, m3, m4, deliv, :storeno AS storeno,
       carrno, empno, prazo, eord_storeno, delivOriginal, bits, bits2, bits3, padbyte, indxno,
       repno, auxShort1, auxShort2, noofinst, 0, s1, s2, s3, s4, frete, remarks, ordnoFromVend,
       remarksInv, remarksRcv, remarksOrd, auxChar, c1, c2, c3, :tipo AS c4
FROM ords
WHERE storeno = :storeno AND
      NO = :ordno;

INSERT INTO oprd (storeno, ordno, mult, ipi, freight, icms, auxLong1, auxLong2, auxMy1, auxMy2,
                  icmsSubst, auxLong3, auxLong4, auxMy3, auxMy4, qtty, qtty_src, qtty_xfr, cost,
                  qttyRcv, qttyCancel, qttyVendaMes, qttyVendaMesAnt, qttyVendaMedia, qttyPendente,
                  stkDisponivel, qttyAbc, seqno, status, bits, bits2, auxShort1, auxShort2,
                  auxShort3, auxShort4, prdno, grade, remarks, padbyte, gradeFechada, obs, auxStr)
SELECT :storeno AS storeno, :ordnoNovo AS ordno, mult, ipi, freight, icms, auxLong1, auxLong2,
       auxMy1, auxMy2, icmsSubst, ROUND(:qtty * 1000) AS auxLong3 /*Salva a quantidade
                                                                    original para manter
                                                                    intocada*/, auxLong4, auxMy3,
       auxMy4, :qtty AS qtty, :qtty AS qtty_src, qtty_xfr, cost, 0 AS qttyRcv, 0 AS qttyCancel,
       qttyVendaMes, qttyVendaMesAnt, qttyVendaMedia, qttyPendente, stkDisponivel, qttyAbc, seqno,
       status, bits, bits2, auxShort1, auxShort2, auxShort3, auxShort4, prdno, grade, remarks,
       padbyte, gradeFechada, obs, :localizacao AS auxStr
FROM oprd
WHERE (storeno = :storeno) AND
      (ordno = :ordno) AND
      (prdno = :prdno) AND
      (grade = :grade)
GROUP BY storeno, ordno, prdno, grade;

UPDATE oprd
SET oprd.qtty = (oprd.qtty - :qtty)
WHERE (oprd.storeno = :storeno) AND
      (oprd.ordno = :ordno) AND
      (oprd.prdno = :prdno) AND
      (oprd.grade = :grade);

DELETE
FROM oprd
WHERE oprd.storeno = :storeno AND
      oprd.ordno = :ordno AND
      oprd.prdno = :prdno AND
      oprd.grade = :grade AND
      ROUND(oprd.qtty, 2) <= 0;

DELETE
FROM oprd
WHERE oprd.storeno = :storeno AND
      oprd.ordno = :ordnoNovo AND
      oprd.prdno = :prdno AND
      oprd.grade = :grade AND
      ROUND(oprd.qtty, 2) <= 0