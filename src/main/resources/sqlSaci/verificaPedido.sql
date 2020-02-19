SELECT storeno, no AS ordno
FROM ords
WHERE storeno = :storeno AND
      no = :ordno