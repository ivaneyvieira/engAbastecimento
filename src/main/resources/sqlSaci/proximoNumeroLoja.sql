SELECT MAX(no) AS proximo
FROM ords         AS O
  INNER JOIN oprd AS P
               ON P.ordno = O.no
WHERE O.storeno = :storeno
  AND O.no BETWEEN (:destino * 100000000) AND (:destino * 100000000 + 99999998)
  AND O.c4 = 'L'
  AND P.auxStr LIKE CONCAT(:abreviacao, '%')
