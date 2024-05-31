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
