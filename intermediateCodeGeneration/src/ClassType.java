public class ClassType extends TypeInfo {
    String name;
    symtab symbolTable;
    Parameter[] methods;
    Parameter[] fields;
    TypeInfo[] constructors;

    public ClassType(String name) {
        super("class");
        this.name = name;
    }

    public ClassType(String name, symtab symbolTable) {
        super("class");
        this.name = name;
        this.symbolTable = symbolTable;
    }

    public ClassType(String name, symtab symbolTable, Parameter[] methods, Parameter[] fields, TypeInfo[] constructors) {
        super("class");
        this.name = name;
        this.symbolTable = symbolTable;
        this.methods = methods;
        this.fields = fields;
        this.constructors = constructors;
    }
}
