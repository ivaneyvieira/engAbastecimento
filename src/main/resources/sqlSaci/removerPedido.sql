DELETE
FROM sqldados.eoprd
WHERE storeno = :storeno AND
      ordno = :ordno AND
      prdno = LPAD(:prdno, 16, ' ') AND
      grade = :grade;

DELETE
FROM sqldados.eoprdf
WHERE storeno = :storeno AND
      ordno = :ordno AND
      prdno = LPAD(:prdno, 16, ' ') AND
      grade = :grade;

DELETE
FROM sqldados.eoprd2
WHERE storeno = :storeno AND
      ordno = :ordno AND
      prdno = LPAD(:prdno, 16, ' ') AND
      grade = :grade;

DELETE
FROM sqldados.eoprd3
WHERE storeno = :storeno AND
      ordno = :ordno AND
      prdno = LPAD(:prdno, 16, ' ') AND
      grade = :grade