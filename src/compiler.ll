
@.strP = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

define void @println(i32 %x) {
    %1 = alloca i32, align 4
    store i32 %x, i32* %1, align 4
    %2 = load i32, i32* %1, align 4
    %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)
    ret void
}

@.str = private unnamed_addr constant [3 x i8] c"%d\00", align 1

define i32 @readInt() #0 {
    %1 = alloca i32, align 4
    %2 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i32 0, i32 0), i32* %1)
    %3 = load i32, i32* %1, align 4
    ret i32 %3
}

declare i32 @scanf(i8*, ...) #1
declare i32 @printf(i8*, ...)

define i32 @main() {
    entry:

%a = alloca i32
%0 = add i32 0, 2
%1 = sub i32 0, %0
%2 = add i32 0, 3
%3 = add i32 0, 6
%4 = add i32 0, 1
%5 = add i32 %3, %4
%6 = mul i32 %2, %5
%7 = add i32 0, 5
%8 = sdiv i32 %6, %7
%9 = add i32 %1, %8
%10 = add i32 0, 14
%11 = add i32 %9, %10
%12 = add i32 0, 4
%13 = sub i32 0, %12
%14 = add i32 0, 64
%15 = add i32 %13, %14
%16 = add i32 0, 2
%17 = sdiv i32 %15, %16
%18 = sub i32 %11, %17
%19 = add i32 0, 3
%20 = add i32 0, 50
%21 = mul i32 %19, %20
%22 = add i32 0, 2
%23 = sdiv i32 %21, %22
%24 = sub i32 %18, %23
store i32 %24, i32* %a
%25 = load i32, i32* %a
call void @println(i32 %25)
        ret i32 0
}

