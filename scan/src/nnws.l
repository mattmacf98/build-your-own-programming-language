%%
%int
%%
[a-zA-Z]+  { return 1; }
[0-9]+     { return 2; }
[ \t\r\n]+ {  }
.          { simple.lexErr("unrecognized character"); }
