import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class tree {
    int id;
    String sym;
    int rule;
    int nkids;
    token tok;
    tree[] kids;
    boolean isConst;
    symtab stab;

    public tree(String s, int r, token t) {
        id = serial.getid();
        sym = s;
        rule = r;
        tok = t;
    }

    public tree(String s, int r, tree[] t) {
        id = serial.getid();
        sym = s;
        rule = r;
        nkids = t.length;
        kids = t;
    }

    public void makeSymbolTables(symtab curr) {
        stab = curr;
        switch (sym) {
            case "ClassDecl":
                curr = new symtab("class", curr);
                break;
            case "MethodDecl":
                curr = new symtab("method", curr);
                break;
        }

        for (int i = 0; i < nkids; i++) {
            kids[i].makeSymbolTables(curr);
        }
    }

    public void populateSymbolTables() {
        switch (sym) {
            case "ClassDecl":
                stab.insert(kids[0].tok.text, false, kids[0].stab);
                break;
            case "FieldDecl":
            case "LocalVarDecl":
                tree kid = kids[1];
                while (kid != null && kid.sym.equals("VarDecls")) {
                    insertVarDeclarator(kid.kids[1]);
                    kid = kids[0];
                }
                return; // ???
            case "MethodDecl":
                stab.insert(kids[0].kids[1].kids[0].tok.text, false, kids[0].stab); // why 1? that is STATIC?
                break;
            case "FormalParam":
                insertVarDeclarator(kids[1]);
                return;
        }

        for (int i = 0; i < nkids; i++) {
            tree kid = kids[i];
            kid.populateSymbolTables();
        }
    }

    private void insertVarDeclarator(tree varDeclarator) {
        if (varDeclarator.tok != null) {
            stab.insert(varDeclarator.tok.text, false);
        } else {
            insertVarDeclarator(varDeclarator.kids[0]);
        }
    }

    private void calcIsConst() {
        for (int i = 0; i < nkids; i++) {
            kids[i].calcIsConst();
        }
        switch (sym) {
            case "INTLIT":
            case "DOUBLELIT":
            case "STRINGLIT":
            case "BOOLLIT": // book has true and false
                isConst = true;
                break;
            case "UnaryExpr":
                isConst = kids[1].isConst;
                break;
            case "RelExpr":
                isConst = kids[0].isConst && kids[2].isConst;
                break;
            case "CondOrExpr":
            case "CondAndExpr":
            case "EqExpr":
            case "MulExpr":
            case "AddExpr": // shouldn't these also be 0 and 2??
                isConst = kids[0].isConst && kids[1].isConst;
                break;
            default: isConst = false;
        }
    }

    public void checkSymbolTables() {
        checkCodeBlocks();
    }

    private void checkCodeBlocks() {
        tree kid;
        if (sym.equals("MethodDecl")) {
            kids[1].checkBlock();
        } else {
            for (int i = 0; i < nkids; i++) {
                kid = kids[i];
                kid.checkCodeBlocks();
            }
        }
    }

    private void checkBlock() {
        switch (sym) {
            case "IDENTIFIER":
                if (stab.lookup(tok.text) == null) {
                    j0.semerror("undeclared variable " + tok.text);
                }
                break;
            case "FieldAccess":
            case "QualifiedName":
                kids[0].checkBlock();
                break;
            case "MethodCall":
                kids[0].checkBlock();
                if (rule == 1290) {
                    kids[1].checkBlock();
                } else {
                    kids[2].checkBlock();
                }
                break;
            case "LocalVarDecl":
                break;
            default:
                for (int i = 0; i < nkids; i++) {
                    kids[i].checkBlock();
                }
        }
    }

    public void print(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        if (tok != null) {
            System.out.println(id + " " + tok.text + " (" + tok.cat + "): " + tok.lineno);
        } else {
            System.out.println(id + " " + sym + " (" + rule + "): " + nkids);
            for (int i=0; i < nkids; i++) {
                kids[i].print(level + 1);
            }
        }
    }

    public void printGraph(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
            pw.printf("digraph {\n");
            int j = 0;
            printGraph(pw);
            pw.printf("}\n");
            pw.close();
        } catch (IOException ioException) {
            System.err.println("printgraph exception");
            System.exit(1);
        }
    }

    int j;
    private void printGraph(PrintWriter pw) {
        if (tok != null) {
            printLeaf(pw);
            return;
        }
        printBranch(pw);

        for (int i = 0; i < nkids; i++) {
            if (kids[i] != null) {
                pw.printf("N%d -> N%d;\n", id, kids[i].id);
                kids[i].printGraph(pw);
            } else {
                pw.printf("N%d -> N%d_%d;\n", id, id, j);
                pw.printf("N%d%d [label=\"Empty rule\"];\n", id, j);
                j++;
            }
        }
    }

    private void printLeaf(PrintWriter pw) {
        String s = parser.yyname[tok.cat];
        printBranch(pw);
        pw.printf("N%d [shape=box style=dotted label=\" %s \\n", id, s);
        pw.printf("text = %s \\l lineno = %d \\l\"];\n", escape(tok.text), tok.lineno);
    }

    private void printBranch(PrintWriter pw) {
        pw.printf("N%d ",id);
        pw.printf("[shape=box label=\"%s",prettyPrintName());
        if (tok != null)
            pw.printf("struct token* leaf %d", tok.id);
        pw.printf("\"];\n");
    }

    private String escape(String s) {
        if (s.charAt(0) == '\"')
            return "\\"+s.substring(0, s.length()-1)+"\\\"";
        else return s;
    }

    private String prettyPrintName() {
        if (tok == null) {
            return sym +"#"+(rule%10);
        } else {
            return escape(tok.text)+":"+tok.cat;
        }
    }
}
