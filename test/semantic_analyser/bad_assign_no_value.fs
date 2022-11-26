BEGIN Factorial

%% Compute the factorial of a number.
   If the input number is negative, print -1. %%

  READ(number) ,              :: Read a number from user input
  result :=,

IF (number > -1) THEN
  WHILE (number > 0) DO
    result :=,
    number :=,    :: decrease number
  END ,
  ELSE                        :: The input number is negative
  result :=,
END ,
PRINT(result) ,
END
