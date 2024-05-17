import java.io.FileReader;

public class j0 {
    static Yylex lex;
    public static int yylineno;
    public static int yycolno;
    public static token yylval;

    public static void main(String[] argv) throws Exception {
        lex = new Yylex(new FileReader(argv[0]));
        yylineno = 1;
        yycolno = 1;
        int i;
        while ((i=lex.yylex()) != Yylex.YYEOF) {
            System.out.println("token " + i + ":" + yylineno + " " + yytext());
        }
    }

    public static String yytext() {
        return lex.yytext();
    }

    public static void lexErr(String s) {
        System.err.println(s + ": line " + yylineno + ": " + yytext());
        System.exit(1);
    }

    public static int scan(int cat) {
        yylval = new token(cat, yytext(), yylineno, yycolno);
        yycolno += yytext().length();
        return cat;
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
}
