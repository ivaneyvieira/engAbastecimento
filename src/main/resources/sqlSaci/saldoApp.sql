SELECT SUM(CASE status
             WHEN 'RECEBIDO'
               THEN 1
             WHEN 'INCLUIDA'
               THEN 0
             WHEN 'CONFERIDA'
               THEN -1
             WHEN 'ENTREGUE'
               THEN -1
             WHEN 'ENT_LOJA'
               THEN 0
             WHEN 'PRODUTO'
               THEN 0
           END * I.quantidade) AS saldo
FROM produtos           AS P
  INNER JOIN itens_nota AS I
               ON I.produto_id = P.id
WHERE P.codigo = LPAD(:prdno, 16, ' ') AND
      P.grade = :grade AND
      I.localizacao LIKE CONCAT(:abreviacao, '%')