public class symtabEntry {
    String sym;

    Address address;
    symtab parentSymbolTable;
    symtab subscopeSymbolTable;
    boolean isConst;
    TypeInfo typeInfo;

    public symtabEntry(String sym, symtab parentSymbolTable, symtab subscopeSymbolTable, boolean isConst, TypeInfo typeInfo, Address address) {
        this.sym = sym;
        this.parentSymbolTable = parentSymbolTable;
        this.subscopeSymbolTable = subscopeSymbolTable;
        this.isConst = isConst;
        this.typeInfo = typeInfo;
        this.address = address;
    }
    public void print(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        System.out.print(sym);
        if (isConst) {
            System.out.print(" (const)");
        }
        System.out.println();
        if (subscopeSymbolTable != null) {
            subscopeSymbolTable.print(level + 1);
        }
    }
}
