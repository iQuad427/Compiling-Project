BEGIN Factorial

%% Compute the factorial of a number.
   If the input number is negative, print -1. %%

  READ(enumber) ,              :: Read a number from user input
  jresult := 1 ,

IF (inumber > -1) THEN
  WHILE fnumber > 0 DO 
    gresult := fresult * jnumber ,
    dnumber := dnumber - 1 ,    :: decrease number
  END ,
  ELSE                        :: The input number is negative
  cresult := -1 ,
END ,
PRINT(aresult) ,
END
