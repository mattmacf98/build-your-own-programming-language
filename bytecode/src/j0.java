import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class j0 {
    public static Yylex yylexer;
    public static parser par;
    public static symtab globalSymTab;
    public static symtab stringTab;
    public static HashMap<String,Integer> labeltable;
    public static boolean textOutput;
    public static boolean methodAddrPushed;
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


    private static ArrayList<Bytecode> byteCode(ArrayList<ThreeAddressCode> intermediateCode) {
        ArrayList<Bytecode> returnValue = new ArrayList<>();
        for (int i = 0; i < intermediateCode.size(); i++) {
            ThreeAddressCode instruction = intermediateCode.get(i);
            switch (instruction.opcode) {
                case "ADD":
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandThree));
                    returnValue.addAll(byteCodeGen(Op.ADD, null));
                    returnValue.addAll(byteCodeGen(Op.POP, instruction.operandOne));
                    break;
                case "SUB":
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandThree));
                    returnValue.addAll(byteCodeGen(Op.SUB, null));
                    returnValue.addAll(byteCodeGen(Op.POP, instruction.operandOne));
                    break;
                case "NEG":
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.NEG, null));
                    returnValue.addAll(byteCodeGen(Op.POP, instruction.operandOne));
                    break;
                case "ASN":
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.POP, instruction.operandOne));
                    break;
                case "ADDR":
                    returnValue.addAll(byteCodeGen(Op.LOAD, instruction.operandOne));
                    break;
                case "LCON":
                    returnValue.addAll(byteCodeGen(Op.LOAD, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.POP, instruction.operandOne));
                    break;
                case "SCON":
                    returnValue.addAll(byteCodeGen(Op.STORE, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.POP, instruction.operandOne));
                    break;
                case "GOTO":
                    returnValue.addAll(byteCodeGen(Op.GOTO, instruction.operandOne));
                    break;
                case "BLT":
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandThree));
                    returnValue.addAll(byteCodeGen(Op.LT, null));
                    returnValue.addAll(byteCodeGen(Op.BIF, instruction.operandOne));
                    break;
                case "BGT":
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandThree));
                    returnValue.addAll(byteCodeGen(Op.GT, null));
                    returnValue.addAll(byteCodeGen(Op.BIF, instruction.operandOne));
                    break;
                case "BLE":
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandThree));
                    returnValue.addAll(byteCodeGen(Op.GT, null));
                    returnValue.addAll(byteCodeGen(Op.BNIF, instruction.operandOne));
                    break;
                case "BGE":
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandTwo));
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandThree));
                    returnValue.addAll(byteCodeGen(Op.LT, null));
                    returnValue.addAll(byteCodeGen(Op.BNIF, instruction.operandOne));
                    break;
                case "PARM":
                    if (!methodAddrPushed) {
                        for (int j = i+1; j < intermediateCode.size(); j++) {
                            ThreeAddressCode callInstruction = intermediateCode.get(j);
                            if (callInstruction.opcode.equals("CALL")) {
                                if (callInstruction.operandOne.str().equals("PrintStream__println:0")) {
                                    returnValue.addAll(j0.byteCodeGen(Op.PUSH, new Address("imm", -1)));
                                } else {
                                    returnValue.addAll(j0.byteCodeGen(Op.PUSH, callInstruction.operandTwo));
                                }
                                break;
                            }
                        }
                        methodAddrPushed = false;
                    }
                    returnValue.addAll(byteCodeGen(Op.PUSH, instruction.operandOne));
                    break;
                case "CALL":
                    returnValue.addAll(byteCodeGen(Op.CALL, instruction.operandTwo));
                    methodAddrPushed = false;
                    break;
                case "RET":
                    returnValue.addAll(byteCodeGen(Op.RETURN, instruction.operandOne));
                    break;
                case "LAB":
                    labeltable.put("L" + instruction.operandOne.offset, returnValue.size() * 8);
                    break;
                case "proc":
                    // record address
                    labeltable.put(instruction.operandOne.region, returnValue.size() * 8);
                    break;
                case "end":
                case ".code":
                case ".global":
                case ".string":
                case "string":
                case "global":
                    break;
                default: { j0.stop("What is " + instruction.opcode); }
            }
        }
        return returnValue;
    }

    public static void gencode(parserVal r) {
        tree root = (tree)(r.obj);
        root.generateFirst();
        root.generateFollow();
        root.generateTargets();
        root.generateCode();

        labeltable = new HashMap<>();
        methodAddrPushed = false;
        ArrayList<Bytecode> byteCode = j0.byteCode(root.intermediateCode);
        if (textOutput) {
            for (Bytecode bytecode : byteCode) {
                bytecode.print();
            }
        } else {
            genByteCode(byteCode);
        }
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

    public static int where;
    public static PrintStream open(String s){
        PrintStream p = null;
        where = 0;
        try {
            p = new PrintStream(s);
        } catch (java.io.FileNotFoundException e) {
            stop("couldn't open output file "+ s + " for writing");
        }
        return p;
    }

    public static int calculate_fio() {
        return 3+ stringTab.count/8 + globalSymTab.count/8;
    }

    public static void write_stringarea(PrintStream fout) {
        String s;
        for(int i=0; i<stringTab.L.size(); i++) {
            s = stringTab.L.get(i);
            // should fully-binarize (de-escape) string here
            // for now, just strip double quotes, replace with NULs
            s = s.substring(1,s.length()-1) + "\0\0" ;

            int len = s.length();
            while (len > 0) {
                if (len < 9) {
                    fout.print(s);
                    if (len < 8) {
                        for(int j = 0; j<8-len; j++)
                            fout.print( "\0");
                    }
                }
                else { fout.print(s.substring(0,8)); s = s.substring(8); }
                len -= 8;
            }
        }
    }

    public static void write_globalarea(PrintStream fout){
        for(int i=0; i<globalSymTab.count; i++)
            fout.print("\0");
    }

    public static void genByteCode(ArrayList<Bytecode> bc) {
        int i = find(".java".getBytes(), yyfilename.getBytes());
        int entrypt;
        String outfilename = yyfilename.substring(0,i) + ".j0";
        PrintStream fout;
        fout = open(outfilename);
        fout.print("Jzero!!\0");               // word 0, magic word
        fout.print("1.0\0\0\0\0\0");           // word 1, version #
        // write first instruction offset. convert Java binary to bcode binary
        int fio = calculate_fio();
        fout.print("\0\0" + reverse(rawstring(fio, 6)));
        write_stringarea(fout);
        write_globalarea(fout);

        // bootstrap instructions: push addr of main, push dummy self, call 0, and halt
        entrypt = fio*8+32;
        if (! labeltable.containsKey("main")) stop("main() not found");
        entrypt += labeltable.get("main");
        fout.print("\11\2"+reverse(rawstring(entrypt, 6))); // PUSH IMM (func entry pt) fio*8+24
        fout.print("\11\2\0\0\0\0\0\0");            // PUSH 0 (null self)
        fout.print("\13\2\0\0\0\0\0\0");            // call 0
        fout.print("\1\0\0\0\0\0\0\0");             // halt

        write_instructions(bc, fout);
        fout.close();
    }

    public static ArrayList<Bytecode> byteCodeGen(int opcode, Address address) {
        ArrayList<Bytecode> byteCode = new ArrayList<>();
        byteCode.add(new Bytecode(opcode, address));
        return byteCode;
    }

    public static void semerror(String s) {
        System.out.println("semantic error: " + s);
        System.exit(1);
    }

    public static void write_instructions(ArrayList<Bytecode> bytecode,
                                          PrintStream fout) {
        for (Bytecode b : bytecode) {
            switch (b.opcode) {
//                case Op.CODE: {
//                }
//                case Op.GLOBAL: {
//                }
//                case Op.LABEL: {
//                }
//                case Op.PROC: {
//                }
//                case Op.STRING: {
//                }
//                case Op.END: {
//                    break;
//                }
                default: {
                    b.printb(fout);
                }
            }
        }
    }

    public static void stop(String s) {
        System.err.println(s);
        System.exit(1);
    }

    public static String rawstring(int i, int len) {
        String s = "";
        for(int j=0; j<len; j++) {
            s = Character.toString((char)(i & 0xFF)) + s;
            i = i >> 8;
        }
        return s;
    }
    public static String reverse(String s) {
        return (new StringBuffer(s)).reverse().toString();
    }

    public static void calcType(parserVal parserVal) {
        tree tree = (tree) parserVal.obj;

        tree.kids[0].calcType();
        tree.kids[1].assignType(tree.kids[0].typeInfo);
    }
}
