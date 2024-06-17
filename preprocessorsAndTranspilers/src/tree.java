import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class tree {
    int id;
    String sym;
    int rule;
    int nkids;
    token tok;
    tree[] kids;
    boolean isConst;
    symtab stab;
    TypeInfo typeInfo;

    ArrayList<String> iconCode;
    ArrayList<String> staticCode;
    ArrayList<String> varDecls;

    ArrayList<ThreeAddressCode> intermediateCode;
    Address address;
    Address first;
    Address follow;
    Address onTrue;
    Address onFalse;

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

    public void generateUnicon() {
        if (kids != null) {
            for (tree kid : kids) {
                kid.generateUnicon();
            }
        }

        switch (sym) {
            case "ClassDecl":
                generateUniconClassDecl();
                break;
            case "ClassBodyDecls":
                generateUniconClassBodyDecls();
                break;
            case "CondAndExpr": case "CondOrExpr": case "MulExpr":
            case "AddExpr":
                generateUniconBinaryExpr();
                break;
            case "Assignment":
                generateUniconAssignment();
                break;
            case "EqExpr":
                generateUniconEqExpr();
                break;
            case "RelExpr":
                generateUniconRelExpr();
                break;
            case "UnaryExpr":
                generateUniconUnaryExpr();
                break;
            case "QualifiedName":
                generateUniconQualifiedName();
                break;
            case "token":
                generateUniconToken();
                break;
            case "MethodCall":
                generateUniconMethodCall();
                break;
            case "ArgList":
                generateUniconArgList();
                break;
            case "IfThenStmt":
                generateUniconIfThenStmt();
                break;
            case "LocalVarDecl":
                generateUniconLocalVarDecl();
                break;
            case "VarDeclarator":
                generateUniconVarDeclarator();
                break;
            case "BlockStmts":
                generateUniconBlockStmts();
                break;
            case "Block":
                generateUniconBlock();
                break;
            case "MethodDeclarator":
                generateUniconMethodDeclarator();
                break;
            case "MethodHeader":
                generateUniconMethodHeader();
                break;
            default:
                iconCode = new ArrayList<>();
                if (kids != null) {
                    for (tree kid : kids) {
                        iconCode.addAll(kid.iconCode);
                    }
                }
        }
    }

    private void generateUniconBlock() {
        iconCode = new ArrayList<>();
        iconCode.add("{");
        for (tree kid : kids) {
            iconCode.addAll(kid.varDecls); // move declarations to top of block
        }
        for (tree kid : kids) {
            iconCode.addAll(kid.iconCode);
        }
        iconCode.add("}");
    }

    private void generateUniconBlockStmts() {
        iconCode = new ArrayList<>();
        iconCode.addAll(kids[0].iconCode);
        iconCode.addAll(kids[1].iconCode);

        varDecls = new ArrayList<>();
        if (kids[0].varDecls != null) {
            varDecls.addAll(kids[0].varDecls);
        }
        if (kids[1].varDecls != null) {
            varDecls.addAll(kids[1].varDecls);
        }
    }

    private void generateUniconVarDeclarator() {
        iconCode = kids[0].iconCode;
    }

    private void generateUniconLocalVarDecl() {
        varDecls = new ArrayList<>();
        varDecls.add("local ");
        varDecls.addAll(kids[1].iconCode);
        iconCode = new ArrayList<>();
//        iconCode.addAll(kids[0].iconCode);
//        iconCode.add(",");
//        iconCode.addAll(kids[1].iconCode);
    }

    private void generateUniconClassDecl() {
        // 0th kid or 1??
        iconCode = new ArrayList<>();
        iconCode.add("package " + kids[0].iconCode.get(0));
        if (kids[kids.length - 1].staticCode != null) {
            iconCode.addAll(kids[kids.length - 1].staticCode);
        }
        iconCode.add("class " + kids[0].iconCode.get(0) + " ()");
        iconCode.addAll(kids[kids.length - 1].iconCode);
    }

    private void generateUniconClassBodyDecls() {
        staticCode = new ArrayList<>();
        iconCode = new ArrayList<>();
        staticCode.addAll(kids[0].staticCode);
        staticCode.addAll(kids[1].staticCode);
        iconCode.addAll(kids[0].iconCode);
        iconCode.addAll(kids[1].iconCode);
    }

    private void generateUniconMethodDeclarator() {
        staticCode = new ArrayList<>();
        iconCode = new ArrayList<>();
        if (kids[1].rule == 1070) {
            // public static method
            staticCode.addAll(kids[0].iconCode);
            for (int i = 1; i < kids[1].iconCode.size() - 1; i++) {
                staticCode.add(kids[1].iconCode.get(i));
            }
            staticCode.add("end");
        } else {
            for (tree kid : kids) {
                iconCode.addAll(kid.iconCode);
            }
            iconCode.add("end");
        }
    }

    private void generateUniconMethodHeader() {
        String procedureName = kids[1].kids[0].iconCode.get(0);
        String reservedWord = rule == 1070 ? "procedure" : "method";
        iconCode = new ArrayList<>();
        iconCode.add(reservedWord + procedureName + "(" + kids[1].iconCode.get(0) + ")");
    }

    private void generateUniconEqExpr() {
        String op;
        if (rule == 1340) {
            op = "===";
        } else {
            op = "~===";
        }
        iconCode = new ArrayList<>();
        iconCode.add(kids[0].iconCode.get(0) + " " + op + " " + kids[1].iconCode.get(0));
    }

    private void generateUniconRelExpr() {
        String op = kids[1].tok.text;
        iconCode = new ArrayList<>();
        iconCode.add(kids[0].iconCode.get(0) + " " + op + " " + kids[2].iconCode.get(0));
    }

    private void generateUniconIfThenStmt() {
        iconCode = new ArrayList<>();
        if (kids[1].iconCode.get(0).equals("{")) {
            iconCode.add("if " + kids[0].iconCode.get(0) + " then {");
            for (int i = 1; i < kids[1].iconCode.size(); i++) {
                iconCode.add(kids[1].iconCode.get(i));
            }
        } else {
            iconCode.add("if" + kids[0].iconCode.get(0) + " then");
            iconCode.addAll(kids[1].iconCode);
        }
        varDecls =  kids[1].varDecls;
    }

    private void generateUniconAssignment() {
        String op = kids[1].iconCode.get(0);
        if (op.equals("=")) {
            op = ":=";
        } else {
            op = op.substring(0, op.length() - 1) + ":=";
        }
        iconCode = new ArrayList<>();
        iconCode.add(kids[0].iconCode.get(0) + op + kids[2].iconCode.get(0));
    }

    private void generateUniconArgList() {
        iconCode = new ArrayList<>();
        iconCode.addAll(kids[0].iconCode);
        iconCode.add(",");
        iconCode.addAll(kids[1].iconCode);
    }

    private void generateUniconMethodCall() {
        iconCode = new ArrayList<>();
        if (rule == 1290) {
            iconCode.addAll(kids[0].iconCode);
            iconCode.add("(");
            iconCode.addAll(kids[1].iconCode);
            iconCode.add(")");
        } else {
            //1291
            iconCode.addAll(kids[0].iconCode);
            iconCode.add(kids[1].tok.text);
            iconCode.add("(");
            iconCode.addAll(kids[2].iconCode);
            iconCode.add(")");
        }
    }

    private void generateUniconUnaryExpr() {
        String op = null;
        switch (rule) {
            case 1300:
                op = "-";
                break;
            case 1301:
                op = "~";
                break;
            default:
                System.out.println("unknown rule " + rule);
                System.exit(1);
        }

        iconCode = new ArrayList<>();
        iconCode.add("(" + op + kids[1].iconCode.get(0) + ")");
    }

    private void generateUniconBinaryExpr() {
        String op = null;
        switch (rule) {
            case 1320:
                if (typeInfo.getBaseType().equals("String")) {
                    op = "||";
                } else {
                    op = "+";
                }
                break;
            case 1321:
                op = "-";
                break;
            case 1310:
                op = "*";
                break;
            case 1311:
                op = "/";
                break;
            case 1312:
                op = "%";
                break;
            case 1350:
                op = "&";
                break;
            case 1360:
                op = "|";
                break;
            default:
                System.out.println("unknown rule " + rule);
                System.exit(1);
        }

        iconCode = new ArrayList<>();
        iconCode.add("(" + kids[0].iconCode.get(0) + " " + op + " " + kids[1].iconCode.get(0) + ")");
    }

    private void generateUniconQualifiedName() {
        if (kids[1].iconCode.get(0).equals("println") && kids[0].kids[1].iconCode.get(0).equals("out")
                && kids[0].kids[0].sym.equals("token") && kids[0].kids[0].iconCode.get(0).equals("System")) {
            iconCode = new ArrayList<>();
            iconCode.add("write");
        } else {
            switch (kids[0].typeInfo.getBaseType()) {
                case "array":
                    if (kids[1].tok.text.equals("length")) {
                        iconCode = new ArrayList<>();
                        iconCode.add("*" + kids[0].iconCode.get(0));
                    } else {
                        System.out.println("don't know how to generate code for array." + kids[1].tok.text);
                        System.exit(1);
                    }
                    break;
                case "class":
                    iconCode = new ArrayList<>();
                    iconCode.add(kids[0].iconCode.get(0) + "." + kids[1].iconCode.get(0));
                    break;
                default:
                    System.out.println("don't know how to generate code for type " + kids[0].typeInfo.getBaseType());
            }
        }
    }

    private void generateUniconToken() {
        iconCode = new ArrayList<>();
        iconCode.add(tok.text);
    }

    public void calcType() {
        for (int i = 0; i < nkids; i++) {
            kids[i].calcType();
        }
        switch (sym) {
            case "FieldDecl":
                typeInfo = kids[0].typeInfo;
                return;
            case "token":
                if ((typeInfo = tok.typeInfo) != null) {
                    return;
                }
                switch (tok.cat) {
                    case parser.IDENTIFIER:
                        typeInfo = new ClassType(tok.text);
                        return;
                    case parser.INT:
                        typeInfo = new TypeInfo(tok.text);
                        return;
                    default:
                        j0.semerror("don't know the type of " + tok.text);
                }
            default:
                j0.semerror("don't know the type of " + sym);
        }
    }

    public void assignType(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
        switch (sym) {
            case "VarDeclarator":
                kids[0].assignType(new ArrayType(typeInfo));
                return;
            case "MethodDeclarator":
                TypeInfo[] paramList;
                if (kids[1] != null) {
                    paramList = kids[1].makeSignature();
                } else {
                    paramList = new TypeInfo[0];
                }

                this.typeInfo = new MethodType(paramList, typeInfo);
                kids[0].typeInfo = this.typeInfo;
                return;
            case "token":
                if (tok.cat != parser.IDENTIFIER) {
                    j0.semerror("eh?" + tok.cat);
                } else {
                    return;
                }
            default:
                j0.semerror("don't know how to assign type " + sym);
        }
        for (tree kid : kids) {
            kid.assignType(typeInfo);
        }
    }

    private TypeInfo[] makeSignature() {
        switch (sym) {
            case "FormalParm":
                return new TypeInfo[]{kids[0].typeInfo};
            case "FormalParmList":
                TypeInfo[] typeInfo1 = kids[0].makeSignature();
                TypeInfo[] typeInfo2 = kids[1].makeSignature();
                TypeInfo[] typeInfos = new TypeInfo[typeInfo1.length + typeInfo2.length];

                System.arraycopy(typeInfo1, 0, typeInfos, 0, typeInfo1.length);
                System.arraycopy(typeInfo2, 0, typeInfos, typeInfo1.length, typeInfo2.length);
                return typeInfos;
            default:
                return null;
        }
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

    public void makeClass() {

        if (sym.equals("ClassDecl")) {

            // counts number of fields and methods this class has
            int numMethods = 0;
            int numFields = 0;
            symtabEntry resultEntry = stab.lookup(kids[0].tok.text);
            for (String key : resultEntry.subscopeSymbolTable.table.keySet()) {
                symtabEntry symtabEntry = resultEntry.subscopeSymbolTable.table.get(key);
                if (symtabEntry.typeInfo.getBaseType().startsWith("method ")) {
                    numMethods++;
                } else {
                    numFields++;
                }
            }

            //populates our fields and methods
            Parameter fields[] = new Parameter[numFields];
            Parameter methods[] = new Parameter[numMethods];
            int methodCur = 0;
            int fieldCur = 0;
            for (String key : resultEntry.subscopeSymbolTable.table.keySet()) {
                symtabEntry symtabEntry = resultEntry.subscopeSymbolTable.table.get(key);
                if (symtabEntry.typeInfo.getBaseType().startsWith("method ")) {
                    methods[methodCur++] = new Parameter(key, symtabEntry.typeInfo);
                } else {
                    fields[fieldCur++] = new Parameter(key, symtabEntry.typeInfo);
                }
            }

            resultEntry.typeInfo = new ClassType(kids[0].tok.text, resultEntry.subscopeSymbolTable, fields, methods, new TypeInfo[0]);
        } else {
            for (int i = 0; i < nkids; i++) {
                if (kids[i].nkids > 0) {
                    kids[i].makeClass();
                }
            }
        }
    }

    public void populateSymbolTables() {
        switch (sym) {
            case "ClassDecl":
                stab.insert(kids[0].tok.text, false, kids[0].stab, new ClassType(kids[0].tok.text));
                break;
            case "FieldDecl":
            case "LocalVarDecl":
                tree kid = kids[1];
                while (kid != null && kid.sym.equals("VarDecls")) {
                    insertVarDeclarator(kid.kids[1]);
                    kid = kids[0];
                }
                if (kid != null) {
                    insertVarDeclarator(kid);
                }
                return;
            case "MethodDecl":
                stab.insert(kids[0].kids[1].kids[0].tok.text, false, kids[0].stab, kids[0].kids[1].typeInfo);
                kids[0].stab.insert("return", false, null, kids[0].kids[0].typeInfo);
                break;
            case "FormalParm":
                insertVarDeclarator(kids[1]);
                break;
        }

        for (int i = 0; i < nkids; i++) {
            tree kid = kids[i];
            kid.populateSymbolTables();
        }
    }

    private void insertVarDeclarator(tree varDeclarator) {
        if (varDeclarator.tok != null) {
            stab.insert(varDeclarator.tok.text, false, null, varDeclarator.typeInfo);
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

    public void checkType(boolean inCodeBlock) {
        if (checkKids(inCodeBlock) || !inCodeBlock) {
            return;
        }

        switch (sym) {
            case "Assignment": case "RelExpr":
                typeInfo = checkTypes(kids[0].typeInfo, kids[2].typeInfo);
                break;
            case "AddExpr":
                typeInfo = checkTypes(kids[0].typeInfo, kids[1].typeInfo);
                break;
            case "ArgList":
            case "Block":
            case "BlockStmts":
                typeInfo = null;
                break;
            case "MethodCall":
                if (rule == 1290) {
                    symtabEntry symtabEntry;
                    MethodType methodType;
                    if (kids[0].sym.equals("QualifiedName")) {
                        methodType = (MethodType) kids[0].dequalify();
                        checkSignature(methodType);
                    } else {
                        if (!kids[0].sym.equals("token")) {
                            j0.semerror("can't check type of " + kids[0].sym);
                        }
                        if (kids[0].tok.cat == parser.IDENTIFIER) {
                            symtabEntry = stab.lookup(kids[0].tok.text);
                            if (symtabEntry != null) {
                                if (!(symtabEntry.typeInfo instanceof MethodType)) {
                                    j0.semerror("method expected, got " + symtabEntry.typeInfo.getBaseType());
                                }
                                methodType = (MethodType) symtabEntry.typeInfo;
                                checkSignature(methodType);
                            }
                        } else {
                            j0.semerror("can't typecheck token " + kids[0].tok.cat);
                        }
                    }
                }
                break;
            case "QualifiedName":
                if (kids[0].typeInfo instanceof ClassType) {
                    ClassType classType = (ClassType) (kids[0].typeInfo);
                    typeInfo = (classType.symbolTable.lookup(kids[1].tok.text)).typeInfo;
                } else if (kids[0].typeInfo instanceof ArrayType) {
                    typeInfo = new TypeInfo("int");
                } else {
                    j0.semerror("illegal . on " + kids[0].typeInfo.getBaseType());
                }
                break;
            case "ArrayCreation":
                typeInfo = new ArrayType(kids[0].typeInfo);
                break;
            case "ArrayAccess":
                if (kids[0].typeInfo.getBaseType().startsWith("array ")) {
                    if (kids[1].typeInfo.getBaseType().equals("int")) {
                        typeInfo = ((ArrayType)(kids[0].typeInfo)).elementType;
                    } else {
                        j0.semerror("subscripting array with " + kids[1].typeInfo.getBaseType());
                    }
                }
                break;
            case "ReturnStmt":
                symtabEntry symtabEntry = stab.lookup("return");
                if (symtabEntry == null) {
                    j0.semerror("stab did not find a return type");
                }
                TypeInfo returnType = symtabEntry.typeInfo;
                if (kids[0].typeInfo != null) {
                    typeInfo = checkTypes(returnType, kids[0].typeInfo);
                } else if (!returnType.getBaseType().equals("void")) {
                    j0.semerror("void return from non-void method");
                }
                break;
            case "InstanceCreation":
                symtabEntry resultValue = stab.lookup(kids[0].tok.text);
                if (resultValue == null) {
                    j0.semerror("unknown type " + kids[0].tok.text);
                }
                assert resultValue != null;
                typeInfo = resultValue.typeInfo;
                if (typeInfo == null) {
                    j0.semerror(kids[0].tok.text + " has unknown type");
                }
                break;
            case "WhileStmt": case "IfThenStmt":
                break;
            case "token":
                typeInfo = tok.type(stab);
                break;
            default:
                j0.semerror("cannot check type of " + sym);
        }
    }

    private TypeInfo dequalify() {
        TypeInfo classType = null;
        symtabEntry symtabEntry;
        //recursive swim down to find token and type info
        if (kids[0].sym.equals("QualifiedName")) {
            classType = kids[0].dequalify();
        } else if (kids[0].sym.equals("token") && kids[0].tok.cat == parser.IDENTIFIER) {
            symtabEntry = stab.lookup(kids[0].tok.text);
            if (symtabEntry == null) {
                j0.semerror("unknown symbol " + kids[0].tok.text);
            } else {
                classType = symtabEntry.typeInfo;
            }
        } else {
            j0.semerror("can't dequalify " + sym);
        }

        assert classType != null;
        if (!classType.getBaseType().equals("class")) {
            j0.semerror("can't dequalify " + classType.getBaseType());
        }

        // get type of the accessor on class
        symtabEntry = ((ClassType) classType).symbolTable.lookup(kids[1].tok.text);
        if (symtabEntry != null) {
            return symtabEntry.typeInfo;
        } else {
            j0.semerror("couldn't lookup " + kids[1].tok.text + " in " + classType.getBaseType());
            return null;
        }
    }

    private void checkSignature(MethodType signature) {
        int expectedNumberOfParams = signature.parameters.length;
        int actualNUmberOfParams = 1;
        tree kid = kids[1];
        if (kid == null && expectedNumberOfParams != 0) {
            j0.semerror("0 params, expected " + expectedNumberOfParams);
        } else if (kid != null) {
            while (kid.sym.equals("ArgList")) {
                actualNUmberOfParams++;
                kid = kid.kids[0];
            }

            if (actualNUmberOfParams != expectedNumberOfParams) {
                j0.semerror(actualNUmberOfParams + " parameters, expected " + expectedNumberOfParams);
            }

            kid = kids[1];
            int paramCursor = expectedNumberOfParams - 1;
            while (kid.sym.equals("ArgList")) {
                checkTypes(kid.kids[1].typeInfo, signature.parameters[paramCursor]);
                kid = kid.kids[0];
                paramCursor--;
            }
            checkTypes(kid.typeInfo, signature.parameters[0]);
        }

        typeInfo = signature.returnType;
    }

    private boolean checkKids(boolean inCodeBlock) {
        switch (sym) {
            case "MethodDecl":
                kids[1].checkType(true);
                return true;
            case "LocalVarDecl":
                kids[1].checkType(false);
                return true;
            case "FieldAccess":
                kids[0].checkType(inCodeBlock);
                return true;
            case "QualifiedName":
                kids[0].checkType(inCodeBlock);
                return false;
            case "RelExpr":
                kids[0].checkType(inCodeBlock);
                kids[2].checkType(inCodeBlock);
                return false;
            default:
                if (kids != null) {
                    for (tree kid : kids) {
                        kid.checkType(inCodeBlock);
                    }
                }
                return false;
        }
    }

    private TypeInfo checkTypes(TypeInfo operand1, TypeInfo operand2) {
        String operator = getOp();
        if (operand1 instanceof MethodType) {
            operand1 = ((MethodType) operand1).returnType;
        }
        if (operand2 instanceof MethodType) {
            operand2 = ((MethodType) operand2).returnType;
        }
        switch (operator) {
            case "=":
            case "+":
            case "-":
            case "param":
            case "return":
                tree tokenKid;
                if ((tokenKid = findToken()) != null) {
                    System.out.print("line " + tokenKid.tok.lineno + ": ");
                }
                if (operand1.getBaseType().equals(operand2.getBaseType()) && ( operand1.getBaseType().equals("int") || operand1.getBaseType().equals("String"))) {
                    System.out.println("typecheck " + operator + " on a " + operand2.getBaseType() + " and a " + operand1.getBaseType() +  " -> OK");
                    return operand1;
                } else if (operand1.getBaseType().equals("class") && operand2.getBaseType().equals("class") && operator.equals("param") &&
                        ((ClassType)operand1).name.equals(((ClassType)operand2).name)) {
                    System.out.println("typecheck " + operator + " on a " + ((ClassType) operand2).name + " and a " + ((ClassType) operand1).name +  " -> OK");
                    return operand1;
                } else if (operand1.getBaseType().equals("array") && operand2.getBaseType().equals("array") && operator.equals("=") &&
                        checkTypes(((ArrayType)(operand1)).elementType, ((ArrayType)(operand2)).elementType) != null) {
                        return operand1;
                } else {
                    j0.semerror("typecheck " + operator + " on a " + operand2.getBaseType() + " and a " + operand1.getBaseType() +  " -> FAIL");
                }
            case "<": case ">":
                if (operand1.getBaseType().equals(operand2.getBaseType()) && (operand1.getBaseType().equals("int") || operand1.getBaseType().equals("double"))) {
                    return new TypeInfo("bool");
                }
            default:
                j0.semerror("don't know how to check " + operator);
                return null;
        }
    }

    private tree findToken() {
        if (sym.equals("token")) {
            return this;
        } else {
            for (tree kid : kids) {
                tree token = kid.findToken();
                if (token != null) {
                    return token;
                }
            }
        }
        return null;
    }

    private String getOp() {
        switch (sym) {
            case "Assignment":
                return "=";
            case "AddExpr":
                return rule == 1320 ? "+" : "-";
            case "MethodCall":
                return "param";
            case "ReturnStmt":
                return "return";
            case "RelExpr": {
                if (kids[1].sym.equals("token")) {
                    return kids[1].tok.text;
                }
            }
            default:
                return sym;
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

    public ArrayList<ThreeAddressCode> generate(String opcode, Address... addresses) {
        switch (addresses.length) {
            case 3:
                return new ArrayList<>(List.of(new ThreeAddressCode(opcode, addresses[0], addresses[1], addresses[2])));
            case 2:
                return new ArrayList<>(List.of(new ThreeAddressCode(opcode, addresses[0], addresses[1], null)));
            case 1:
                return new ArrayList<>(List.of(new ThreeAddressCode(opcode, addresses[0], null, null)));
            case 0:
                return new ArrayList<>(List.of(new ThreeAddressCode(opcode, null, null, null)));
            default:
                j0.semerror("generate(): wrong number of arguments");
                return new ArrayList<>();
        }
    }

    public Address generateLabel() {
        return new Address("lab", serial.getid());
    }

    public void generateFirst() {
        if (kids != null) {
            for (tree kid : kids) {
                kid.generateFirst();
            }
        }

        switch (sym) {
            case "UnaryExpr":
                if (kids[1].first != null) {
                    first = kids[1].first;
                } else {
                    first = generateLabel();
                }
                break;
            case "AddExpr":
            case "MulExpr":
            case "RelExpr":
                if (kids[0].first != null) {
                    first = kids[0].first;
                } else if (kids[1].first != null) {
                    first = kids[1].first;
                }else {
                    first = generateLabel();
                }
                break;
            case "Block": case "WhileStmt":
                if (kids[0].first != null) {
                    first = kids[0].first;
                } else {
                    first = generateLabel();
                }
                break;
            case "BlockStmts":
                if (kids[1].first == null) {
                    kids[1].first = generateLabel();
                }
                if (kids[0].first != null) {
                    first = kids[0].first;
                } else {
                    first = kids[1].first;
                }
                break;
            default:
                if (kids != null) {
                    for(tree k : kids) {
                        if (k.first != null) {
                            first = k.first;
                            break;
                        }
                    }
                }
        }
    }

    public void generateFollow() {
        switch (sym) {
            case "MethodDecl":
                kids[1].follow = follow = generateLabel();
                break;
            case "BlockStmts":
                kids[0].follow = kids[1].first;
                kids[1].follow = follow;
                break;
            case "Block":
                kids[0].follow = follow;
        }

        if (kids != null) {
            for (tree kid : kids) {
                kid.generateFollow();
            }
        }
    }

    public void generateTargets() {
        switch (sym) {
            case "IfThenStmt": case "WhileStmt":
                kids[0].onTrue = kids[1].first;
                kids[0].onFalse = follow;
                break;
            case "CondAndExpr":
                kids[0].onTrue = kids[1].first;
                kids[0].onFalse = onFalse;
                kids[1].onTrue = onTrue;
                kids[1].onFalse = onFalse;
                break;
        }

        if (kids != null) {
            for (tree kid : kids) {
                kid.generateTargets();
            }
        }
    }

    public void generateCode() {
        if (kids != null) {
            for (tree kid : kids) {
                kid.generateCode();
            }
        }

        switch (sym) {
            case "ClassDecl": { generateClassDecl(); break; }
            case "AddExpr": { generateAddExpr(); break; }
            case "RelExpr": { generateRelExpr(); break; }
            case "WhileStmt": { generateWhileStmt(); break; }
            case "MethodCall": { generateMethodCall(); break; }
            case "Assignment": { generateAssignment(); break; }
            case "MethodDecl": { generateMethodDecl(); break; }
            case "QualifiedName": { generateQualifiedName(); break; }
            case "token": { generateToken(); break; }
            default:
                intermediateCode = new ArrayList<>();
                if (kids != null) {
                    for (tree kid : kids) {
                        intermediateCode.addAll(kid.intermediateCode);
                    }
                }
        }
    }

    private void generateClassDecl() {
        int firstGlobal = 0;
        intermediateCode = new ArrayList<>();
        // emit string constants
        if (j0.stringTab.table.size() > 0) {
            intermediateCode.addAll(generate(".string"));
            for (String key : j0.stringTab.table.keySet()) {
                symtabEntry ste = j0.stringTab.table.get(key);
                if (ste.address == null) {
                    System.err.println("null label in stringtab");
                    System.exit(1);
                }

                intermediateCode.addAll(generate("LAB", ste.address));
                intermediateCode.addAll(generate("string", new Address(key, 0)));
            }
        }


        // emit globals
        if (j0.globalSymTab.table.size() > 0) {
            for (String key : j0.globalSymTab.table.keySet()) {
                symtabEntry ste = j0.globalSymTab.table.get(key);
                if (firstGlobal == 0) {
                    intermediateCode.addAll(generate(".global", ste.address));
                    firstGlobal = 1;
                }

                intermediateCode.addAll(generate("global", ste.address, new Address(key, 0)));
            }
        }

        intermediateCode.addAll(generate(".code"));
        if (kids != null) {
            for (tree kid : kids) {
                intermediateCode.addAll(kid.intermediateCode);
            }
        }
    }

    private void generateMethodDecl() {
        intermediateCode = new ArrayList<>();
        intermediateCode.addAll(generate("proc", new Address(kids[0].kids[1].kids[0].tok.text, 0)));
        for (tree kid : kids[1].kids) {
            intermediateCode.addAll(kid.intermediateCode);
        }
        intermediateCode.addAll(generate("LAB", follow));
        intermediateCode.addAll(generate("RET"));
        intermediateCode.addAll(generate("end"));
    }

    private void generateQualifiedName() {
        intermediateCode = new ArrayList<>();
        intermediateCode.addAll(kids[0].intermediateCode);
        if (typeInfo != null && typeInfo instanceof MethodType) {
            //no code compile time method resolution
            address = new Address(kids[0].typeInfo.getBaseType() + "__" + kids[1].tok.text, 0);
        } else {
            address = generateLocal();
            if (kids[0].typeInfo instanceof ArrayType) {
                // array.length
                intermediateCode.addAll(generate("ASIZE", address, kids[0].address));
            } else if (kids[0].typeInfo != null && kids[0].typeInfo.getBaseType().equals("class")) {
                ClassType classType = (ClassType) kids[0].typeInfo;
                symtabEntry ste = stab.lookup(kids[1].tok.text);
                if (ste == null || classType == null) {
                    return;
                }

                //lookup address within class
                if (kids[1].address == null) {
                    kids[1].address = ste.address;
                }
                intermediateCode.addAll(generate("FIELD", address, kids[0].address, kids[1].address));

            }
        }
    }

    private void generateAssignment() {
        address = kids[0].address;
        intermediateCode = new ArrayList<>();
        intermediateCode.addAll(kids[0].intermediateCode);
        intermediateCode.addAll(kids[2].intermediateCode);
        intermediateCode.addAll(generate("ASN", address, kids[2].address));
    }

    private void generateToken() {
        intermediateCode = new ArrayList<>();
        switch (tok.cat) {
            case parser.IDENTIFIER:
                symtabEntry ste = stab.lookup(tok.text);
                if (ste != null) {
                    address = ste.address;
                }
                break;
            case parser.INTLIT:
                address = new Address("imm", Integer.parseInt(tok.text));
                break;
            case parser.STRINGLIT:
                j0.stringTab.insert(tok.text, true, null, new TypeInfo("string"));
                address = j0.stringTab.lookup(tok.text).address;
                break;
        }
    }

    private void generateAddExpr() {
        address = generateLocal();
        intermediateCode = new ArrayList<>();
        intermediateCode.addAll(kids[0].intermediateCode);
        intermediateCode.addAll(kids[1].intermediateCode);
        String operation = rule == 1320 ? "ADD" : "SUB";
        intermediateCode.addAll(generate(operation, address, kids[0].address, kids[1].address));
    }

    private void generateRelExpr() {
        String operation = "";
        switch (kids[1].tok.cat) {
            case '<':
                operation = "BLT";
                break;
            case '>':
                operation = "BGT";
                break;
            case parser.LESSTHANOREQUAL:
                operation = "BLE";
                break;
            case parser.GREATERTHANOREQUAL:
                operation = "BGE";
                break;
        }

        intermediateCode = new ArrayList<>();
        intermediateCode.addAll(kids[0].intermediateCode);
        intermediateCode.addAll(kids[2].intermediateCode);
        intermediateCode.addAll(generate(operation, onTrue, kids[0].address, kids[2].address));
        intermediateCode.addAll(generate("GOTO", onFalse));
    }

    private void generateWhileStmt() {
        intermediateCode = new ArrayList<>();
        intermediateCode.addAll(generate("LAB", kids[0].first));
        intermediateCode.addAll(kids[0].intermediateCode);
        intermediateCode.addAll(generate("LAB", kids[0].onTrue));
        intermediateCode.addAll(kids[1].intermediateCode);
        intermediateCode.addAll(generate("GOTO", kids[0].first));
    }

    private void generateMethodCall() {
        int numParams = 0;
        intermediateCode = new ArrayList<>();
        if (kids[1] != null) {
            intermediateCode.addAll(kids[1].intermediateCode);
            tree kid = kids[1];
            while (kid.sym.equals("ArgList")) {
                intermediateCode.addAll(generate("PARM", kid.kids[1].address));
                numParams++;
                kid = kid.kids[0];
            }
            intermediateCode.addAll(generate("PARM", kid.address));
            numParams++;
        }

        if (kids[0].sym.equals("QualifiedName")) {
            intermediateCode.addAll(kids[0].intermediateCode);
            intermediateCode.addAll(generate("PARM", kids[0].kids[0].address));
        } else {
            intermediateCode.addAll(generate("PARM", new Address("self", 0)));
        }

        intermediateCode.addAll(generate("CALL", kids[0].address, new Address("imm", numParams)));
    }

    private Address generateLocal() {
        return stab.generateLocal();
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
