%%
%int
%%
[a-zA-Z]+ { return Parser.NAME; }
[0-9]+ { return Parser.NUMBER; }
[ \t\n]+ { }
. { lexer.lexErr("unrecognized character");}
