public class token {
    public int id;
    public int cat;
    public String text;
    public int lineno;
    public TypeInfo typeInfo;
    public token(int c, String s, int l) {
        cat = c; text = s; lineno = l;
        id = serial.getid();

        switch (cat) {
            case parser.INT:
                typeInfo = new TypeInfo("int");
                break;
            case parser.DOUBLE:
                typeInfo = new TypeInfo("double");
                break;
            case parser.BOOL:
                typeInfo = new TypeInfo("boolean");
                break;
            case parser.VOID:
                typeInfo = new TypeInfo("void");
                break;
            case parser.INTLIT:
                typeInfo = new TypeInfo("int");
                break;
            case parser.DOUBLELIT:
                typeInfo = new TypeInfo("double");
                break;
            case parser.STRINGLIT:
                typeInfo = new ClassType("String");
                break;
            case parser.BOOLLIT:
                typeInfo = new TypeInfo("boolean");
                break;
            case parser.NULLVAL:
                typeInfo = new TypeInfo("null");
                break;
            case '=':
            case '+':
            case '-':
                typeInfo = new TypeInfo("n/a");
                break;
        }
    }

    public TypeInfo type(symtab symbolTable) {
        symtabEntry result;
        if (typeInfo != null) {
            return typeInfo;
        } else if (cat == parser.IDENTIFIER && (result = symbolTable.lookup(text)) != null) {
            typeInfo = result.typeInfo;
            return typeInfo;
        } else {
            System.out.println(symbolTable.table.keySet());
            j0.semerror("cannot check the type of " + text + " " + cat);
            return null;
        }
    }
}
