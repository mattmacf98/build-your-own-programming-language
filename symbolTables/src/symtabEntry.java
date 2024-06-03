public class symtabEntry {
    String sym;
    symtab parentSymbolTable;
    symtab subscopeSymbolTable;
    boolean isConst;

    public symtabEntry(String sym, symtab parentSymbolTable, symtab subscopeSymbolTable, boolean isConst) {
        this.sym = sym;
        this.parentSymbolTable = parentSymbolTable;
        this.subscopeSymbolTable = subscopeSymbolTable;
        this.isConst = isConst;
    }

    public symtabEntry(String sym, symtab parentSymbolTable, boolean isConst) {
        this.sym = sym;
        this.parentSymbolTable = parentSymbolTable;
        this.isConst = isConst;
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
