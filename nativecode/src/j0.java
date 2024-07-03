import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class j0 {
    public static Yylex yylexer;
    public static parser par;
    public static symtab globalSymTab;
    public static symtab stringTab;
    public static int yylineno;
    public static int yycolno;
    public static String yyfilename;
    public static ArrayList<x64> xcode;

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

    public static x64loc loc(String s) {
        return new x64loc(s);
    }

    public static x64loc loc(Object o) {
        if (o instanceof Integer) {
            return new x64loc((Integer) o);
        } else if (o instanceof String) {
            return new x64loc((String) o);
        } else  {
            return null;
        }
    }

    public static x64loc loc(Address a) {
        switch (a.region) {
            case "loc":
                if (a.offset <= 88) {
                    return loadRegion(a);
                } else {
                    return new x64loc("rbp", -a.offset);
                }
            case "glob":
                return new x64loc("rip", a.offset);
            case "const":
            case "imm":
                return new x64loc("imm", a.offset);
            case "lab":
                return new x64loc("lab", a.offset);
            case "obj":
                return new x64loc("r15", a.offset);
            default:
                semerror("x64loc unknown region");
                return null;
        }
    }

    public static ArrayList<x64> l64(x64 x) {
        return new ArrayList<x64>(Collections.singletonList(x));
    }
    public static ArrayList<x64> xgen(String o){
        return l64(new x64(o));
    }
    public static ArrayList<x64> xgen(String o, Address src, Address dst) {
        return l64(new x64(o, loc(src), loc(dst)));
    }
    public static ArrayList<x64> xgen(String o, Address opnd) {
        return l64(new x64(o, loc(opnd)));
    }
    public static ArrayList<x64> xgen(String o, Address src, String dst) {
        return l64(new x64(o, loc(src), loc(dst)));
    }
    public static ArrayList<x64> xgen(String o, String src, Address dst) {
        return l64(new x64(o,loc(src),loc(dst)));
    }
    public static ArrayList<x64> xgen(String o, String src, String dst) {
        return l64(new x64(o,loc(src),loc(dst)));
    }
    public static ArrayList<x64> xgen(String o, String opnd) {
        return l64(new x64(o, loc(opnd)));
    }

    public static x64loc loadRegion(Address a) {
        int region = a.offset/8;
        if (!RegisterUse.registers[region].loaded) {
            xcode.addAll(xgen("movq", -a.offset + "(%rbp)", RegisterUse.registers[region].registerName));
            RegisterUse.registers[region].loaded = true;
        }

        return new x64loc(RegisterUse.registers[a.offset/8 + 1].registerName);
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
        xcode = x64code(root.intermediateCode);
        genx64code();
    }

    public static PrintStream open(String s){
        PrintStream p = null;
        try {
            p = new PrintStream(s);
        } catch (java.io.FileNotFoundException e) {
            stop("couldn't open output file "+ s + " for writing");
        }
        return p;
    }

    public static void stop(String s) {
        System.err.println(s);
        System.exit(1);
    }

    public static int find(String needle, String haystack) {
        return find(needle.getBytes(), haystack.getBytes());
    }

    public static int find(byte[] needle, byte[]haystack) {
        int i=0;
        for( ; i < haystack.length - needle.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < needle.length; ++j) {
                if (haystack[i+j] != needle[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    public static void genx64code() {
        int i = find(".java", yyfilename);
        String outfilename = yyfilename.substring(0,i) + ".s";
        PrintStream fout;
        fout = open(outfilename);
        fout.println("\t.file\t\"" + yyfilename + "\"");
        // write out our "runtime system" with every generated code
        fout.println("\t.text");
        fout.println("\t.globl\tPrintStream__println");
        fout.println("\t.def\tPrintStream__println;\t.scl\t2;\t.type\t32;\t.endef");
        fout.println("\t.seh_proc\tPrintStream__println");
        fout.println("PrintStream__println:");
        fout.println("\tpushq\t%rbp");
        fout.println("\t.seh_pushreg\t%rbp");
        fout.println("\tmovq\t%rsp, %rbp");
        fout.println("\t.seh_setframe\t%rbp, 0");
        fout.println("\tsubq\t$32, %rsp\n"+
                "\t.seh_stackalloc\t32\n" +
                "\t.seh_endprologue\n"+
                "\tmovq\t%rsi, %rcx\n"+ // Windows conventions!
                "\tcall\tputs\n"+
                "\tnop\n"+
                "\taddq\t$32, %rsp\n"+
                "\tpopq\t%rbp\n"+
                "\tret\n"+
                "\t.seh_endproc\n"+
                "\t.text\n"+
                "\t.def\t__main;\t.scl\t2;\t.type\t32;\t.endef");
        write_x64strings(fout);
        x64print(fout);
        fout.println("\t.ident\t\"j0: (Unicon) 0.1.0\"");
        fout.println("\t.def\tputs;\t.scl\t2;\t.type\t32;\t.endef");
        fout.close();
    }

    public static void write_x64strings(PrintStream fout) {
        String s;
        fout.println("\t.section\t.rdata,\"dr\"");
        for(int i=0; i < stringTab.L.size(); i++) {
            s = stringTab.L.get(i);
            fout.println(".Lstr" + i + ":");
            s = s.substring(0, s.length()-1) + "\\0\"";
            fout.println("\t.ascii " + s);
        }
    }

    public static void x64print(PrintStream f) {
        for(x64 x : xcode) x.print(f);
    }
    public static void x64print() {
        for(x64 x : xcode) x.print(System.out);
    }

    public static ArrayList<x64> x64code(ArrayList<ThreeAddressCode> intermediateCode) {
        int parmCount = -1;
        for (int i = 0; i < intermediateCode.size(); i++) {
            ThreeAddressCode instruction = intermediateCode.get(i);
            switch (instruction.opcode) {
                case "ADD":
                    xcode.addAll(xgen("movq", instruction.operandTwo, "%rax"));
                    xcode.addAll(xgen("addq", instruction.operandThree, "%rax"));
                    xcode.addAll(xgen("movq", "%rax", instruction.operandOne));
                    break;
                case "NEG":
                    xcode.addAll(xgen("movq", instruction.operandTwo, "%rax"));
                    xcode.addAll(xgen("negq", "%rax"));
                    xcode.addAll(xgen("movq", "%rax", instruction.operandOne));
                    break;
                case "ASN":
                    xcode.addAll(xgen("movq", instruction.operandTwo, "%rax"));
                    xcode.addAll(xgen("movq", "%rax", instruction.operandOne));
                    break;
                case "ADDR":
                    xcode.addAll(xgen("leaq", instruction.operandTwo, "%rax"));
                    xcode.addAll(xgen("%rax", instruction.operandOne));
                    break;
                case "LCON":
                    xcode.addAll(xgen("movq", instruction.operandTwo, "%rax"));
                    xcode.addAll(xgen("movq", "(%rax)", "%rax"));
                    xcode.addAll(xgen("movq", "%rax", instruction.operandOne));
                    break;
                case "SCON":
                    xcode.addAll(xgen("movq", instruction.operandTwo, "%rbx"));
                    xcode.addAll(xgen("movq", instruction.operandOne, "%rax"));
                    xcode.addAll(xgen("movq", "%rbx", "(%rax)"));
                    break;
                case "GOTO":
                    xcode.addAll(xgen("goto", instruction.operandOne));
                    break;
                case "BLT":
                    xcode.addAll(xgen("movq", instruction.operandTwo, "%rax"));
                    xcode.addAll(xgen("cmpq", instruction.operandThree, "%rax"));
                    xcode.addAll(xgen("jl", instruction.operandOne));
                    break;
                case "PARM":
                    if (parmCount == -1) {
                        for (int j = i+1; j < intermediateCode.size(); j++) {
                            ThreeAddressCode callInstruction = intermediateCode.get(j);
                            if (callInstruction.opcode.equals("CALL")) {
                                break;
                            }
                            parmCount++;
                        }
                    } else {
                        parmCount--;
                    }
                    generateParm(parmCount, instruction.operandOne);
                    break;
                case "CALL":
                    xcode.addAll(xgen("call", instruction.operandThree));
                    xcode.addAll(xgen("movq", "%rax", instruction.operandOne));
                    parmCount = -1;
                    break;
                case "RETURN":
                    xcode.addAll(xgen("movq", instruction.operandOne, "%rax"));
                    xcode.addAll(xgen("leave"));
                    xcode.addAll(xgen("ret", instruction.operandOne));
                    break;
                case "LAB":
                    for (RegisterUse registerUse : RegisterUse.registers) {
                        registerUse.save();
                    }
                    xcode.addAll(xgen("lab", instruction.operandOne));
                    break;
                case "proc":
                    int n = 0;
                    if (instruction.operandTwo == null)
                        System.err.println("instr.op2 is null");
                    if (instruction.operandThree == null)
                        System.err.println("instr.op3 is null");
                    if (instruction.operandTwo != null) n += instruction.operandTwo.intgr();
                    if (instruction.operandThree != null) n += instruction.operandThree.intgr();
                    n *= 8;

                    xcode.addAll(xgen(".text"));
                    xcode.addAll(xgen(".globl", instruction.operandOne.region));
                    xcode.addAll(xgen(".def\t" + instruction.operandOne.region +
                            ";\t.scl\t2;\t.type\t32;\t.endef"));
                    xcode.addAll(xgen(".seh_proc\t"+instruction.operandOne.region));
                    xcode.addAll(xgen("lab", instruction.operandOne.region));
                    xcode.addAll(xgen("pushq", "%rbp"));
                    xcode.addAll(xgen(".seh_pushreg\t%rbp"));
                    xcode.addAll(xgen("movq", "%rsp", "%rbp"));
                    xcode.addAll(xgen(".seh_setframe\t%rbp, 0"));
                    xcode.addAll(xgen("subq", "$"+n, "%rsp"));
                    xcode.addAll(xgen(".seh_stackalloc\t"+n));
                    xcode.addAll(xgen(".seh_endprologue"));
                    if (instruction.operandOne.region.equals("main"))
                        xcode.addAll(xgen("call","__main"));
                    int j = 0;
                    if (instruction.operandTwo != null)
                        for( ; j < instruction.operandTwo.offset; j++)
                            RegisterUse.registers[j].loaded = RegisterUse.registers[j].dirty = true;
                    for( ; j < 11; j++)
                        RegisterUse.registers[j].loaded = RegisterUse.registers[j].dirty = false;
                    break;
                case "end":
                    if (!xcode.get(xcode.size() - 1).opcode.equals("ret")) {
                        xcode.addAll(xgen("leave"));
                        xcode.addAll(xgen("ret"));
                    }
                    xcode.addAll(xgen(".seh_endproc"));
                    break;
            }
        }
        return xcode;
    }

    public static void generateParm(int parmCount, Address address) {
        for (RegisterUse registerUse : RegisterUse.registers) {
            registerUse.save();
        }
        if (parmCount > 6) {
            xcode.addAll(xgen("pushq", address));
        } else {
            String s = "error:" + parmCount;
            switch (parmCount) {
                case 1:
                    s = "%rdi";
                    break;
                case 2:
                    s = "%rsi";
                    break;
                case 3:
                    s = "%rdx";
                    break;
                case 4:
                    s = "%rcx";
                    break;
                case 5:
                    s = "%r8";
                    break;
                case 6:
                    s = "%r9";
                    break;
            }
            xcode.addAll(xgen("movq", address, s));
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
