all: java
BYSRC=parser.java parserVal.java Yylex.java
STJ=symtab.java symtabEntry.java
CGJ=ThreeAddressCode.java Address.java
JSRC=j0.java tree.java token.java yyerror.java serial.java $(BYSRC) $(STJ) $(CGJ)
BYJOPTS= -Jclass=parser
BYJIMPS= -Jyylex=j0.yylex -Jyyerror=yyerror.yyerror
java: j0.class

j: java
	java j0 hello.java
	dot -Tpng hello.java.dot > hello.png

j0.class: $(JSRC)
	javac $(JSRC)

parser.java parserVal.java: j0gram.y
	yacc.irix $(BYJOPTS) $(BYJIMPS) j0gram.y

Yylex.java: javalex.l
	jflex javalex.l
