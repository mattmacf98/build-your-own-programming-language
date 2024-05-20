import java.io.FileReader;

public class simple {
    static Yylex lex;
    public static void main(String[] argv) throws Exception {
        lex = new Yylex(new FileReader(argv[0]));
        int i;
        while ((i=lex.yylex()) != Yylex.YYEOF) {
            System.out.println("token " + i + ": " + yytext());
        }
    }

    public static String yytext() {
        return lex.yytext();
    }

    public static void lexErr(String s) {
        System.err.println(s + ": " + yytext());
        System.exit(1);
    }
}
