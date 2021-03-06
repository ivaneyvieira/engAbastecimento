SELECT U.no,
       U.name,
       login,
       auxLong1                AS storeno,
       IFNULL(cast(concat(CHAR(ascii(SUBSTRING(pswd, 1, 1)) + ascii('e') - ascii('j')),
			  CHAR(ascii(SUBSTRING(pswd, 2, 1)) + ascii('a') - ascii('h')),
			  CHAR(ascii(SUBSTRING(pswd, 3, 1)) + ascii('c') - ascii('k')),
			  CHAR(ascii(SUBSTRING(pswd, 4, 1)) + ascii(' ') - ascii(' ')),
			  CHAR(ascii(SUBSTRING(pswd, 5, 1)) + ascii(' ') - ascii('B')),
			  CHAR(ascii(SUBSTRING(pswd, 6, 1)) + ascii(' ') - ascii(')')),
			  CHAR(ascii(SUBSTRING(pswd, 7, 1)) + ascii(' ') - ascii(')')),
			  CHAR(ascii(SUBSTRING(pswd, 8, 1)) + ascii(' ') - ascii('-'))) AS CHAR),
	      '')              AS senha,
       bits2                   AS bitAcesso,
       IFNULL(abreviacoes, '') AS abreviacoes,
       U.prntno                AS prntno,
       P.name                  AS impressora
FROM sqldados.users                   AS U
  LEFT JOIN sqldados.abastecimentoLoc AS L
	      USING (no)
  LEFT JOIN sqldados.prntr            AS P
	      ON P.no = U.prntno
WHERE login = :login
   OR :login = 'TODOS'