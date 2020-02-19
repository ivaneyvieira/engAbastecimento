SELECT (MAX(no) + 1) AS proximo
FROM lastno AS D
WHERE se = 'RS'
  AND storeno = :destino
  AND dupse = 0
