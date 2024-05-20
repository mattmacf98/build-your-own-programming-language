public class trivial {
    static Parser par;
    public static void main(String[] args) throws Exception {
        lexer.init(args[0]);
        par = new Parser();
        int i = par.yyparse();
        if (i == 0) {
            System.out.println("no errors");
        }
    }
}
