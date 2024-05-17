public class token {
    public int cat;
    public String text;
    public int lineno;
    public int colno;
    public int ival;
    public String sval;
    public double dval;
    public token(int c, String s, int ln, int col) {
        cat = c;
        text = s;
        lineno = ln;
        colno = col;

        switch (cat) {
            case parser.INTLIT:
                ival = Integer.parseInt(s);
                break;
            case parser.DOUBLELIT:
                dval = Double.parseDouble(s);
                break;
            case parser.STRINGLIT:
                sval = deEscape(s);
                break;
        }
    }

    private String deEscape(String sin) {
        StringBuilder sout = new StringBuilder();
        sin = sin.substring(1, sin.length() - 1);
        int i = 0;
        while (sin.length() > 0) {
            char c = sin.charAt(0);
            if (c =='\\') {
               sin = sin.substring(1);
               if (sin.length() < 1) {
                   j0.lexErr("malformed string literal");
               } else {
                   c = sin.charAt(0);
                   switch (c) {
                       case 't':
                           sout.append('\t');
                           break;
                       case 'n':
                           sout.append('\n');
                           break;
                       default:
                           j0.lexErr("unrecognized escape");
                   }
               }
            } else {
                sout.append(c);
            }
            sin = sin.substring(1);
        }

        return sout.toString();
    }
}
