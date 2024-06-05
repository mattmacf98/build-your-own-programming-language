import java.util.HashMap;

public class symtab {
    String scope;
    symtab parent;
    HashMap<String, symtabEntry> table;

    symtab(String sc) {
        this.scope = sc;
        this.table = new HashMap<String,symtabEntry>();
    }

    public symtab(String scope, symtab parent) {
        this.scope = scope;
        this.parent = parent;
        this.table = new HashMap<String, symtabEntry>();
    }

    public symtabEntry lookup(String sym) {
        symtabEntry result;
        result = table.get(sym);
        if (result != null) {
            return result;
        } else if (parent != null) {
            return parent.lookup(sym);
        } else  {
            return null;
        }
    }

    public void insert(String scope, boolean isConst, symtab sub, TypeInfo typeInfo) {
        if (table.containsKey(scope)) {
            j0.semerror("redeclaration of " + scope);
        } else {
            if (sub != null) {
                sub.parent = this;
            }
            table.put(scope, new symtabEntry(scope, this, sub, isConst, typeInfo));
        }
    }

    public void print() {
        print(0);
    }

    public void print(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(" ");
        }
        System.out.println(scope +  " - " + table.size() + " symbols");
        for (symtabEntry entry : table.values()) {
            entry.print(level + 1);
        }
    }
}
