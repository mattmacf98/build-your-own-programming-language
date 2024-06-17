import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class j0 {
    public static Yylex yylexer;
    public static parser par;
    public static symtab globalSymTab;
    public static symtab stringTab;
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
        ((tree)root.obj).print(0);
//        ((tree) root.obj).printGraph(yyfilename + ".dot");
    }

    public static void semantic(parserVal rootParserVal) {
        tree root = (tree)(rootParserVal.obj);
        symtab outSymTab, systemSymTab;
        globalSymTab = new symtab("global");
        stringTab = new symtab("strings");
        systemSymTab = new symtab("class");
        outSymTab = new symtab("class");
        outSymTab.insert("println", false, null, new MethodType(new TypeInfo[]{new ClassType("String")}, new TypeInfo("void")));
        systemSymTab.insert("out", false, outSymTab, new ClassType("PrintStream", outSymTab));
        globalSymTab.insert("System", false, systemSymTab, new ClassType("System", systemSymTab));

        root.makeSymbolTables(globalSymTab);
        root.populateSymbolTables();
        root.checkSymbolTables();
        root.makeClass();
        root.checkType(false);
//        globalSymTab.print();
    }

    public static void generateUnicon(parserVal r) {
        tree root = (tree)(r.obj);
        root.generateUnicon();

        int len = yyfilename.length();
        String baseName = yyfilename.substring(0, len - 5);
        String outfile = baseName + "_helper.icn";
        PrintWriter fout;

        try {
            fout = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
            fout.printf("procedure main(argv)\n");
            fout.printf("   " + baseName + "__main ! argv\n");
            fout.printf("end\n");
            fout.close();
        } catch (IOException ioException) {
            System.err.println("can't write to file " + outfile);
            System.exit(1);
        }

        String outfileName = baseName + ".icn";
        try {
            fout = new PrintWriter(new BufferedWriter(new FileWriter(outfileName)));
            for (int i = 0; i < root.iconCode.size(); i++) {
                String s = root.iconCode.get(i);
                fout.printf(s + "\n");
                System.out.println(s);
            }
            fout.close();

//            String cmd = "unicon -s " + outfileName + " " + outfile;
//            Process process = Runtime.getRuntime().exec(cmd);
//            int eValue = 0;
//            try {
//                eValue = process.waitFor();
//            } catch (InterruptedException e) {
//                System.err.println("unicon interrupted");
//            }
//            System.out.println("unicon exited with value " + eValue);
        } catch (IOException ioException) {
            System.err.println("can't write to file " + outfileName);
            System.exit(1);
        }
    }


    public static void gencode(parserVal r) {
        tree root = (tree)(r.obj);
        root.generateFirst();
        root.generateFollow();
        root.generateTargets();
        root.generateCode();
        if (root.intermediateCode != null) {
            for(int i = 0; i < root.intermediateCode.size(); i++) {
                root.intermediateCode.get(i).print();
            }
        }
    }

    public static void semerror(String s) {
        System.out.println("semantic error: " + s);
        System.exit(1);
    }

    public static void calcType(parserVal parserVal) {
        tree tree = (tree) parserVal.obj;

        tree.kids[0].calcType();
        tree.kids[1].assignType(tree.kids[0].typeInfo);
    }
}
