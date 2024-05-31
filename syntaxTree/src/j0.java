import java.io.FileReader;

public class j0 {
    public static Yylex yylexer;
    public static parser par;
    public static int yylineno;
    public static int yycolno;

    public static String yyfilename;

    public static void main(String[] argv) throws Exception {
        init(argv[0]);
        par = new parser();
        yylineno = 1;
        yycolno = 1;
        int i = par.yyparse();
        if (i == 0) {
            System.out.println("no errors");
        }
    }

    public static void init(String s) throws Exception {
        yyfilename = s;
        yylexer = new Yylex(new FileReader(s));
    }

    public static int YYEOF() { return Yylex.YYEOF; }

    public static String yytext() {
        return yylexer.yytext();
    }

    public static void lexErr(String s) {
        System.err.println(s);
        System.exit(1);
    }

    public static int scan(int cat) {
        par.yylval = new parserVal(new tree("token", cat, new token(cat, yytext(), yylineno)));
        return cat;
    }

    public static int yylex() {
        int rv = 0;
        try {
            rv = yylexer.yylex();
        } catch(java.io.IOException ioException) {
            rv = -1;
        }
        return rv;
    }

    public static short ord(String s) {
        return (short) s.charAt(0);
    }

    public static void newline() {
        yylineno++;
        yycolno = 1;
    }

    public static void whitespace() {
        yycolno += yytext().length();
    }

    public static parserVal node(String s, int r, parserVal... p) {
        tree[] t = new tree[p.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = (tree)(p[i].obj);
        }
        return new parserVal(new tree(s,r,t));
    }

    public static void comment() {
        String s = yytext();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == '\n') {
                yylineno++;
                yycolno = 1;
            } else {
                yycolno++;
            }
        }
    }

    public static void print(parserVal root) {
//        ((tree)root.obj).print(0);
        ((tree) root.obj).printGraph(yyfilename + ".dot");
    }
}
