
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

%number = alloca i32
%0 = call i32 @readInt()
store i32 %0, i32* %number
%result = alloca i32
%1 = add i32 0, 1
store i32 %1, i32* %result
%2 = load i32, i32* %number
%3 = add i32 0, 1
%4 = sub i32 0, %3
%5 = icmp sgt i32 %2, %4
br i1 %5, label %label0, label %label1
label0:
br label %label3
label3:
%6 = load i32, i32* %number
%7 = add i32 0, 0
%8 = icmp sgt i32 %6, %7
br i1 %8, label %label4, label %label5
label4:
%9 = load i32, i32* %result
%10 = load i32, i32* %number
%11 = mul i32 %9, %10
store i32 %11, i32* %result
%12 = load i32, i32* %number
%13 = add i32 0, 1
%14 = sub i32 %12, %13
store i32 %14, i32* %number
br label %label3
label5:
br label %label2
label1:
%15 = add i32 0, 1
%16 = sub i32 0, %15
store i32 %16, i32* %result
br label %label2
label2:
%17 = load i32, i32* %result
call void @println(i32 %17)
        ret i32 0
}

