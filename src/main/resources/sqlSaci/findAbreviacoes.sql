SELECT MID(localizacao, 1, 4) AS abreviacoes
FROM sqldados.prdloc
GROUP BY abreviacoes
HAVING abreviacoes LIKE 'CD%' AND
       abreviacoes NOT LIKE 'CD00' AND
       length(abreviacoes) = 4
ORDER BY abreviacoes