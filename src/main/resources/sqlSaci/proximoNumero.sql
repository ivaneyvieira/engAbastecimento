SELECT (MAX(no) + 1) AS proximo
FROM ords
WHERE storeno = :storeno
  AND no BETWEEN (:destino * 1000) AND (:destino * 1000 + 998)